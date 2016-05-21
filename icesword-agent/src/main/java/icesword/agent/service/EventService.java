package icesword.agent.service;

import icesword.agent.Startup;
import icesword.agent.data.process.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;

public class EventService {

    private static AtomicLong        CURRENT_POSITION = new AtomicLong(0);

    private static List<List<Event>> DATA_POOL        = Lists.newArrayList();

    private static final int         FULL_SIZE        = 100;

    static {
        DATA_POOL.add(new ArrayList<Event>());
        DATA_POOL.add(new ArrayList<Event>());
    }

    public static synchronized void oOOo() {
        CURRENT_POSITION.addAndGet(1);
    }

    public static synchronized void addEvent(Event e) {
        if (!Startup.MODE.isRemoteMsg()) {
            return;
        }
        int p = Long.valueOf((CURRENT_POSITION.get() % 2)).intValue();
        if (DATA_POOL.get(p).size() > FULL_SIZE) {
            DATA_POOL.get(p).clear();
        }
        DATA_POOL.get(p).add(e);
    }

    public static synchronized List<Event> getLastOne() {
        int p = Long.valueOf(((CURRENT_POSITION.get() + 1) % 2)).intValue();
        return DATA_POOL.get(p);
    }

    public static synchronized void cleanLastOne() {
        int p = Long.valueOf(((CURRENT_POSITION.get() + 1) % 2)).intValue();
        DATA_POOL.get(p).clear();
    }
}
