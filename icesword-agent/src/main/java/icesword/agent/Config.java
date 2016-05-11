package icesword.agent;

import java.util.concurrent.atomic.AtomicLong;

public class Config {

    public AtomicLong      groupId;
    public AtomicLong      appId;
    public volatile String agentIdentifier;
    public AtomicLong      interval;

}
