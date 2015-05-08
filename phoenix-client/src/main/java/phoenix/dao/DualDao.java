package phoenix.dao;

import java.util.List;
import java.util.Map;

import phoenix.dataObject.MultiResult;

public interface DualDao {

    public List<String> query(Map<String, Object> paramMap);

    public List<MultiResult> queryPhoenix(Map<String, Object> paramMap);

}
