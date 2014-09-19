package jakiro.datasource;

import jakiro.datasource.factory.IDataSourceManagerTool;
import jakiro.datasource.threadlocal.DataSourceContextHolder;
import jakiro.util.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class DynamicCoreDataSource extends AbstractRoutingDataSource {

    protected static Logger                          LOG                       = LoggerFactory.getLogger(DynamicCoreDataSource.class);

    public static Map<String, DynamicCoreDataSource> REG                       = new HashMap<String, DynamicCoreDataSource>();

    protected Map<Object, Object>                    tmp_targetDataSources     = new HashMap<Object, Object>();

    protected DataSource                             tmp_defaultTargetDataSource;

    protected String                                 dynamicCoreDataSourceName = null;

    public String getDynamicCoreDataSourceName() {
        return dynamicCoreDataSourceName;
    }

    public void setDynamicCoreDataSourceName(String dynamicCoreDataSourceName) {
        this.dynamicCoreDataSourceName = dynamicCoreDataSourceName;
    }

    protected String dataSourceClassName = DruidDataSource.class.getCanonicalName();

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    protected volatile boolean closed = false;

    @Override
    public void afterPropertiesSet() {
        Validate.notBlank(dynamicCoreDataSourceName, "DynamicCoreDataSourceName is null");
        super.setTargetDataSources(tmp_targetDataSources);
        super.afterPropertiesSet();
        REG.put(dynamicCoreDataSourceName, this);
    }

    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        tmp_defaultTargetDataSource = (DataSource) defaultTargetDataSource;
        super.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        tmp_targetDataSources.putAll(targetDataSources);
    }

    public Map<Object, Object> getTmp_targetDataSources() {
        return tmp_targetDataSources;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceName();
    }

    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        for (Entry<Object, Object> e : tmp_targetDataSources.entrySet()) {
            IDataSourceManagerTool.getHandler(dataSourceClassName).destroyDataSource((DataSource) e.getValue());
        }
        IDataSourceManagerTool.getHandler(dataSourceClassName).destroyDataSource(tmp_defaultTargetDataSource);
    }
}
