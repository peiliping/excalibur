package meepo;

import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.tools.PropertiesTool;

public class Startup {

    public static Agent agent;

    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

    private static final long SLEEP = 15 * 1000;

    private static Options OPTIONS =
            (new Options()).addOption("s", "sourceDataConfig", true, "Source DS Config Path").addOption("t", "targetDataConfig", true, "Target DS Config Path")
                    .addOption("c", "mainConfig", true, "Main Config Path");

    public static void main(String... args) throws Exception {

        CommandLine cmd = (new DefaultParser()).parse(OPTIONS, args);

        Config config = new Config(PropertiesTool.loadFile(cmd.getOptionValue("c")));
        config.setSourceDataSource(PropertiesTool.createDataSource(cmd.getOptionValue("s")));
        config.setTargetDataSource(PropertiesTool.createDataSource(cmd.getOptionValue("t")));
        config.init().printConfig();

        agent = new Agent(config);
        LOG.info("========== Start" + new Date() + " ==========");
        agent.run();

        checkAlive(agent);
        close();
    }

    private static void checkAlive(Agent agent) {
        while (!agent.getFINISHED().get()) {
            try {
                Thread.sleep(SLEEP);
                agent.printLog().checkFinished();
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void close() {
        try {
            agent.killAll();
            Thread.sleep(SLEEP);
        } catch (Exception e) {
        }
    }

}
