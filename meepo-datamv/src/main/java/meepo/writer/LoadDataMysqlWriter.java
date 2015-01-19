package meepo.writer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class LoadDataMysqlWriter extends IWorker {

    private final IStorage<Object[]> buffer;

    private final Config             config;

    private final DataSource         target;

    private String                   SQL;

    public LoadDataMysqlWriter(IStorage<Object[]> buffer, Config config, DataSource target) {
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
        return "LOAD DATA LOCAL INFILE 'sql.csv' INTO TABLE " + config.getTargetTableName() + " FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' ";
    }

    private void executeWrite() {
        final List<Object[]> datas = buffer.get(config.getWriterStepSize());
        if (datas.isEmpty())
            return;
        BasicDao.excuteLoadData(target, SQL, getDataInputStream(datas));
    }

    public static InputStream getDataInputStream(List<Object[]> datas) {
        StringBuilder builder = new StringBuilder();
        for (Object[] o : datas) {
            for (int i = 0; i < o.length; i++) {
                builder.append(o[i]);
                builder.append(i == o.length - 1 ? "\n" : "\t");
            }
        }
        byte[] bytes = builder.toString().getBytes();
        InputStream is = new ByteArrayInputStream(bytes);
        return is;
    }
}
