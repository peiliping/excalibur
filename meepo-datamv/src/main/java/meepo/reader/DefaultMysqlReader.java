package meepo.reader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class DefaultMysqlReader extends IWorker {

    private final IStorage<Object[]> buffer;

    private final Config             config;

    private final DataSource         source;

    private volatile long            currentPos = 0;

    private String                   Q_SQL;

    public DefaultMysqlReader(IStorage<Object[]> buffer, Config config, DataSource source) {
        this.buffer = buffer;
        this.config = config;
        this.source = source;
        this.Q_SQL = buildSQL();
    }

    @Override
    public void work() {
        if (currentPos >= config.getEnd().get()) {
            run = false;
            return;
        }
        executeQuery();
        updateCurrentPos();
    }

    private void updateCurrentPos() {
        long l = Math.max(config.getStart().get(), currentPos);
        currentPos = l + config.getReaderStepSize();
    }

    private String buildSQL() {
        return "select " + config.getSourceColumsNames() + " from " + config.getSourceTableName() + " where " + config.getPrimaryKeyName() + " > ? and "
                + config.getPrimaryKeyName() + " <= ? ";
    }

    private void executeQuery() {
        BasicDao.excuteQuery(source, Q_SQL, new ICallable<Object>() {
            @Override
            public void handleParams(PreparedStatement p) throws Exception {
                p.setLong(1, currentPos);
                p.setLong(2, Math.min(currentPos + config.getReaderStepSize(), config.getEnd().get()));
            }

            @Override
            public Object handleResultSet(ResultSet r) throws Exception {
                while (r.next()) {
                    Object[] item = new Object[config.getSourceColumsArray().size()];
                    for (int i = 1; i <= config.getSourceColumsArray().size(); i++) {
                        item[i - 1] = r.getObject(i);
                    }
                    buffer.add(item);
                }
                return null;
            }
        });
    }
}
