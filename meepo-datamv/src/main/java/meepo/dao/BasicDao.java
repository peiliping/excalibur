package meepo.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicDao {

    private static final Logger LOG = LoggerFactory.getLogger(BasicDao.class);

    public static Pair<Long, Long> autoGetStartEndPoint(DataSource ds, String tableName, String primaryKeyName) {
        String sql_query = "select " + "min(" + primaryKeyName + "),max(" + primaryKeyName + ") from " + tableName;
        return excuteQuery(ds, sql_query, new ICallable<Pair<Long, Long>>() {
            @Override
            public void handleParams(PreparedStatement p) throws Exception {}

            @Override
            public Pair<Long, Long> handleResultSet(ResultSet r) throws Exception {
                Validate.isTrue(r.next());
                return Pair.of(r.getLong(1) - 1, r.getLong(2));
            }
        });
    }

    public static Pair<List<String>, Map<String, Integer>> parserSchema(DataSource ds, String tableName, String columsNames) {
        String sql_query = "select " + columsNames + " from " + tableName + " limit 1";
        return excuteQuery(ds, sql_query, new ICallable<Pair<List<String>, Map<String, Integer>>>() {
            @Override
            public void handleParams(PreparedStatement p) throws Exception {}

            @Override
            public Pair<List<String>, Map<String, Integer>> handleResultSet(ResultSet r) throws Exception {
                Map<String, Integer> result = new HashMap<String, Integer>();
                List<String> array = new ArrayList<String>();
                Validate.isTrue(r.getMetaData().getColumnCount() > 0);
                for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
                    result.put(r.getMetaData().getColumnName(i), r.getMetaData().getColumnType(i));
                    array.add(r.getMetaData().getColumnName(i));
                }
                return Pair.of(array, result);
            }
        });
    }

    public static <E> boolean excuteBatchAdd(DataSource ds, String sql, ICallable<E> cal) {
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = ds.getConnection();
            c.setAutoCommit(false);
            p = c.prepareStatement(sql);
            cal.handleParams(p);
            if (p.isClosed()) {
                return excuteBatchAdd(ds, sql, cal);
            }
            p.executeBatch();
            c.commit();
            return true;
        } catch (Exception e) {
            LOG.error("basicdao.excuteBatchAdd", e);
            return false;
        } finally {
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                LOG.error("basicdao.excuteBatchAdd", e);
            }
        }
    }

    public static <E> boolean excuteLoadData(DataSource ds, String sql, InputStream data) {
        Connection c = null;
        PreparedStatement p = null;
        int result = 0;
        try {
            c = ds.getConnection();
            p = c.prepareStatement(sql);
            if (p.isWrapperFor(com.mysql.jdbc.Statement.class)) {
                com.mysql.jdbc.PreparedStatement mysqlStatement = p.unwrap(com.mysql.jdbc.PreparedStatement.class);
                mysqlStatement.setLocalInfileInputStream(data);
                result = mysqlStatement.executeUpdate();
            }
        } catch (Exception e) {
            LOG.error("basicdao.excuteLoadData", e);
        } finally {
            try {
                if (p != null)
                    p.close();
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                LOG.error("basicdao.excutequery", e);
            }
        }
        return result > 0;
    }

    public static <E> E excuteQuery(DataSource ds, String sql, ICallable<E> cal) {
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = ds.getConnection();
            p = c.prepareStatement(sql);
            cal.handleParams(p);
            ResultSet r = p.executeQuery();
            E e = cal.handleResultSet(r);
            r.close();
            return e;
        } catch (Exception e) {
            LOG.error("basicdao.excutequery", e);
        } finally {
            try {
                if (p != null)
                    p.close();
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                LOG.error("basicdao.excutequery", e);
            }
        }
        return null;
    }

}
