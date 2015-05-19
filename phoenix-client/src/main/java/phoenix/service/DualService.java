package phoenix.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import phoenix.dao.DualDao;

@Service
public class DualService {

    @Autowired
    private DualDao            dao;

    public List<String> queryDual() {
        return dao.query(new HashMap<String, Object>());
    }

}
