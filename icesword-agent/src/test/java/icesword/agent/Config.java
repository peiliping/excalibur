package icesword.agent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Config {

    public AtomicLong      groupId = new AtomicLong(0);
    public AtomicLong      appId   = new AtomicLong(0);

    public AtomicInteger   period  = new AtomicInteger(0);
    public AtomicInteger   status  = new AtomicInteger(0); // 0 1

    public volatile String agentIdentifier;

}
