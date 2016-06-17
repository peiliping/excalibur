package phoenix.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import lombok.Getter;
import lombok.Setter;
import phoenix.config.Config;
import phoenix.util.Constants;
import phoenix.util.InitTool;

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
			Properties ps = InitTool.loadFile(path);
			DataSource ds = DruidDataSourceFactory.createDataSource(ps);
			if (ps.containsKey("name")) {
				((DruidDataSource) ds).setName(ps.getProperty("name"));
			} else {
				String url = ((DruidDataSource) ds).getRawJdbcUrl();
				String ip_port = url.split("/")[2].replaceAll(":", "_");
				((DruidDataSource) ds).setName(ip_port);
			}
			setTargetDataSource(ds);
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
