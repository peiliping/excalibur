package icesword.agent.data.process;


public class JvmItem {

    public int     pid;

    public String  mainClass;

    public String  mainArgs;

    public String  vmArgs;

    public String  vmFlags;

    public String  vmVersion;

    public boolean status = true;

    public String  errorString;

    public String  simpleDesc;

    public JvmItem(int pid) {
        this.pid = pid;
    }

    public void simpleDesc() {
        if (mainClass != null && mainClass.trim().length() > 0) {
            String[] v = mainClass.split("/");
            simpleDesc = v[v.length - 1];
        }
    }
}
