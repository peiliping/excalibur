package pudge.entity;

public class Message {

    private Topic  topic;

    private long   timestamp;

    private String md5;

    private Info   entireInfo;

    private Info   diffInfo;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Info getEntireInfo() {
        return entireInfo;
    }

    public void setEntireInfo(Info entireInfo) {
        this.entireInfo = entireInfo;
    }

    public Info getDiffInfo() {
        return diffInfo;
    }

    public void setDiffInfo(Info diffInfo) {
        this.diffInfo = diffInfo;
    }

}
