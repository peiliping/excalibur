package phoenix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.service.DualService;
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
        DualService s3 = Config.getApplicationContext().getBean(DualService.class);
        while (true) {
            try {
                s.log();
                s.log2();
            } catch (Exception e) {
            }
            try {
                s2.log();
                s.log2();
            } catch (Exception e) {
            }
            Thread.sleep(100);
            s3.queryDual();
        }
    }
}
