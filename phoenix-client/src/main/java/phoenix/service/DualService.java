package phoenix.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import phoenix.dao.DualDao;
import phoenix.dataObject.MultiResult;
import phoenix.util.HBasePartitionUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class DualService {

    @Autowired
    private DualDao            dao;

    private HBasePartitionUtil UTIL = new HBasePartitionUtil(256, 64);

    public List<String> queryDual() {
        return dao.query(new HashMap<String, Object>());
    }

    public List<MultiResult> queryPhoenix(long applicationId, long metricTypeId, int dataVersion, long start, long end) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataVersion", dataVersion);
        params.put("applicationId", applicationId);
        params.put("metricTypeId", metricTypeId);
        params.put("start", start);
        params.put("end", end);
        Set<Integer> t = UTIL.getNodeIds(start, end, applicationId, metricTypeId);
        params.put("salts", Lists.newArrayList(t.toArray()));
        return dao.queryPhoenix(params);
    }
}
