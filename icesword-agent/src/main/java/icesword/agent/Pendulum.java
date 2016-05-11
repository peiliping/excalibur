package icesword.agent;

import java.util.ArrayList;
import java.util.List;


/**
 * 1 Send Healthy MSG
 * 
 * 2 Update Config Info
 * 
 * 3 Find JVM Pid
 * 
 * @author peiliping
 *
 */
public class Pendulum implements Runnable {

    private List<ClientStatus> clientStatus = new ArrayList<ClientStatus>();

    public String              configServerAddress;
    public Config              config;

    public static final String path         = "/config";

    public Pendulum(String configServerAddress, Config config) {
        this.configServerAddress = configServerAddress;
        this.config = config;
    }

    @Override
    public void run() {
        while (true) {
            try {
                
                Thread.sleep(60 * 1000);
            } catch (Exception e) {
            }
        }
    }

}
