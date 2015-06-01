package jakiro.mybatis;


public interface CheckHandler {

    public static final String SQL_TYPE_INSERT = "insert";
    public static final String SQL_TYPE_UPDATE = "update";
    public static final String SQL_TYPE_DELETE = "delete";
    public static final String SQL_TYPE_SELECT = "select";
    public static final String SQL_TYPE_OTHER  = "other";

    public void check(String sqlType, String tableName, Object params, String mapperId);


}
