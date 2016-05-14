package icesword.agent;

import icesword.agent.data.result.GCDataItem;
import icesword.agent.data.result.IData;
import icesword.agent.data.result.Meta;
import icesword.agent.data.result.ResultData;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class T {

    public static void main(String[] args) {

        Map<String, List<IData>> data = Maps.newHashMap();
        List<IData> ld = Lists.newArrayList();
        ld.add(GCDataItem.builder().times(1d).cost_ms(1d).timestamp(System.currentTimeMillis()).process_signal("Flume").build());
        ld.add(GCDataItem.builder().times(1d).cost_ms(1d).timestamp(System.currentTimeMillis() + 1).process_signal("Flume").build());
        data.put("1.7/ParNew", ld);
        ResultData rd = ResultData.builder().meta(Meta.builder().app_group_id(1).app_id(2).idnetifier("xxx").name_space("JVMGCMetrics").build()).data(data).build();

        System.out.println(JSON.toJSONString(rd));
    }
}
