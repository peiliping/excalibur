package meepo.storage.plugin;

import com.google.common.collect.Lists;
import meepo.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.util.Date;
import java.util.List;

public class Long2DatePlugin extends IPlugin<Object[]> {

    private static final Logger LOG = LoggerFactory.getLogger(Long2DatePlugin.class);

    private Integer[] pos;

    public Long2DatePlugin(Config config) {
        super(config);
        List<Integer> t = Lists.newArrayList();
        for (int i = 0; i < config.getTargetTypesArray().size(); i++) {
            if (Types.TIMESTAMP == config.getTargetTypesArray().get(i) || Types.DATE == config.getTargetTypesArray().get(i)) {
                t.add(i);
            }
        }
        this.pos = new Integer[t.size()];
        t.toArray(pos);
    }

    @Override public Object[] convert(Object[] e) {
        for (Integer i : pos) {
            e[i] = new Date((Long) e[i]);
        }
        return e;
    }

}
