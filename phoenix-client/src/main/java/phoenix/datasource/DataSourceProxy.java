package phoenix.datasource;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import phoenix.config.Config;
import phoenix.util.Constants;
import phoenix.util.PropertiesTool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DataSourceProxy extends LazyConnectionDataSourceProxy {

    @Setter
    @Getter
    private String dataSourceName;

    @Override
    public void afterPropertiesSet() {
        if (dataSourceName == null) {
            dataSourceName = Constants.CONF_DATASOURCE_ITEM;
        }
        String path = Config.getContext().getString(dataSourceName);
        try {
            Properties ps = PropertiesTool.loadFile(path);
            setTargetDataSource(DruidDataSourceFactory.createDataSource(ps));
        } catch (Exception e) {
            Validate.isTrue(false, "DataSource Init Error");
        }
        super.afterPropertiesSet();
    }

    public void close() {
        if (getTargetDataSource() instanceof DruidDataSource) {
            ((DruidDataSource) getTargetDataSource()).close();
        }
    }

}
