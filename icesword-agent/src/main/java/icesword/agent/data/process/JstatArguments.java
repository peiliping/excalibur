package icesword.agent.data.process;

import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;

import sun.tools.jstat.Arguments;

public class JstatArguments extends Arguments {

    public JstatArguments(String[] args) throws IllegalArgumentException {
        super(args);
    }

    @Override
    public List<URL> optionsSources() {
        List<URL> result = Lists.newArrayList();
        URL u = this.getClass().getResource("jstat_options");
        result.add(u);
        return result;
    }
}
