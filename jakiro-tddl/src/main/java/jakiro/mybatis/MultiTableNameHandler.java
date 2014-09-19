package jakiro.mybatis;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiTableNameHandler implements TableNameHandler {

    protected List<TableNameHandler> handlers = new ArrayList<TableNameHandler>();

    @Override
    public String getTargetTableName(String sqlType, String tableName, Object params, String mapperId) {
        String result = tableName;
        for (TableNameHandler th : handlers) {
            result = th.getTargetTableName(sqlType, result, params, mapperId);
        }
        return result;
    }

}
