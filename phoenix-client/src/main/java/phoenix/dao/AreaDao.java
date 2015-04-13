package phoenix.dao;

import java.util.List;
import java.util.Map;

import phoenix.dataObject.AreaDO;

public interface AreaDao {

    public List<AreaDO> queryAreaList(Map<String, Object> paramMap);

}
