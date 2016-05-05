package phoenix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Preconditions;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.quasar.Quasar;
import phoenix.quasar.Quasar2;
import phoenix.util.Constants;
import phoenix.util.InitTool;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {

		Preconditions.checkNotNull(args, "Missing Params");
		Config.setContext(new Context(InitTool.loadFile(args[0])));

		InitTool.initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
		Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));

		LOG.info("=================START=================");

		Thread.sleep(1000 * 5);

		int tn = 16;

		new Quasar2().main(Config.getApplicationContext(), 4);
		new Quasar().main(Config.getApplicationContext(), 4);
		for (int t = 0; t < 50; t++) {
			new Quasar().main(Config.getApplicationContext(), tn);
			new Quasar2().main(Config.getApplicationContext(), tn);
			Thread.sleep(1000);
		}

	}
}
