package meepo.reader;

import meepo.Config;
import meepo.tools.RingBuffer;

public class DefaultMysqlReader implements Runnable {

    private final RingBuffer<Object[]> buffer;

    private final Config               config;

    public DefaultMysqlReader(RingBuffer<Object[]> buffer, Config config) {
        this.buffer = buffer;
        this.config = config;
    }

    @Override
    public void run() {

    }

}
