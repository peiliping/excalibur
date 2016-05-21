package icesword.agent.data.result;

import icesword.agent.data.process.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Maps;

@Setter
@Getter
public class ResultData {

    private Meta                               meta = Meta.builder().build();

    private ConcurrentMap<String, List<IData>> data = Maps.newConcurrentMap();

    public ResultData(String nameSpace) {
        meta.name_space = nameSpace;
    }

    public void addOne(String key, IData val) {
        if (!data.containsKey(key)) {
            data.putIfAbsent(key, new ArrayList<IData>()); // thread safe
        }
        data.get(key).add(val);
    }

    public void updateMeta(Config config) {
        if (config != null && config.app_group_id != null && config.app_group_id > 0) {
            meta.app_group_id = config.app_group_id;
            meta.identifier = config.identifier;
            meta.app_id = config.app_id;
        }
    }

}
