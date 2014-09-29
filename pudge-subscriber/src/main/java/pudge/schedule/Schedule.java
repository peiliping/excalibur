package pudge.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pudge.ClientConfig;
import pudge.entity.Message;
import pudge.tools.HttpUtil;
import pudge.tools.HttpUtil.HttpResult;

public class Schedule {

    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public void run(final ClientConfig conf, final Message mes) {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HttpResult r = HttpUtil.httpGet(buildUrl(conf, mes));
            }
        }, conf.getInitDelay(), conf.getInterval(), TimeUnit.MILLISECONDS);
    }

    private String buildUrl(ClientConfig conf, Message mes) {
        String r = conf.getServerAddress() + ":" + conf.getServerPort() + "/" + conf.getUrlPath() + "?";
        r = r + "topic=" + mes.getTopic().getTopic();
        r = r + "&=timestamp=" + mes.getTimestamp();
        return r;
    }
}
