package phoenix.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneapm.operation.monitor.annotation.Monitor;

import phoenix.dao.DualDao;

@Service
public class DualService {

	@Autowired
	private DualDao dao;

	@Monitor(metricName = "plp")
	public List<String> queryDual() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 11);
		return dao.query(params);
	}

}
