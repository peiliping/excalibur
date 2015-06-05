package phoenix.config;

import phoenix.config.Context;

import com.google.common.eventbus.Subscribe;


public class Main {

    public static void main(String[] args) throws Exception {
        // Main m = new Main();
        // WatchConfigFileService wcs = new WatchConfigFileService("all",
        // "/home/peiliping/dev/logs/config");
        // m.handle(wcs.getConfig());
        // wcs.regist(m);
        T_Variables v = new T_Variables();
        v.set_variablesGroupName("all");
        v.set_variablesConfigFilePath("conf/variables.conf");
        v.afterPropertiesSet();
        System.out.println("END");
    }

    @Subscribe
    public void handle(Context ct) {
        System.out.println(ct);
    }
}
