package icesword.agent;

import icesword.agent.data.ClientStatus;
import icesword.agent.data.Config;
import icesword.agent.data.process.JvmItem;
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
    public static final String         MEMORY_PATH         = "/metric/jvm/memory";

    private static Config              config              = new Config();
    private static List<ClientStatus>  clientStatus        = new ArrayList<ClientStatus>();

    private static JstatMonitorService jstatMonitorService = new JstatMonitorService();

    public static void main(String[] args) {
        parseArgs(args);
        // TODO
        // String baseParams = "agent_ip=" + NetTools.getLocalIP() + "&" + "health_info=";
        String baseParams = "agent_ip=10.128.7.116" + "&" + "health_info=";
        clientStatus.add(new ClientStatus(1, "Agent Start."));
        while (true) {
            try {
                boolean r = updateConfig(configServerAddress + CONNECT_PATH, baseParams);
                if (true) {
                    // TODO
                    System.out.println(JSON.toJSONString(config));
                    List<JvmItem> jvms = JpsMonitorService.findWorkerJVM(null);
                    // jstatMonitorService.addJVMs(jvms, clientStatus);
                    // if (jstatMonitorService.data.size() > 0) {
                    // Result rs = new Result(config);
                    // rs.data.addAll(jstatMonitorService.data);
                    // jstatMonitorService.data.clear();
                    // sendData(configServerAddress + MEMORY_PATH, JSON.toJSONString(rs));
                    // }
                }
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
            }
        }
    }

    private static boolean sendData(String baseUrl, String data) {
        System.out.println(data);
        HttpResult hr = NetTools.httpPost(baseUrl, "data=" + data);
        System.out.println(hr.success);
        System.out.println(hr.content);
        return hr.success;
    }

    private static boolean updateConfig(String baseUrl, String baseParams) {
        System.out.println(JSON.toJSONString(clientStatus));
        HttpResult hr = NetTools.httpPost(baseUrl, baseParams + JSON.toJSONString(clientStatus));
        System.out.println(JSON.toJSONString(hr));
        if (hr.success) {
            JSONObject jo = JSON.parseObject(hr.content);
            config.groupId.set(jo.getLongValue("app_group_id"));
            config.appId.set(jo.getLongValue("app_id"));
            config.period.set(jo.getIntValue("period"));
            config.status.set(jo.getIntValue("status"));
            config.agentIdentifier = jo.getString("identifier");
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
