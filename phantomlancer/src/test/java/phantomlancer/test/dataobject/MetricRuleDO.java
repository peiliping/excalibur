package phantomlancer.test.dataobject;

import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MetricRuleDO {

    private long                 tenantId;

    private int                  type;

    private long                 id;

    private String               name;

    private long                 parentId;

    private String               parentName;

    private Map<Integer, String> mtcsDefine;

    private int                  systemRetainNum4mtcs;

    private Map<Integer, String> dimsDefine;

    private int                  systemRetainNum4dims;

    private Map<Integer, String> tagscDefine;

    private int                  systemRetainNum4tags;

    private Map<String, String>  attributes;

    private int                  version;

    private Date                 gmtCreateTime;

    private Date                 gmtModifyTime;

    enum MetricRuleType {

        SYSTEM, USERCUSTOM

    }

}
