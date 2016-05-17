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

    private static final String CLIENT_IP    = "10.128.7.116";      // NetTools.getLocalIP();

    public static final String  PROTOCAL     = "http://";

    public static final String  CONNECT_PATH = "/connect";

    public static final String  M_DATA_PATH  = "/metric/jvm/gc";

    public static final String  G_DATA_PATH  = "/metric/jvm/memory";

    private String              configServerAddress;

    private Config              config;

    public void updateConfigAndSendEvent(String agentVersion) {
        String params = "agent_ip=" + CLIENT_IP + "&agent_version=" + agentVersion + "&health_info=";
        EventService.oOOo();
        List<Event> es = EventService.getLastOne();
        params = params + JSON.toJSONString(es);
        HttpResult hr = NetTools.httpPost(PROTOCAL + configServerAddress + CONNECT_PATH, params);
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
        result.getLeft().getMeta().app_group_id = config.app_group_id;
        result.getLeft().getMeta().app_id = config.app_id;
        result.getLeft().getMeta().identifier = config.identifier;
        String paramsM = "data=" + JSON.toJSONString(result.getLeft());
        System.out.println(paramsM);
        HttpResult hr1 = NetTools.httpPost(PROTOCAL + configServerAddress + M_DATA_PATH, paramsM);
        System.out.println(JSON.toJSONString(hr1));
        result.getRight().getMeta().app_group_id = config.app_group_id;
        result.getRight().getMeta().app_id = config.app_id;
        result.getRight().getMeta().identifier = config.identifier;
        String paramsG = "data=" + JSON.toJSONString(result.getRight());
        System.out.println(paramsM);
        HttpResult hr2 = NetTools.httpPost(PROTOCAL + configServerAddress + G_DATA_PATH, paramsG);
        System.out.println(JSON.toJSONString(hr2));
        DataService.cleanLastOne();
    }
}
