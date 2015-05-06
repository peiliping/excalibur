package jakiro.datasource.threadlocal;


public class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceBody> contextHolder = new ThreadLocal<DataSourceBody>();

    public static void setDataSourcePrefix(String prefix) {
        initDataSourceBody();
        contextHolder.get().prefix = prefix;
    }

    public static void setDataSourceName(String dataSourceName) {
        initDataSourceBody();
        contextHolder.get().name = dataSourceName;
    }

    public static void setDataSourceSuffix(String suffix) {
        initDataSourceBody();
        contextHolder.get().suffix = suffix;
    }

    public static void setDataSourceRW(boolean isWrite) {
        initDataSourceBody();
        contextHolder.get().rw = isWrite ? "w" : "r";
    }

    public static String getDataSourceName() {
        return contextHolder.get() != null ? contextHolder.get().buildName() : null;
    }

    public static void clearDataSourceName() {
        contextHolder.remove();
    }

    private static void initDataSourceBody() {
        if (contextHolder.get() == null)
            contextHolder.set(new DataSourceBody());
    }
}
