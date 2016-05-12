package icesword.agent.data;

import java.util.HashMap;
import java.util.Map;

public class JVMMemoryData {

    public String              name;

    public String              parent;

    public double              used;

    public double              capacity;

    public long                timestamp;

    public Map<String, String> tags = new HashMap<String, String>();

    public JVMMemoryData(String name, String parent, double used, double capacity) {
        this.name = name;
        this.parent = parent;
        this.used = used;
        this.capacity = capacity;
        this.timestamp = System.currentTimeMillis();
    }

    public JVMMemoryData addTag(String key, String value) {
        tags.put(key, value);
        return this;
    }

}
