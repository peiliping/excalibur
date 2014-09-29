package pudge.entity;


public enum InfoType {

    INFO_TYPE_INIT(1, "init"), INFO_TYPE_ADD(2, "add"), INFO_TYPE_UPDATE(3, "update"), INFO_TYPE_DELETE(4, "delete");

    private final int    key;

    private final String content;

    public int getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    private InfoType(int key, String content) {
        this.key = key;
        this.content = content;
    }

    public static InfoType getInfoType(int key) {
        for (InfoType ma : InfoType.values()) {
            if (ma.getKey() == key) {
                return ma;
            }
        }
        return null;
    }
}
