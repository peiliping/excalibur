package phoenix;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.datasource.davincicode.model.Account;
import phoenix.datasource.davincicode.model.ConfigItem;
import phoenix.service.DualService;
import phoenix.util.Constants;
import phoenix.util.InitTool;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {

		ConfigItem ci = new ConfigItem();
		ci.setId(1);
		ci.setKey("ABC");
		ci.setVersion(System.currentTimeMillis());
		ci.setCurrentAccount(new Account());
		ci.getCurrentAccount().setUsername("root");
		ci.getCurrentAccount().setPassword("root");
		ci.setPreviousAccount(new Account());
		ci.getPreviousAccount().setUsername("root");
		ci.getPreviousAccount().setPassword("root");
		ci.setProperties(Maps.newConcurrentMap());
		ci.getProperties().put("url", "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8");
		ci.getProperties().put("driverClassName", "com.mysql.jdbc.Driver");
		ci.getProperties().put("initialSize", "100");
		ci.getProperties().put("minIdle", "100");
		ci.getProperties().put("maxActive", "5000");
		ci.getProperties().put("maxWait", "5000");
		ci.getProperties().put("defaultAutoCommit", "true");
		ci.getProperties().put("timeBetweenEvictionRunsMillis", "300000");
		ci.getProperties().put("minEvictableIdleTimeMillis", "300000");
		ci.getProperties().put("validationQuery", "SELECT 'x' FROM DUAL");
		ci.getProperties().put("testWhileIdle", "true");
		ci.getProperties().put("testOnBorrow", "false");
		ci.getProperties().put("testOnReturn", "false");
		ci.getProperties().put("poolPreparedStatements", "true");
		ci.getProperties().put("maxPoolPreparedStatementPerConnectionSize", "20");
		ci.getProperties().put("removeAbandoned", "true");
		ci.getProperties().put("removeAbandonedTimeout", "1200");
		ci.getProperties().put("logAbandoned", "true");

		System.out.println(JSON.toJSONString(ci));

		Preconditions.checkNotNull(args, "Missing Params");
		Config.setContext(new Context(InitTool.loadFile(args[0])));

		InitTool.initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
		Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));

		LOG.info("=================START=================");

		DualService ds = Config.getApplicationContext().getBean(DualService.class);
		System.out.println(ds.queryDual().get(0));
	}
}
