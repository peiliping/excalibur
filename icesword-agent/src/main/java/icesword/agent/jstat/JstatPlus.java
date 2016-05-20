package icesword.agent.jstat;

import icesword.agent.service.JstatMonitorService;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.cli.CommandLine;

public abstract class JstatPlus implements Runnable {

    protected int                 monitorIntervel    = 1000;

    protected AtomicBoolean       running            = new AtomicBoolean(false);

    protected AtomicLong          coordinateIntervel = new AtomicLong(60 * 1000);

    protected JstatMonitorService jstatPool          = new JstatMonitorService();

    protected CommandLine         commandLine;

    public JstatPlus(CommandLine commandLine, int monitorIntervel) {
        this.commandLine = commandLine;
        this.monitorIntervel = monitorIntervel;
        this.running.set(true);
    }

    protected abstract void fly();

    @Override
    public void run() {
        while (running.get()) {
            try {
                fly();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                jstatPool.cleanDoneFuture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(coordinateIntervel.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
