package icesword.agent.service;

import icesword.agent.data.process.Config;
import icesword.agent.data.process.Event;
import icesword.agent.data.result.ResultData;
import icesword.agent.util.NetTools;
import icesword.agent.util.NetTools.HttpResult;
import icesword.agent.util.Pair;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.alibaba.fastjson.JSON;

@Setter
@Getter
@Builder
public class ConfigService {

    private static final String CLIENT_IP        = "10.128.7.116";       // NetTools.getLocalIP();

    public static final String  CONNECT_PROTOCAL = "http://";

    public static final String  CONNECT_PATH     = "/connect";

    public static final String  M_DATA_PATH      = "/metrics/jvm/gc";

    public static final String  G_DATA_PATH      = "/metrics/jvm/memory";

    private String              configServerAddress;

    private Config              config;

    public void updateConfigAndSendEvent(String agentVersion) {
        String params = "agent_ip=" + CLIENT_IP + "&agent_version=" + agentVersion + "&health_info=";
        EventService.oOOo();
        List<Event> es = EventService.getLastOne();
        params = params + JSON.toJSONString(es);
        HttpResult hr = NetTools.httpPost(CONNECT_PROTOCAL + configServerAddress + CONNECT_PATH, params);
        if (hr.success) {
            System.out.println(hr.content);
            config = JSON.parseObject(hr.content, Config.class);
            config.period = config.period * 1000;
            EventService.cleanLastOne();
        } else {
            EventService.addEvent(new Event(0, "Update Config Error ."));
        }
    }

    public void sendData() {
        DataService.oOOo();
        Pair<ResultData, ResultData> result = DataService.getLastOne();
        System.out.println(JSON.toJSONString(result));
        DataService.cleanLastOne();
    }
}
