package phoenix;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
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

        final CuratorFramework zk =
                CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").namespace("").retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(5000)
                        .build();
        zk.start();
        System.out.println(zk.checkExists().forPath("/kafka_clusters"));
        zk.getData().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println(event.getPath());
            }
        }).inBackground().forPath("/kafka_clusters");
        Thread.sleep(Long.MAX_VALUE);
    }
}
