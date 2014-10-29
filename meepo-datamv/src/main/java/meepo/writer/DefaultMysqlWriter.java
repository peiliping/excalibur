package meepo.writer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class DefaultMysqlWriter extends IWorker {

    private final IStorage<Object[]> buffer;

    private final Config             config;

    private final DataSource         target;

    private String                   SQL;

    public DefaultMysqlWriter(IStorage<Object[]> buffer, Config config, DataSource target) {
        this.buffer = buffer;
        this.config = config;
        this.target = target;
        this.SQL = buildSQL();
    }

    @Override
    public void work() {
        executeWrite();
    }

    private String buildSQL() {
        String v = "?";
        for (int i = 1; i < config.getTargetColumsArray().size(); i++) {
            v = v + ",?";
        }
        return "INSERT INTO " + config.getTargetTableName() + " (" + config.getTargetColumsNames() + ") VALUES ( " + v + ")";
    }

    private void executeWrite() {
        final List<Object[]> datas = buffer.get(config.getWriterStepSize());
        if (datas.isEmpty())
            return;
        BasicDao.excuteBatchAdd(target, SQL, new ICallable<Object>() {
            @Override
            public Object handleResultSet(ResultSet r) throws Exception {
                return null;
            }

            @Override
            public void handleParams(PreparedStatement p) throws Exception {
                for (Object[] data : datas) {
                    for (int i = 0; i < data.length; i++) {
                        p.setObject(i + 1, data[i], config.getTargetColumsType().get(config.getTargetColumsArray().get(i)));
                    }
                    p.addBatch();
                }
            }
        });
    }
}
