package phoenix.dao;

import java.util.List;
import java.util.Map;

import phoenix.dataObject.AreaDO;
import phoenix.dataObject.DataDO;

public interface AreaDao {

    public List<AreaDO> queryAreaList(Map<String, Object> paramMap);

    public List<DataDO> metricTest(Map<String, Object> paramMap);

}
