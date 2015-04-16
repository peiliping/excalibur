package phoenix.datasource;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

public class DataSourceProxy extends LazyConnectionDataSourceProxy {

    public void init() {
        
    }

    public void close() {
        // TODO invoke close core
    }

}
