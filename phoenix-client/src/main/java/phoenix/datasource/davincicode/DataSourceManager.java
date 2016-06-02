package phoenix.datasource.davincicode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.Setter;
import phoenix.datasource.davincicode.datasource.DataSourceUnit;
import phoenix.datasource.davincicode.model.ConfigItem;
import phoenix.datasource.davincicode.model.Param;
import phoenix.datasource.davincicode.util.NetTools;
import phoenix.datasource.davincicode.util.NetTools.HttpResult;

public class DataSourceManager implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceManager.class);

	private static final String PROTOCAL = "http://";

	@Setter
	@Getter
	private String configServerAddress;

	private ExecutorService timerExecutor = Executors.newSingleThreadExecutor();

	@Setter
	@Getter
	private long interval = 60 * 1000;

	private Map<String, ConfigItem> configs = Maps.newConcurrentMap();

	@Setter
	@Getter
	private Map<String, DataSourceUnit> manage = Maps.newConcurrentMap();

	private final EventBus eventBus = new EventBus("ConfigService-event-bus");

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notEmpty(configServerAddress);
		updateConfig(true);
		this.timerExecutor.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(interval);
						updateConfig(false);
					} catch (Exception e) {
						LOG.error("Config Service Update Config Error :", e);
					}
				}
			}
		});
	}

	private void updateConfig(boolean init) {
		HttpResult result = NetTools.httpPost(PROTOCAL + this.configServerAddress, buildParams());
		Validate.isTrue(result.success);
		Map<String, ConfigItem> tc = JSON.parseObject(result.content, new TypeReference<Map<String, ConfigItem>>() {
		});
		this.configs.putAll(tc);
		if (init) {
			for (Map.Entry<String, DataSourceUnit> v : manage.entrySet()) {
				eventBus.register(v.getValue());
			}
		}
		this.eventBus.post(tc);
	}

	private String buildParams() {
		List<Param> result = Lists.newArrayList();
		for (Map.Entry<String, DataSourceUnit> me : manage.entrySet()) {
			long version = configs.containsKey(me.getKey()) ? configs.get(me.getKey()).getVersion() : 0;
			result.add(Param.builder().key(me.getKey()).version(version).build());
		}
		return JSON.toJSONString(result);
	}

	public void close() {
		this.timerExecutor.shutdown();
		for (Map.Entry<String, DataSourceUnit> v : manage.entrySet()) {
			eventBus.unregister(v.getValue());
		}
	}
}
