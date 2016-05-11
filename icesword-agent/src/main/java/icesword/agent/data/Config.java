package icesword.agent.data;

import java.util.concurrent.atomic.AtomicLong;

public class Config {

    public AtomicLong      groupId = new AtomicLong(0);
    public AtomicLong      appId   = new AtomicLong(0);
    public volatile String agentIdentifier;
    public AtomicLong      period  = new AtomicLong(0);

}
