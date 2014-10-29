package meepo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import meepo.dao.BasicDao;
import meepo.reader.DefaultMysqlReader;
import meepo.storage.IStorage;
import meepo.storage.RamRingBufferStorage;
import meepo.tools.PropertiesTool;
import meepo.writer.DefaultMysqlWriter;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Main {

    private static final Logger    LOG      = LoggerFactory.getLogger(Main.class);

    public static volatile boolean FINISHED = false;

    public static void main(String[] args) {
        checkParams(args);
        // Init DataSource
        DataSource source = PropertiesTool.createDataSource(args[0]);
        DataSource target = PropertiesTool.createDataSource(args[1]);
        // Init Config
        Config config = new Config(PropertiesTool.loadFile(args[2]));
        if (config.needAutoInitStartEnd()) {
            config.initStartEnd(BasicDao.autoGetStartEndPoint(source, config.getSourceTableName(), config.getPrimaryKeyName()));
        }
        Pair<List<String>, Map<String, Integer>> psource = BasicDao.parserSchema(source, config.getSourceTableName(), config.getSourceColumsNames());
        config.setSourceColumsArray(psource.getLeft());
        config.setSourceColumsType(psource.getRight());
        Pair<List<String>, Map<String, Integer>> ptarget = BasicDao.parserSchema(target, config.getTargetTableName(), config.getTargetColumsNames());
        config.setTargetColumsArray(ptarget.getLeft());
        config.setTargetColumsType(ptarget.getRight());
        LOG.error("===Config json : " + JSON.toJSONString(config, SerializerFeature.PrettyFormat));
        // Product & Custom
        LOG.error("==Start=== " + new Date());
        final IStorage<Object[]> storage = new RamRingBufferStorage<Object[]>(config.getBufferSize());
        final ThreadPoolExecutor readerPool =
                new ThreadPoolExecutor(config.getReadersNum(), config.getReadersNum(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < config.getReadersNum(); i++) {
            readerPool.submit(new DefaultMysqlReader(storage, config, source, i));
        }
        final ThreadPoolExecutor writerPool =
                new ThreadPoolExecutor(config.getWritersNum(), config.getWritersNum(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < config.getWritersNum(); i++) {
            writerPool.submit(new DefaultMysqlWriter(storage, config, target));
        }
        LOG.error("===Init Finished ");
        // END
        checkAlive(storage);
    }

    private static void checkAlive(IStorage<Object[]> storage) {
        while (!FINISHED) {
            try {
                Thread.sleep(1000 * 60);
                LOG.warn("storage size :" + storage.getCurrentSize());
            } catch (InterruptedException e) {
                LOG.error("checkalive : ", e);
            }
        }
        System.exit(0);
    }

    private static void checkParams(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Params Invalid !");
            System.exit(-1);
        }
    }
}
