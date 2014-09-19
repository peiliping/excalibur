package jakiro;

import jakiro.datasource.DynamicCoreDataSource;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

public class DynamicDataSource extends LazyConnectionDataSourceProxy {

    protected DynamicCoreDataSource core = new DynamicCoreDataSource();

    public void setDynamicDataSourceName(String name) {
        core.setDynamicCoreDataSourceName(name);
    }

    public void setDataSourceClassName(String name) {
        core.setDataSourceClassName(name);
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        core.setTargetDataSources(targetDataSources);
    }

    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        core.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    @Override
    public void afterPropertiesSet() {
        core.afterPropertiesSet();
        super.setTargetDataSource(core);
        super.afterPropertiesSet();
    }

    public Map<Object, Object> allDataSources() {
        return core.getTmp_targetDataSources();
    }

    @Override
    @Deprecated
    public void setTargetDataSource(DataSource targetDataSource) {}

    public void close() {
        core.close();
    }
}
