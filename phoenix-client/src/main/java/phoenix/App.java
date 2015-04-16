package phoenix;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.dao.DualDao;
import phoenix.util.Constants;
import phoenix.util.PropertiesTool;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.google.common.base.Preconditions;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger("abc");

    public static void main(String[] args) throws Exception {
        
        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(PropertiesTool.loadFile(args[0])));

        initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
        Config.setApplicationContext(new ClassPathXmlApplicationContext("classpath*:spring.xml"));

        // test--------
        DualDao a = Config.getApplicationContext().getBean(DualDao.class);
        List<String> l = a.query(new HashMap<String, Object>());
        LOG.warn(l.size() + " " + l.get(0));
        System.out.println(l.size() + "" + l.get(0));
    }

    private static void initLogBack(String fn) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(loggerContext);
            joranConfigurator.doConfigure(fn);
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }
}
