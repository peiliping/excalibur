package meepo.reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import meepo.Config;
import meepo.tools.RingBuffer;
import meepo.tools.RingBuffer.Mode;

public class DefaultMysqlReader implements Runnable {

    private final RingBuffer<Object[]> buffer;

    private final Config               config;

    private final DataSource           source;

    private volatile long              currentPos = 0;

    private String                     Q_SQL;

    public DefaultMysqlReader(RingBuffer<Object[]> buffer, Config config, DataSource source) {
        this.buffer = buffer;
        this.config = config;
        this.source = source;
        Q_SQL = buildSQL();
    }

    @Override
    public void run() {
        while (true) {
            if (currentPos >= config.getEnd().get())
                break;
            executeQuery();
            updateCurrentPos();
        }
    }

    private void updateCurrentPos() {
        long l = Math.max(config.getStart().get(), currentPos);
        currentPos = l + config.getStepSize();
    }

    private String buildSQL() {
        return "select " + config.getSourceColumsNames() + " from " + config.getSourceTableName() + " where " + config.getPrimaryKeyName() + " > ? and "
                + config.getPrimaryKeyName() + " <= ? ";
    }

    private void executeQuery() {
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = source.getConnection();
            p = c.prepareStatement(Q_SQL);
            p.setLong(1, currentPos);
            p.setLong(2, Math.min(currentPos + config.getStepSize(), config.getEnd().get()));
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Object[] item = new Object[config.getSourceColumsArray().size()];
                for (int i = 1; i <= config.getSourceColumsArray().size(); i++) {
                    item[i - 1] = r.getObject(i);
                }
                buffer.add(item, Mode.MODE_BLOCKING);
            }
            r.close();
        } catch (Exception e) {
            // TODO LOG
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
