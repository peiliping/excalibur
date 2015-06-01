package jakiro.mybatis;

import jakiro.datasource.threadlocal.DataSourceContextHolder;
import jakiro.util.Pair;
import jakiro.util.ReflectionUtils;
import jakiro.util.SQLParser;
import jakiro.util.StringUtils;
import jakiro.util.Validate;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class Interceptor4DB implements Interceptor {

    // 输入
    private Map<String, String>               tableNameVsDataSource = new HashMap<String, String>();
    private Map<String, TableNameHandler>     tableNameVsHandler    = new HashMap<String, TableNameHandler>();
    // cache sql parser
    private Map<String, Pair<String, String>> idVSTableNameType     = new ConcurrentHashMap<String, Pair<String, String>>();
    // cache result
    private Map<String, Pair<String, String>> id4TableName          = new ConcurrentHashMap<String, Pair<String, String>>();
    private Map<String, Object>               id4DataSource         = new ConcurrentHashMap<String, Object>();
    private final static Pair<String, String> Skip4HandleName       = Pair.of("", "");
    private static final Object               Skip4DataSource       = new Object();
    // 配置
    private String                            prefix                = "_shared_a0b9c8d7e6_";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private boolean noCache4Sql = false;

    public void setNoCache4Sql(boolean noCache4Sql) {
        this.noCache4Sql = noCache4Sql;
    }

    private boolean cleanThreadLocalFirst = false;

    public void setCleanThreadLocalFirst(boolean cleanThreadLocalFirst) {
        this.cleanThreadLocalFirst = cleanThreadLocalFirst;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MappedStatement mappedStatement = null;
        if (statementHandler instanceof RoutingStatementHandler) {
            StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(statementHandler, "delegate");
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
        } else {
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
        }

        String mapperId = mappedStatement.getId();
        Object params = statementHandler.getBoundSql().getParameterObject();

        handleTableName(mapperId, statementHandler, params);
        handleDataSource(mapperId);

        try {
            return invocation.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceName();
        }
    }

    private void handleDataSource(String mapperId) throws Exception {
        if (cleanThreadLocalFirst) {
            DataSourceContextHolder.clearDataSourceName();
        }
        if (!id4DataSource.containsKey(mapperId)) {
            Pair<String, String> tableNameAndType = idVSTableNameType.get(mapperId);
            if (tableNameAndType == null) {
                DataSourceContextHolder.clearDataSourceName();
                throw new Exception("Parse Sql Failure !!!");
            }
            String ds = tableNameVsDataSource.get(tableNameAndType.getLeft());
            if (ds == null) {
                id4DataSource.put(mapperId, Skip4DataSource);
            } else {
                DataSourceContextHolder.setDataSourceName(ds);
                id4DataSource.put(mapperId, ds);
            }
        } else if (id4DataSource.get(mapperId) == Skip4DataSource) {

        } else {
            DataSourceContextHolder.setDataSourceName((String) id4DataSource.get(mapperId));
        }
    }

    private void handleTableName(String id, StatementHandler statementHandler, Object params) {
        Pair<String, String> tn = id4TableName.get(id);
        if (tn == null || noCache4Sql) {
            tn = SQLParser.findTableNameAndType(statementHandler.getBoundSql().getSql());
            Validate.notNull(tn);
            idVSTableNameType.put(id, Pair.of(tn.getLeft(), tn.getRight()));
            tn = tn.getLeft().startsWith(prefix) ? tn : Skip4HandleName;
            id4TableName.put(id, tn);
        }
        if (tn != Skip4HandleName && tableNameVsHandler.get(tn.getLeft()) != null) {
            String p = tableNameVsHandler.get(tn.getLeft()).getTargetTableName(tn.getRight(), tn.getLeft(), params, id);
            String sql = statementHandler.getBoundSql().getSql();
            if (StringUtils.isNotBlank(sql) && StringUtils.isNotBlank(p)) {
                String nsql = sql.replaceAll(tn.getLeft(), p);
                ReflectionUtils.setFieldValue(statementHandler.getBoundSql(), "sql", nsql);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

    public void setTableNameVsDataSource(Properties properties) {
        if (properties == null || properties.size() == 0)
            return;
        for (Entry<Object, Object> e : properties.entrySet()) {
            tableNameVsDataSource.put((String) e.getKey(), (String) e.getValue());
        }
    }

    public void setTableNameVsHandler(Properties properties) {
        if (properties == null || properties.size() == 0) {
            return;
        }
        for (Entry<Object, Object> e : properties.entrySet()) {
            Object o = null;
            try {
                Class<?> c = Class.forName((String) e.getValue());
                o = c.newInstance();
                Validate.isTrue(o instanceof TableNameHandler);
            } catch (Exception ec) {
                Validate.isTrue(false, ec.toString());
            }
            tableNameVsHandler.put((String) e.getKey(), (TableNameHandler) o);
        }
    }
}
