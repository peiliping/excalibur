package sun.tools.jstat;

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
        URL url = this.getClass().getResource("/jstat_options");
        List<URL> result = Lists.newArrayList(url);
        return result;
    }
}
