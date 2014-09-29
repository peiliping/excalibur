package pudge.entity;


public enum TopicType {

    TOPIC_TYPE_OTHER(0, "other"), TOPIC_TYPE_DATASOURCE(1, "datasource");

    private final int    key;

    private final String content;

    public int getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    private TopicType(int key, String content) {
        this.key = key;
        this.content = content;
    }

    public static TopicType getTopic(int key) {
        for (TopicType tt : TopicType.values()) {
            if (tt.getKey() == key) {
                return tt;
            }
        }
        return null;
    }
}
