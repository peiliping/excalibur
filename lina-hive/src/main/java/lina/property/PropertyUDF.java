package lina.property;

import lina.util.FeatureUtil;

import org.apache.hadoop.hive.ql.exec.UDF;

public class PropertyUDF extends UDF {

    public String evaluate(String prop, String key) {
        return FeatureUtil.toMap(prop).get(key);
    }

}
