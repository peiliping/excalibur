package icesword.agent.util;

public enum Mode {

    OFF_LINE("offline"), ON_LINE("online");

    private String name;

    Mode(String name) {
        this.name = name;
    }

    public static Mode getMode(String name) {
        if (name != null && ON_LINE.name.equals(name)) {
            return ON_LINE;
        }
        return OFF_LINE;
    }

}
