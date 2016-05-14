package icesword.agent.data.process;

import java.net.URL;
import java.util.List;

import sun.tools.jstat.Arguments;

public class JstatArguments extends Arguments {

    public JstatArguments(String[] args) throws IllegalArgumentException {
        super(args);
    }

    @Override
    public List<URL> optionsSources() {
        List<URL> result = super.optionsSources();
        // TODO add source
        // URL u = this.getClass().getResource("resources/jstat_options");
        // result.add(u);
        return result;
    }
}
