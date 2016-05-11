package icesword.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App {

    public static String           configServerAddress;

    private static ExecutorService diamond        = Executors.newFixedThreadPool(10);

    private static Config          config         = new Config();

    private static MonitorService  monitorService = new MonitorService();

    public static void main(String[] args) {
        parseArgs(args);
        diamond.submit(new Pendulum(configServerAddress, config));
        // Hold
    }


    private static void parseArgs(String[] args) {
        if (args != null && args.length > 0) {
            configServerAddress = args[0];
        } else {
            System.out.println("Params Missing");
            System.exit(1);
        }
    }
}
