package icesword.agent.data.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Maps;

@Setter
@Getter
public class ResultData {

    private Meta                     meta = Meta.builder().build();

    private Map<String, List<IData>> data = Maps.newConcurrentMap();

    public void addOne(String key, IData val) {
        if (!data.containsKey(key)) {
            data.put(key, new ArrayList<IData>());
        }
        data.get(key).add(val);
    }

}
