package phoenix;

import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;

import phoenix.datasource.davincicode.model.Account;
import phoenix.datasource.davincicode.model.ConfigItem;

public class TreemapTest {

	public static void main(String[] args) throws InterruptedException {

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

		vvv(1);
	}

	private static void vvv(int v) {
		while (v-- > 0) {

			ConcurrentNavigableMap<Object, String> tm = new ConcurrentSkipListMap<>();

			long ts = System.currentTimeMillis();

			for (int i = 20; i > 0; i--) {
				String t = String.valueOf(i);
				tm.put(t, t);
			}
			for (Map.Entry<Object, String> x : tm.entrySet()) {
				System.out.println(x);
			}

			System.out.println(JSON.toJSONString(tm));
			String xx = JSON.toJSONString(tm);
			tm = JSON.parseObject(xx, new TypeReference<ConcurrentSkipListMap<Object, String>>() {
			});
			for (Map.Entry<Object, String> x : tm.entrySet()) {
				System.out.println(x);
			}
		}

	}
}
