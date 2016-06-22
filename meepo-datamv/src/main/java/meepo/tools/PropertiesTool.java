package meepo.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class PropertiesTool {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesTool.class);

	public static DataSource createDataSource(String configPath) {
		Properties properties = loadFile(configPath);
		try {
			return DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			LOG.error("Create DataSource Error : " + configPath, e);
			Validate.isTrue(false);
		}
		return null;
	}

	public static Properties loadFile(String configPath) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			Properties properties = new Properties();
			properties.load(in);
			return properties;
		} catch (Exception e) {
			LOG.error("Load PropertyFile Error : " + configPath, e);
			Validate.isTrue(false);
		}
		return null;
	}

}
