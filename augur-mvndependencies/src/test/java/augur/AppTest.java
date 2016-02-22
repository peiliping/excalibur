package augur;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;

public class AppTest {


    public static void main(String[] args) {

        Set<String> excludes = new HashSet<String>();
        excludes.add("jakiro:jakiro-tddl:(,0.7)");
        excludes.add("com.alibaba:fastjson:(,1.1.30)");
        excludes.add("com.oneapm.operation:Operation-Monitor:(,1.3)");
        Set<String> includes = new HashSet<String>();
        Set<String> warnings = new HashSet<String>();

        Map<String, Set<String>> rules = new HashMap<String, Set<String>>();
        rules.put("excludes", excludes);
        rules.put("includes", includes);
        rules.put("warnings", warnings);

        Map<String, Map<String, Set<String>>> rs = new HashMap<String, Map<String, Set<String>>>();
        rs.put("rules", rules);

        System.out.println(JSON.toJSONString(rs));
    }
}
