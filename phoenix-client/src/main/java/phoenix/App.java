package phoenix;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.dataObject.MultiResult;
import phoenix.service.DualService;
import phoenix.util.Constants;
import phoenix.util.PropertiesTool;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(PropertiesTool.loadFile(args[0])));

        initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
        Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));

        final DualService DS = Config.getApplicationContext().getBean(DualService.class);

        LOG.warn("======================START===================");
        Files.readLines(new File(Config.getContext().getString("dataFile")), Charset.defaultCharset(), new LineProcessor<String>() {
            @Override
            public String getResult() {
                return null;
            }

            @Override
            public boolean processLine(String line) throws IOException {
                long t = System.currentTimeMillis();
                String[] params = line.split("\t");
                long applicationId = Long.valueOf(params[0]);
                long metricTypeId = Long.valueOf(params[1]);
                long start = Long.valueOf(params[2]);
                long end = Long.valueOf(params[3]);
                List<MultiResult> res = DS.queryPhoenix(applicationId, metricTypeId, 0, start, end);
                LOG.info(applicationId + " " + metricTypeId + " " + (System.currentTimeMillis() - t) + " " + res.size());
                return true;
            }
        });
        LOG.warn("======================END=====================");
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
