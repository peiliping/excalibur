package icesword.agent;

import icesword.agent.data.ClientStatus;
import icesword.agent.data.Config;
import icesword.agent.data.JvmItem;
import icesword.agent.service.JpsMonitorService;
import icesword.agent.service.JstatMonitorService;
import icesword.agent.util.NetTools;
import icesword.agent.util.NetTools.HttpResult;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class App {

    public static String               configServerAddress;
    public static final String         CONNECT_PATH        = "/connect";

    private static Config              config              = new Config();
    private static List<ClientStatus>  clientStatus        = new ArrayList<ClientStatus>();
    private static JstatMonitorService jstatMonitorService = new JstatMonitorService();

    public static void main(String[] args) {
        parseArgs(args);
        String baseParams = "agent_ip=" + NetTools.getLocalIP() + "&" + "health_info=";

        while (true) {
            try {
                boolean r = updateConfig(configServerAddress + CONNECT_PATH, baseParams);
                if (!r) {
                    List<JvmItem> jvms = JpsMonitorService.findWorkerJVM();
                    System.out.println(JSON.toJSONString(jvms));
                }
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
            }
        }
    }

    private static boolean updateConfig(String baseUrl, String baseParams) {
        HttpResult hr = NetTools.httpPost(baseUrl, baseParams + JSON.toJSONString(clientStatus));
        if (hr.success) {
            JSONObject jo = JSON.parseObject(hr.content);
            config.groupId.set(jo.getLongValue("groupId"));
            config.appId.set(jo.getLongValue("appId"));
            config.agentIdentifier = jo.getString("agentIdentifier");
            config.period.set(jo.getLongValue("period"));
            clientStatus.clear();
            return true;
        }
        return false;
    }

    private static void parseArgs(String[] args) {
        if (args != null && args.length > 0) {
            configServerAddress = args[0];
        } else {
            System.out.println("Params Missing");
            System.exit(1);
        }
    }
}
