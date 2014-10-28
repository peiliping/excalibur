package meepo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.Pair;

public class BasicDao {

    public static Pair<List<String>, Map<String, Integer>> parserSchema(DataSource ds, String tableName, String columsNames) {
        String sql_query = "select " + columsNames + " from " + tableName + " limit 1";
        Connection c = null;
        PreparedStatement p = null;
        try {
            Map<String, Integer> result = new HashMap<String, Integer>();
            List<String> array = new ArrayList<String>();
            c = ds.getConnection();
            p = c.prepareStatement(sql_query);
            ResultSet r = p.executeQuery();
            for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
                result.put(r.getMetaData().getColumnName(i), r.getMetaData().getColumnType(i));
                array.add(r.getMetaData().getColumnName(i));
            }
            r.close();
            return Pair.of(array, result);
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
        return null;
    }

    public static Pair<Long, Long> autoGetPoint(DataSource ds, String tableName, String primaryKeyName) {
        String sql_query = "select " + "min(" + primaryKeyName + "),max(" + primaryKeyName + ") from " + tableName;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = ds.getConnection();
            p = c.prepareStatement(sql_query);
            ResultSet r = p.executeQuery();
            return r.next() ? Pair.of(r.getLong(1)-1, r.getLong(2)) : Pair.of(0L, 0L);
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
        return null;
    }
}
