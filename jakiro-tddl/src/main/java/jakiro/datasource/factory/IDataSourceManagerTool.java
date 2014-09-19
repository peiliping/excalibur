package jakiro.datasource.factory;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

public abstract class IDataSourceManagerTool {

    protected static HashMap<String, IDataSourceManagerTool> HandlerMap = new HashMap<String, IDataSourceManagerTool>();

    static {
        HandlerMap.put(DruidDataSource.class.getCanonicalName(), new DruidDataSourceManagerTool());
    }

    protected static Logger                                  log        = LoggerFactory.getLogger(IDataSourceManagerTool.class);

    public abstract DataSource createAinitDataSource(Map<String, String> properties);

    public abstract boolean destroyDataSource(DataSource datasource);

    public static IDataSourceManagerTool getHandler(String clazzName) {
        return HandlerMap.get(clazzName);
    }

}
