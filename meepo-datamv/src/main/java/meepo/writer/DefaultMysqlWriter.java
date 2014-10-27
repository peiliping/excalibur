package meepo.writer;

import meepo.Config;
import meepo.tools.RingBuffer;

public class DefaultMysqlWriter implements Runnable {

    private final RingBuffer<Object[]> buffer;

    private final Config               config;

    public DefaultMysqlWriter(RingBuffer<Object[]> buffer, Config config) {
        this.buffer = buffer;
        this.config = config;
    }

    @Override
    public void run() {

    }

}
