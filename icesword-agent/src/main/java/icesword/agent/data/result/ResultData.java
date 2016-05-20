package icesword.agent.data.result;

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

    public void addOne(String key, IData val) {
        if (!data.containsKey(key)) {
            data.putIfAbsent(key, new ArrayList<IData>()); // thread safe
        }
        data.get(key).add(val);
    }

}
