package jakiro.mybatis;

import jakiro.util.Pair;
import jakiro.util.ReflectionUtils;
import jakiro.util.SQLParser;
import jakiro.util.Validate;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class Interceptor4SQL implements Interceptor {

    private Map<String, Pair<String, String>> idVSTableNameType = new HashMap<String, Pair<String, String>>();
    
    

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
        handle(mapperId, statementHandler, params);
        return invocation.proceed();
    }

    public void handle(String id, StatementHandler statementHandler, Object params) {
        Pair<String, String> tn = null;
        if (idVSTableNameType.containsKey(id)) {
            tn = idVSTableNameType.get(id);
        } else {
            tn = SQLParser.findTableNameAndType(statementHandler.getBoundSql().getSql());
            Validate.notNull(tn);
            idVSTableNameType.put(id, Pair.of(tn.getLeft(), tn.getRight()));
        }
        
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

}
