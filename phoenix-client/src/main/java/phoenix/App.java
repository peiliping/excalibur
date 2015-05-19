package phoenix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.util.Constants;
import phoenix.util.InitTool;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.google.common.base.Preconditions;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(InitTool.loadFile(args[0])));

        InitTool.initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
        Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));


    }
}
