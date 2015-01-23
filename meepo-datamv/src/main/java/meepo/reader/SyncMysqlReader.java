package meepo.reader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.Pair;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class SyncMysqlReader extends IWorker {

    private final IStorage<Object[]> buffer;

    private final Config             config;

    private final DataSource         source;

    private volatile long            currentPos = 0;

    private String                   SQL;

    private volatile boolean         skip       = false;

    private long                     startTime  = System.currentTimeMillis();

    public SyncMysqlReader(IStorage<Object[]> buffer, Config config, DataSource source) {
        this.buffer = buffer;
        this.config = config;
        this.source = source;
        this.SQL = buildSQL();
        this.currentPos = config.getStart().get();
    }

    @Override
    public void work() {
        Pair<Long, Long> p = BasicDao.autoGetStartEndPoint(source, config.getSourceTableName(), config.getPrimaryKeyName());
        long tmpend = p.getRight();
        while (currentPos < tmpend) {
            if (tmpend - currentPos >= config.getReaderStepSize()) {
                executeQuery(currentPos, currentPos + config.getReaderStepSize());
                if (skip) {
                    skip = false;
                } else {
                    currentPos = currentPos + config.getReaderStepSize();
                }
            } else {
                executeQuery(currentPos, tmpend);
                if (skip) {
                    skip = false;
                } else {
                    currentPos = tmpend;
                }
            }
        }
        try {
            Thread.sleep(config.getSyncDelay());
            if (config.isSyncSuicide() && System.currentTimeMillis() - startTime > 90000000) {
                super.run = false;
            }
        } catch (InterruptedException e) {
        }

    }

    private String buildSQL() {
        return "select " + config.getSourceColumsNames() + " from " + config.getSourceTableName() + " where " + config.getPrimaryKeyName() + " > ? and "
                + config.getPrimaryKeyName() + " <= ? ";
    }

    private void executeQuery(final long start, final long end) {
        Boolean r = BasicDao.excuteQuery(source, SQL, new ICallable<Boolean>() {
            @Override
            public void handleParams(PreparedStatement p) throws Exception {
                p.setLong(1, start);
                p.setLong(2, end);
            }

            @Override
            public Boolean handleResultSet(ResultSet r) throws Exception {
                while (r.next()) {
                    Object[] item = new Object[config.getSourceColumsArray().size()];
                    for (int i = 1; i <= config.getSourceColumsArray().size(); i++) {
                        item[i - 1] = r.getObject(i);
                    }
                    buffer.add(item);
                }
                return true;
            }
        });
        if (r == null) {
            skip = true;
        }
    }
}
