package phoenix.dao;

import java.util.List;
import java.util.Map;

import com.oneapm.operation.monitor.annotation.Monitor;

public interface DualDao {

    public List<String> query(Map<String, Object> paramMap);

}
