package meepo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import meepo.dao.BasicDao;
import meepo.reader.DefaultMysqlReader;
import meepo.tools.PropertiesTool;
import meepo.tools.RingBuffer;
import meepo.writer.DefaultMysqlWriter;

import org.apache.commons.lang3.tuple.Pair;

public class Main {

    public static volatile boolean FINISHED = false;

    public static void main(String[] args) {
        // TEST
        args = new String[3];
        args[0] = "/home/peiliping/dev/logs/meepo-source.conf";
        args[1] = "/home/peiliping/dev/logs/meepo-target.conf";
        args[2] = "/home/peiliping/dev/logs/meepo.conf";
        //
        checkParams(args);
        DataSource source = PropertiesTool.createDataSource(args[0]);
        DataSource target = PropertiesTool.createDataSource(args[1]);

        Config config = new Config(PropertiesTool.loadFile(args[2]));
        if (config.needAutoInitStartEnd()) {
            config.initStartEnd(BasicDao.autoGetPoint(source, config.getSourceTableName(), config.getPrimaryKeyName()));
        }
        Pair<List<String>, Map<String, Integer>> psource = BasicDao.parserSchema(source, config.getSourceTableName(), config.getSourceColumsNames());
        config.setSourceColumsArray(psource.getLeft());
        config.setSourceSchema(psource.getRight());
        Pair<List<String>, Map<String, Integer>> ptarget = BasicDao.parserSchema(target, config.getTargetTableName(), config.getTargetColumsNames());
        config.setTargetColumsArray(ptarget.getLeft());
        config.setTargetSchema(ptarget.getRight());

        final RingBuffer<Object[]> BUFFER = new RingBuffer<Object[]>(config.getBufferSize());
        final ThreadPoolExecutor readerPool = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < config.getReadersNum(); i++) {
            readerPool.submit(new DefaultMysqlReader(BUFFER, config));
        }
        final ThreadPoolExecutor writerPool = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < config.getWritersNum(); i++) {
            writerPool.submit(new DefaultMysqlWriter(BUFFER, config));
        }
        checkAlive();
    }

    private static void checkAlive() {
        while (!FINISHED) {
            try {
                Thread.sleep(1000 * 60);
                // TODO LOG
            } catch (InterruptedException e) {
                // TODO LOG
            }
        }
    }

    private static void checkParams(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Params Invalid !");
            System.exit(-1);
        }
    }
}
