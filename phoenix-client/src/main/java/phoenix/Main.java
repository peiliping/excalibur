package phoenix;

import phoenix.config.Context;

import com.google.common.eventbus.Subscribe;


public class Main {

    public static void main(String[] args) throws Exception {
        // Main m = new Main();
        // WatchConfigFileService wcs = new WatchConfigFileService("all",
        // "/home/peiliping/dev/logs/config");
        // m.handle(wcs.getConfig());
        // wcs.regist(m);
        V v = new V();
        v.set_variablesGroupName("all");
        v.set_variablesConfigFilePath("/home/peiliping/dev/logs/config");
        v.afterPropertiesSet();

        v.toLog();

    }

    @Subscribe
    public void handle(Context ct) {
        System.out.println(ct);
    }
}
