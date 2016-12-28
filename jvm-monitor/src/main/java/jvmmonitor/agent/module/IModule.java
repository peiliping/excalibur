package jvmmonitor.agent.module;

import java.util.Map;

/**
 * Created by peiliping on 16-12-21.
 */
public interface IModule {

    public String getModuleName();

    public void monitor(long timestamp);

    public boolean changed();

    public void transform(long timestamp);

    public Map<String, long[][]> pullData();

}
