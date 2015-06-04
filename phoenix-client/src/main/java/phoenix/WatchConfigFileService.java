package phoenix;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phoenix.config.Context;

import com.google.common.eventbus.EventBus;

public class WatchConfigFileService {

    private static Map<String, WatchConfigFileService> BOSS                = new ConcurrentHashMap<String, WatchConfigFileService>();

    private static final Logger                        LOGGER              = LoggerFactory.getLogger(WatchConfigFileService.class);
    private AtomicLong                                 lastChangeTimeStamp = new AtomicLong(0);
    private ExecutorService                            executor            = Executors.newSingleThreadExecutor();
    private final File                                 file;
    private final String                               serviceName;
    private final EventBus                             eventBus;
    private final Long                                 intervalTime;
    private volatile Context                           config;

    public WatchConfigFileService() {
        this("default", "/oneapm/etc/tomcat/config", 1000 * 5);
    }

    public WatchConfigFileService(String name) {
        this(name, "/oneapm/etc/tomcat/config", 1000 * 5);
    }

    public WatchConfigFileService(String name, String configPath) {
        this(name, configPath, 1000 * 5);
    }

    public WatchConfigFileService(String name, String configFilePath, long checkConfigFileInterval) {
        Validate.isTrue(StringUtils.isNotBlank(configFilePath), "ConfigFilePath is blank !");
        Validate.isTrue(StringUtils.isNotBlank(name), "Name is blank !");
        this.serviceName = name;
        Validate.isTrue(!BOSS.containsKey(serviceName));
        this.eventBus = new EventBus(serviceName + "-event-bus");
        this.file = Paths.get(configFilePath).toFile();
        Validate.isTrue(this.file.exists(), "ConfigFile is not exist !");
        Validate.isTrue(checkConfigFileInterval > 0);
        this.intervalTime = checkConfigFileInterval;
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        long lastModified = file.lastModified();
                        if (lastModified > lastChangeTimeStamp.get()) {
                            LOGGER.info("ConfigFile is changed ! WatchConfigFileService-" + serviceName);
                            Context ct = new Context(file.getAbsolutePath());
                            config = ct;
                            eventBus.post(ct);
                            lastChangeTimeStamp.set(lastModified);
                        }
                        Thread.sleep(intervalTime);
                    } catch (Throwable e) {
                        LOGGER.error("WatchConfigFileService-" + serviceName + " Error :", e);
                    }
                }
            }
        });
        BOSS.put(name, this);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
    }

    public void regist(Object o) {
        eventBus.register(o);
    }

    public static WatchConfigFileService get(String name) {
        return BOSS.get(name);
    }

    public Context getConfig() {
        return config;
    }
}
