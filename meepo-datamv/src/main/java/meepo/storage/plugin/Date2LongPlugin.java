package meepo.storage.plugin;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import meepo.Config;

import com.google.common.collect.Lists;

public class Date2LongPlugin extends IPlugin<Object[]> {

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
