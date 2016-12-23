package jvmmonitor.agent.module;

/**
 * Created by peiliping on 16-12-21.
 */
public interface IModule {

    public void init();

    public void monitor();

    public void output();

    public boolean noChange();

    public String getModuleName();

}
