package jakiro.mybatis;

import java.util.ArrayList;
import java.util.List;

public class MultiCheckHandler implements CheckHandler {

    protected List<CheckHandler> handlers = new ArrayList<CheckHandler>();

    @Override
    public void check(String sqlType, String tableName, Object params, String mapperId) {
        for (CheckHandler ch : handlers) {
            ch.check(sqlType, tableName, params, mapperId);
        }
    }

}
