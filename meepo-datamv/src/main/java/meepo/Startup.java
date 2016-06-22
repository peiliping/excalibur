package meepo;

import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.tools.PropertiesTool;

public class Startup {

	private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

	private static Options OPTIONS = (new Options()).addOption("s", "sourceDataConfig", true, "Source DS Config Path")
			.addOption("t", "targetDataConfig", true, "Target DS Config Path")
			.addOption("c", "mainConfig", true, "Main Config Path");

	public static void main(String... args) throws Exception {

		CommandLine cmd = (new DefaultParser()).parse(OPTIONS, args);

		Config config = new Config(PropertiesTool.loadFile(cmd.getOptionValue("c")));
		config.setSourceDataSource(PropertiesTool.createDataSource(cmd.getOptionValue("s")));
		config.setTargetDataSource(PropertiesTool.createDataSource(cmd.getOptionValue("t")));
		config.init().printConfig();

		Agent agent = new Agent(config);
		LOG.info("========== Start" + new Date() + " ==========");
		agent.run();

		checkAlive(agent);
	}

	private static void checkAlive(Agent agent) {
		while (!agent.getFINISHED().get()) {
			try {
				Thread.sleep(1000 * 30);
				agent.printLog().checkFinished();
			} catch (InterruptedException e) {
			}
		}
		System.exit(0);
	}

}
