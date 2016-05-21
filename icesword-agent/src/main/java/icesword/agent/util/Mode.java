package icesword.agent.util;

public enum Mode {

    OFF_LINE("offline", false), ON_LINE("online", true);

    private String  name;

    private boolean remoteMsg;

    public boolean isRemoteMsg() {
        return remoteMsg;
    }

    Mode(String name, boolean remoteMsg) {
        this.name = name;
        this.remoteMsg = remoteMsg;
    }

    public static Mode getMode(String name) {
        if (name != null && ON_LINE.name.equals(name)) {
            return ON_LINE;
        }
        return OFF_LINE;
    }

}
