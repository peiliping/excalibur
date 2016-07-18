package sinper.histogram;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sinper.histogram.reader.BaseReader;
import sinper.histogram.reader.ParserReader;
import sinper.histogram.reader.WindowHistogramReader;

public class Startup {

    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

    private static Options OPTIONS =
            (new Options()).addOption("s", "sourceData", true, "Source Data Path").addOption("m", "mode", true, "Mode").addOption("i", "interval", true, "window interval")
                    .addOption("r", "resultData", true, "Result Data Path");

    public static void main(String[] args) throws Exception {

        CommandLine cmd = (new DefaultParser()).parse(OPTIONS, args);

        String mode = cmd.getOptionValue("m");
        String sourceDataFilePath = cmd.getOptionValue("s");
        String resultDataFilePath = cmd.getOptionValue("r");

        if ("base".equals(mode)) {
            BaseReader br = new BaseReader(sourceDataFilePath, resultDataFilePath);
            br.handle();
        } else if ("timewindow".equals(mode)) {
            String windowInterval = cmd.getOptionValue("i");
            WindowHistogramReader whr = new WindowHistogramReader(sourceDataFilePath, resultDataFilePath, Long.valueOf(windowInterval));
            whr.handle();
        } else if ("parse".equals(mode)) {
            ParserReader pr = new ParserReader(resultDataFilePath);
            pr.parse();
        }


    }

}
