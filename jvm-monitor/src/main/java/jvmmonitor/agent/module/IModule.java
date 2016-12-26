package jvmmonitor.agent.module;

import java.util.Map;

/**
 * Created by peiliping on 16-12-21.
 */
public interface IModule {

    public void init();

    public void monitor(long timestamp);

    public void output(long timestamp);

    public boolean noChange();

    public String getModuleName();

    public Map<String, long[][]> pullData();

}
