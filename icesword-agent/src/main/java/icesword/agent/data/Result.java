package icesword.agent.data;

import java.util.ArrayList;
import java.util.List;

public class Result {

    public Meta                meta = new Meta();

    public List<JVMMemoryData> data = new ArrayList<JVMMemoryData>();

    public Result(Config config) {
        this.meta.app_group_id = config.groupId.toString();
        this.meta.app_id = config.appId.toString();
        this.meta.idnetifier = config.agentIdentifier;
    }

}
