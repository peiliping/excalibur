package jakiro.sequence.dao;

import jakiro.sequence.SequenceException;
import jakiro.sequence.seq.SequenceRange;
import jakiro.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSequenceDao implements SequenceDao {

    protected static final Logger log                              = LoggerFactory.getLogger(AbstractSequenceDao.class);

    private static final int      DEFAULT_RETRY_TIMES              = 150;
    private static final int      MIN_STEP                         = 1;
    private static final int      MAX_STEP                         = 100000;

    private static final long     DELTA                            = 100000000L;

    private static final String   DEFAULT_TABLE_NAME               = "sequence";
    private static final String   DEFAULT_NAME_COLUMN_NAME         = "name";
    private static final String   DEFAULT_VALUE_COLUMN_NAME        = "value";
    private static final String   DEFAULT_GMT_MODIFIED_COLUMN_NAME = "gmt_modified";

    private DataSource            dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String                tableName                        = DEFAULT_TABLE_NAME;

    private String                nameColumnName                   = DEFAULT_NAME_COLUMN_NAME;

    private String                valueColumnName                  = DEFAULT_VALUE_COLUMN_NAME;

    private String                gmtModifiedColumnName            = DEFAULT_GMT_MODIFIED_COLUMN_NAME;

    private int                   retryTimes                       = DEFAULT_RETRY_TIMES;

    protected int                 step                             = 1000;

    private volatile String       selectSql;
    private volatile String       updateSql;

    public void setStep(int step) {
        if (step < MIN_STEP || step > MAX_STEP) {
            StringBuilder message = new StringBuilder();
            message.append("Property step out of range [").append(MIN_STEP);
            message.append(",").append(MAX_STEP).append("], step = ").append(step);
            throw new IllegalArgumentException(message.toString());
        }
        this.step = step;
    }

    public void setRetryTimes(int retryTimes) {
        if (retryTimes < 0) {
            throw new IllegalArgumentException("Property retryTimes cannot be less than zero, retryTimes = " + retryTimes);
        }
        this.retryTimes = retryTimes;
    }


    protected String getSelectSql() {
        if (selectSql == null) {
            synchronized (this) {
                if (selectSql == null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("select ").append(valueColumnName);
                    buffer.append(" from ").append(tableName);
                    buffer.append(" where ").append(nameColumnName).append(" = ?");
                    selectSql = buffer.toString();
                }
            }
        }
        return selectSql;
    }

    protected String getUpdateSql() {
        if (updateSql == null) {
            synchronized (this) {
                if (updateSql == null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("update ").append(tableName);
                    buffer.append(" set ").append(valueColumnName).append(" = ?, ");
                    buffer.append(gmtModifiedColumnName).append(" = ? where ");
                    buffer.append(nameColumnName).append(" = ? and ");
                    buffer.append(valueColumnName).append(" = ?");
                    updateSql = buffer.toString();
                }
            }
        }

        return updateSql;
    }

    protected static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.debug("Could not close JDBC ResultSet", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC ResultSet", e);
            }
        }
    }

    protected static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.debug("Could not close JDBC Statement", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC Statement", e);
            }
        }
    }

    protected static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.debug("Could not close JDBC Connection", e);
            } catch (Throwable e) {
                log.debug("Unexpected exception on closing JDBC Connection", e);
            }
        }
    }

    @Override
    public SequenceRange nextRange(String name, int index, int total) throws SequenceException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name is Null");
        }
        long oldValue;
        long newValue;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        for (int i = 0; i < retryTimes + 1; ++i) {
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(getSelectSql());
                stmt.setString(1, name);
                rs = stmt.executeQuery();
                rs.next();
                oldValue = rs.getLong(1);
                if (oldValue < 0) {
                    StringBuilder message = new StringBuilder();
                    message.append("Sequence value cannot be less than zero, value = ").append(oldValue);
                    message.append(", please check table ").append(tableName);
                    throw new SequenceException(message.toString());
                }
                if (oldValue > Long.MAX_VALUE - DELTA) {
                    StringBuilder message = new StringBuilder();
                    message.append("Sequence value overflow, value = ").append(oldValue);
                    message.append(", please check table ").append(tableName);
                    throw new SequenceException(message.toString());
                }
                newValue = buildNewValue(oldValue, index, total);
                if (!check(oldValue, newValue, index, total)) {
                    throw new SequenceException("Check Failed" + oldValue + " " + newValue + " " + index + " " + total);
                }
            } catch (SQLException e) {
                throw new SequenceException(e);
            } finally {
                closeResultSet(rs);
                rs = null;
                closeStatement(stmt);
                stmt = null;
                closeConnection(conn);
                conn = null;
            }

            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(getUpdateSql());
                stmt.setLong(1, newValue);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setString(3, name);
                stmt.setLong(4, oldValue);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    continue;
                }
                return buildReturnRange(newValue, oldValue);
            } catch (SQLException e) {
                throw new SequenceException(e);
            } finally {
                closeStatement(stmt);
                stmt = null;
                closeConnection(conn);
                conn = null;
            }
        }
        throw new SequenceException("Retried too many times, retryTimes = " + retryTimes);
    }

    protected abstract long buildNewValue(long old, int index, int total);

    protected abstract SequenceRange buildReturnRange(long newV, long oldV);

    protected abstract boolean check(long oldV, long newV, int index, int total);
}
