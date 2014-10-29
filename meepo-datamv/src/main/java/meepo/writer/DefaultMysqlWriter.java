package meepo.writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import meepo.Config;
import meepo.storage.IStorage;

public class DefaultMysqlWriter implements Runnable {

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
    public void run() {
        while (true) {
            executeWrite(getDatas());
        }
    }

    private String buildSQL() {
        String v = "?";
        for (int i = 1; i < config.getTargetColumsArray().size(); i++) {
            v = v + ",?";
        }
        return "INSERT INTO " + config.getTargetTableName() + " (" + config.getTargetColumsNames() + ") VALUES ( " + v + ")";
    }

    private List<Object[]> getDatas() {
        return buffer.get(config.getWriterStepSize());
    }

    private void executeWrite(List<Object[]> datas) {
        if (datas.isEmpty())
            return;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = target.getConnection();
            c.setAutoCommit(false);
            p = c.prepareStatement(SQL);
            for (Object[] data : datas) {
                for (int i = 0; i < data.length; i++) {
                    p.setObject(i + 1, data[i], config.getTargetColumsType().get(config.getTargetColumsArray().get(i)));
                }
                p.addBatch();
            }
            p.executeBatch();
            c.commit();
        } catch (Exception e) {
            // TODO LOG
            System.out.println(e);
        } finally {
            try {
                if (p != null)
                    p.close();
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                // TODO LOG
            }
        }
    }
}
