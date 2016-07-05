package meepo.reader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class DefaultMysqlReader extends IWorker {

    protected long currentPos = 0;

    public DefaultMysqlReader(IStorage<Object[]> buffer, Config config, int index) {
        super(buffer, config, index);
        long vStart = config.getStart() - (config.getStart() % config.getReaderStepSize());
        this.currentPos = Math.max(vStart + index * config.getReaderStepSize(), config.getStart());
    }

    @Override public void work() {
        if (currentPos >= config.getEnd()) {
            RUN = false;
            return;
        }
        boolean status = executeQuery(currentPos, Math.min(currentPos + config.getReaderStepSize(), config.getEnd()));
        if (status) {
            currentPos += config.getReadersNum() * config.getReaderStepSize();
        }
    }

    @Override protected String buildSQL() {
        return "SELECT " + config.getSourceColumnsNames() + " FROM " + config.getSourceTableName() + " WHERE " + config.getPrimaryKeyName() + " > ? AND " + config
                .getPrimaryKeyName() + " <= ? " + config.getSourceExtraSQL();
    }

    protected boolean executeQuery(final long start, final long end) {
        Boolean result = BasicDao.excuteQuery(config.getSourceDataSource(), SQL, new ICallable<Boolean>() {
            @Override public void handleParams(PreparedStatement p) throws Exception {
                p.setLong(1, start);
                p.setLong(2, end);
            }

            @Override public Boolean handleResultSet(ResultSet r) throws Exception {
                while (r.next()) {
                    Object[] item = new Object[config.getSourceColumnsArray().size()];
                    for (int i = 1; i <= config.getSourceColumnsArray().size(); i++) {
                        item[i - 1] = r.getObject(i);
                    }
                    buffer.add(item);
                }
                return true;
            }
        });
        return (result != null && result);
    }
}
