package icesword.agent.service;

import icesword.agent.data.process.Config;
import icesword.agent.data.process.Event;
import icesword.agent.data.result.Meta;
import icesword.agent.data.result.ResultData;
import icesword.agent.util.CompressUtil;
import icesword.agent.util.NetTools;
import icesword.agent.util.NetTools.HttpResult;
import icesword.agent.util.Triple;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.alibaba.fastjson.JSON;

@Setter
@Getter
@Builder
public class ConfigService {

    private static final String CLIENT_IP    = "10.44.23.9"; // NetTools.getLocalIP();

    public static final String  PROTOCAL     = "http://";

    public static final String  CONNECT_PATH = "/connect";

    public static final String  DATA_PATH    = "/metric/jvm";

    private String              configServerAddress;

    private Config              config;

    public void updateConfigAndSendEvent(String agentVersion) {
        String params = "agent_ip=" + CLIENT_IP + "&agent_version=" + agentVersion + "&health_info=";
        EventService.oOOo();
        List<Event> es = EventService.getLastOne();
        params = params + JSON.toJSONString(es);
        HttpResult hr = NetTools.httpPost(PROTOCAL + configServerAddress + CONNECT_PATH, params);
        if (hr.success) {
            config = JSON.parseObject(hr.content, Config.class);
            config.period = config.period * 1000;
            EventService.cleanLastOne();
        } else {
            EventService.addEvent(new Event(0, "Update Config Error ."));
        }
    }

    public void sendData() {
        try {
            DataService.oOOo();
            Triple<ResultData, ResultData, ResultData> result = DataService.getLastOne();
            sendData(result.getLeft());
            sendData(result.getMiddle());
            sendData(result.getRight());
            DataService.cleanLastOne();
        } catch (Exception ec) {
            ec.printStackTrace();
        }
    }

    private void sendData(ResultData rd) {
        if (rd.getData().size() > 0) {
            buildMeta(rd.getMeta());
            try {
                String paramsM = "data=" + CompressUtil.compress(rd);
                NetTools.httpPost(PROTOCAL + configServerAddress + DATA_PATH, paramsM);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void buildMeta(Meta meta) {
        meta.app_group_id = config.app_group_id;
        meta.app_id = config.app_id;
        meta.identifier = config.identifier;
    }
}
