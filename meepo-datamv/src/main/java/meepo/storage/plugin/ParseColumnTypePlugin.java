package meepo.storage.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.Config;

public class ParseColumnTypePlugin extends IPlugin<Object[]> {

    private static final Logger LOG = LoggerFactory.getLogger(ParseColumnTypePlugin.class);

    public ParseColumnTypePlugin(Config config) {
        super(config);
    }

    @Override public Object[] convert(Object[] e) {
        for (int i = 0; i < e.length; i++)
            LOG.info("Column " + i + " : " + e[i].getClass().getSimpleName());
        return e;
    }

}
