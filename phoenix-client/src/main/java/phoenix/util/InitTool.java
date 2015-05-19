package phoenix.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class InitTool {

    public static Properties loadFile(String configPath) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(configPath));
        Properties properties = new Properties();
        properties.load(in);
        in.close();
        return properties;
    }

    public static void initLogBack(String fn) {
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
