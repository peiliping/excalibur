package phoenix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.service.ITestLogService;
import phoenix.service.Test2LogService;
import phoenix.util.Constants;
import phoenix.util.InitTool;

import com.google.common.base.Preconditions;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(InitTool.loadFile(args[0])));

        InitTool.initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
        Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));

        ITestLogService s = Config.getApplicationContext().getBean(ITestLogService.class);
        Test2LogService s2 = Config.getApplicationContext().getBean(Test2LogService.class);

        while (true) {
            s.log();
            try {
                s.log2();
            } catch (Exception e) {
            }

            s2.log();
            try {
                s2.log2();
            } catch (Exception e) {
            }
            Thread.sleep(10);
        }
        // DualService s = Config.getApplicationContext().getBean(DualService.class);
        // System.out.println(s.queryDual());

    }
}
