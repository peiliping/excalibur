package phoenix.dataObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import phoenix.util.FeatureUtil;

public abstract class AbstractBasicDO implements Serializable {

    private static final long   serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @Setter
    @Getter
    private long                id;
    /**
     * 数据创建时间
     */
    @Setter
    @Getter
    private Date                gmtCreateTime;
    /**
     * 数据修改时间
     */
    @Setter
    @Getter
    private Date                gmtModifyTime;
    /**
     * 并发锁
     */
    @Setter
    @Getter
    private long                ccLock;

    /**
     * KV扩展字段
     */
    private Map<String, String> attributes       = new HashMap<String, String>();


    public String getAttributes() {
        return (null != this.attributes ? FeatureUtil.toString(this.attributes) : "");
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public void setAttributes(String attributes) {
        Map<String, String> map = FeatureUtil.toMap(attributes);
        if (null != map && map.size() > 0) {
            this.attributes.putAll(map);
        }
    }

    public void addAttribute(String key, String value) {
        if (isAllowAttribute(key))
            this.attributes.put(key, value);
    }

    public abstract boolean isAllowAttribute(String key);

    public abstract String displayName();

}
