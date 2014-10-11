package pudge.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pudge.ClientConfig;
import pudge.SubscriberListener;
import pudge.entity.Info;
import pudge.entity.InfoType;
import pudge.entity.Message;
import pudge.tools.HttpUtil;
import pudge.tools.HttpUtil.HttpResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Schedule {

    private static final Logger      LOG               = LoggerFactory.getLogger("Pudge");

    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public void run(final ClientConfig conf, final AtomicReference<Message> mes, final SubscriberListener sl) {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HttpResult r = HttpUtil.httpGet(buildUrl(conf, mes.get()), conf.getRetryTimes(), conf.getExcuteTimeOut());
                if (!r.success) {
                    LOG.error("Pudge Connect Failed : " + buildUrl(conf, mes.get()), conf.getRetryTimes(), conf.getExcuteTimeOut());;
                }
                JSONObject jo = JSON.parseObject(r.content);
                Message newmes = new Message();
                newmes.setTopic(mes.get().getTopic());
                newmes.setTimestamp(jo.getLongValue(CONS_TIMESTAMP));
                newmes.setMd5(jo.getString(CONS_MD5));
                Info entire = new Info();
                entire.setInfo(jo.getString(CONS_ENTIREINFOSTR));
                entire.setInfoType(InfoType.getInfoType(jo.getIntValue(CONS_ENTIREINFOTYPE)));
                newmes.setEntireInfo(entire);
                Info diff = new Info();
                diff.setInfo(jo.getString(CONS_DIFFINFOSTR));
                diff.setInfoType(InfoType.getInfoType(jo.getIntValue(CONS_DIFFINFOTYPE)));
                newmes.setDiffInfo(diff);
                sl.notify(newmes);
            }
        }, conf.getInitDelay(), conf.getInterval(), TimeUnit.MILLISECONDS);
    }

    private String buildUrl(ClientConfig conf, Message mes) {
        String r = conf.getServerAddress() + ":" + conf.getServerPort() + "/" + conf.getUrlPath() + "?";
        r = r + "topic=" + mes.getTopic().getTopic();
        r = r + "&=timestamp=" + mes.getTimestamp();
        r = r + "&id=" + mes.getTopic().getId();
        return r;
    }

    private static final String CONS_TIMESTAMP      = "timestamp";
    private static final String CONS_MD5            = "md5";
    private static final String CONS_DIFFINFOSTR    = "diffinfostr";
    private static final String CONS_DIFFINFOTYPE   = "diffinfotype";
    private static final String CONS_ENTIREINFOSTR  = "entireinfostr";
    private static final String CONS_ENTIREINFOTYPE = "entireinfotype";

}
