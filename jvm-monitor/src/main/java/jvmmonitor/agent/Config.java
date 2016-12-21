package jvmmonitor.agent;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jvmmonitor.agent.module.*;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * Created by peiliping on 16-12-19.
 */


public class Config {

    // metric monitor interval
    @Getter private int interval;

    // filter info
    private Set<Integer> targetPids;
    private Set<String>  excludeKeyWords;
    private static final Set<String> EXCLUDEKEYWORDS_CONS = Sets.newHashSet();

    static {
        EXCLUDEKEYWORDS_CONS.add("sun.tools");
        EXCLUDEKEYWORDS_CONS.add("jvmmonitor.agent.Startup");
    }

    @Getter private Set<String> modules;
    public static final Map<String, Class<? extends AbstractModule>> MODULES_CONS = Maps.newHashMap();

    static {
        MODULES_CONS.put("class", ModuleZClass.class);
        MODULES_CONS.put("gc", ModuleZGC.class);
        MODULES_CONS.put("memory", ModuleZMemory.class);
        MODULES_CONS.put("thread", ModuleZThread.class);
    }

    public Config(int interval, Set<Integer> targetPids, Set<String> excludeKeyWords, Set<String> modules) {
        this.interval = interval;
        this.targetPids = targetPids;
        this.excludeKeyWords = excludeKeyWords;
        this.excludeKeyWords.addAll(EXCLUDEKEYWORDS_CONS);
        this.modules = modules;
        if (modules.size() == 0) {
            modules.addAll(MODULES_CONS.keySet());
        }
    }

    public boolean filterPid(Integer id) {
        if (targetPids.isEmpty()) {
            return true;
        } else {
            return targetPids.contains(id);
        }
    }

    public boolean filterKeyWords(String mainClass) {
        for (String kw : excludeKeyWords) {
            if (mainClass.matches(kw)) {
                return true;
            }
        }
        return false;
    }

}
