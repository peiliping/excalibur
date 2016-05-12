package icesword.agent.data;


public class JvmItem {

    public int    pid;

    public String mainClass;

    public String mainArgs;

    public String vmArgs;

    public String vmFlags;

    public String vmVersion;

    public JvmItem(int pid) {
        this.pid = pid;
    }

}
