package jakiro.datasource.factory;


import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DruidDataSourceManagerTool extends IDataSourceManagerTool {

    @Override
    public DataSource createAinitDataSource(Map<String, String> properties) {
        DataSource ds = null;
        try {
            ds = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            log.error("create druid datasource error", e);
            try {
                ds = DruidDataSourceFactory.createDataSource(properties);
            } catch (Exception ex) {
                log.error("create druid datasource error", ex);
                throw new IllegalArgumentException("Cannot Create Datasource", ex);
            }
        }
        try {
            ((DruidDataSource) ds).init();
        } catch (SQLException e) {
            log.error("Init druid datasource errror", e);
            throw new IllegalArgumentException("Init Datasource Failure", e);
        }
        return ds;
    }

    @Override
    public boolean destroyDataSource(DataSource datasource) {
        if (datasource != null && datasource instanceof DruidDataSource) {
            try {
                ((DruidDataSource) datasource).close();
                return true;
            } catch (Throwable ex) {
                log.error("close datasource error : ", ex);
            }
        }
        return false;
    }
}
