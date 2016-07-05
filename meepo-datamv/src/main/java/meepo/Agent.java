package meepo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import meepo.storage.IStorage;
import meepo.storage.RamRingBufferStorage;
import meepo.storage.plugin.IPlugin;
import meepo.tools.IWorker;
import meepo.tools.Mode;

public class Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

    @Getter private AtomicBoolean FINISHED = new AtomicBoolean(false);

    private Config config;

    private IStorage<Object[]> storage;

    private ThreadPoolExecutor readersPool;

    private ThreadPoolExecutor writersPool;

    @SuppressWarnings("unchecked") public Agent(Config config) {
        this.config = config;
        this.storage = new RamRingBufferStorage<Object[]>(config.getBufferSize());
        if (config.getPluginClass() != null) {
            try {
                this.storage.addPlugin((IPlugin<Object[]>) config.getPluginClass().getConstructor(Config.class).newInstance(config));
            } catch (Exception e) {
                LOG.error("Load Plugin Error :", e);
            }
        }
        this.writersPool = new ThreadPoolExecutor(config.getWritersNum(), config.getWritersNum(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.readersPool = new ThreadPoolExecutor(config.getReadersNum(), config.getReadersNum(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public Agent run() {
        submit(config.getWritersNum(), config.getTargetMode(), writersPool);
        submit(config.getReadersNum(), config.getSourceMode(), readersPool);
        return this;
    }

    private void submit(int size, Mode md, ThreadPoolExecutor pool) {
        for (int i = 0; i < size; i++) {
            IWorker w = IWorker.create(md, storage, config, i);
            pool.submit(w);
        }
    }

    public Agent checkFinished() {
        if (readersPool.getActiveCount() == 0 && storage.getCurrentSize() == 0) {
            FINISHED.set(true);
        }
        return this;
    }

    public void killAll() {
        readersPool.shutdown();
        writersPool.shutdown();
    }

    public Agent printLog() {
        LOG.info("Storage Size : " + storage.getCurrentSize());
        LOG.info("ReadersPool Size : " + readersPool.getActiveCount());
        LOG.info("WritersPool Size : " + writersPool.getActiveCount());
        return this;
    }
}
