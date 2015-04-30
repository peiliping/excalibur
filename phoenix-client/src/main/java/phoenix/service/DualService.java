package phoenix.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import phoenix.dao.DualDao;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

@Service
public class DualService {

    @Autowired
    private DualDao dao;

    public List<String> queryMobile(String token, String day, String pt ) {
        Preconditions.checkNotNull(token, day, pt);
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        params.put("day", day);
        params.put("pt", pt);
        return dao.queryMobile(params);
    }
}
