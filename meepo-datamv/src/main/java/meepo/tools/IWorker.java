package meepo.tools;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.Config;
import meepo.Startup;
import meepo.storage.IStorage;

public abstract class IWorker implements Runnable {

    protected static final Logger LOG = LoggerFactory.getLogger(IWorker.class);

    protected boolean RUN = true;

    protected IStorage<Object[]> buffer;

    protected Config config;

    protected String SQL;

    protected int index;

    public IWorker(IStorage<Object[]> buffer, Config config, int index) {
        this.buffer = buffer;
        this.config = config;
        this.index = index;
        this.SQL = buildSQL();
    }

    @Override public void run() {
        start();
        while (RUN && !Startup.agent.getFINISHED().get()) {
            work();
        }
        close();
    }

    protected void start() {
        RUN = true;
        LOG.info(this.getClass().getSimpleName() + "_" + index + " : Started . " + new Date());
    }

    protected void close() {
        RUN = false;
        LOG.info(this.getClass().getSimpleName() + "_" + index + " : Finished . " + new Date());
    }

    protected abstract void work();

    protected String buildSQL() {
        return null;
    }

    public static IWorker create(Mode md, IStorage<Object[]> buffer, Config config, int index) {
        IWorker w = null;
        try {
            w = (IWorker) md.clazz.getDeclaredConstructor(IStorage.class, Config.class, int.class).newInstance(buffer, config, index);
        } catch (Exception e) {
            LOG.error("Create Worker Error :", e);
        }
        return w;
    }

}
