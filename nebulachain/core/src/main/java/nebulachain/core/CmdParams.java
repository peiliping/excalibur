package nebulachain.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CmdParams {

    private Integer pid;

    private String  libPath;

    public CmdParams(String[] args) throws ParseException {
        Options options = (new Options()).addOption("p", "pid", true, "the pid of to be attaching jvm").addOption("l", "libpath", true, "jar lib path");
        CommandLine commandLine = (new DefaultParser()).parse(options, args);
        this.pid = Integer.valueOf(commandLine.getOptionValue("pid"));
        this.libPath = commandLine.getOptionValue("libpath").trim();
    }

    public String getAgentPath() {
        return libPath + "/agent.jar";
    }

    public String getCorePath() {
        return libPath + "/core.jar";
    }

}
