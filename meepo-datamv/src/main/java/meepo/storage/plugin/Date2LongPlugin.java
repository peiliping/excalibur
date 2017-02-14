package meepo.storage.plugin;

import com.google.common.collect.Lists;
import meepo.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.util.Date;
import java.util.List;

public class Date2LongPlugin extends IPlugin<Object[]> {

    private static final Logger LOG = LoggerFactory.getLogger(Date2LongPlugin.class);

    private Integer[] pos;

    public Date2LongPlugin(Config config) {
        super(config);
        List<Integer> t = Lists.newArrayList();
        for (int i = 0; i < config.getSourceTypesArray().size(); i++) {
            if (Types.TIMESTAMP == config.getSourceTypesArray().get(i) || Types.DATE == config.getSourceTypesArray().get(i)) {
                t.add(i);
            }
        }
        this.pos = new Integer[t.size()];
        t.toArray(pos);
    }

    @Override public Object[] convert(Object[] e) {
        for (Integer i : pos) {
            e[i] = ((Date) e[i]).getTime();
        }
        return e;
    }

}
