package meepo.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class PropertiesTool {

    public static DataSource createDataSource(String configPath) {
        try {
            Properties properties = loadFile(configPath);
            return properties == null ? null : DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            // TODO LOG
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
            // TODO LOG
        }
        return null;
    }

}
