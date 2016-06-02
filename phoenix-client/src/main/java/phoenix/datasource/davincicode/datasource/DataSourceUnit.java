package phoenix.datasource.davincicode.datasource;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.eventbus.Subscribe;

import lombok.Getter;
import lombok.Setter;
import phoenix.datasource.davincicode.model.ConfigItem;

public class DataSourceUnit extends LazyConnectionDataSourceProxy {

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceUnit.class);

	@Setter
	@Getter
	private String davinciCode;

	private ConfigItem configItem;

	public void close() {
		if (getTargetDataSource() instanceof DruidDataSource) {
			((DruidDataSource) getTargetDataSource()).close();
		}
	}

	@Subscribe
	public void handleEvent(Map<String, ConfigItem> config) {
		if (!config.containsKey(this.davinciCode))
			return;
		ConfigItem ci = config.get(this.davinciCode);
		if (this.configItem != null && this.configItem.getVersion() >= ci.getVersion())
			return;

		try {
			setTargetDataSource(DruidDataSourceFactory.createDataSource(buildProperties(ci)));
			super.afterPropertiesSet();
			this.configItem = ci;
		} catch (Exception e) {
			LOG.error("DataSourceUnit Handle Event Error : ", e);
			Validate.isTrue(false, e.getMessage());
		}
	}

	public Properties buildProperties(ConfigItem ci) {
		Properties ps = new Properties();
		ps.setProperty("username", ci.getCurrentAccount().getUsername());
		ps.setProperty("password", ci.getCurrentAccount().getPassword());
		for (Map.Entry<String, String> item : ci.getProperties().entrySet()) {
			ps.setProperty(item.getKey(), item.getValue());
		}
		return ps;
	}

	@Override
	public void afterPropertiesSet() {
		// lazy
	}

}
