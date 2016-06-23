package meepo.tools;

import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.Config;
import meepo.reader.DefaultMysqlReader;
import meepo.reader.SyncMysqlReader;
import meepo.storage.IStorage;
import meepo.writer.DefaultMysqlWriter;
import meepo.writer.LoadDataMysqlWriter;
import meepo.writer.NullWriter;
import meepo.writer.ReplaceMysqlWriter;

public abstract class IWorker implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(IWorker.class);

	protected volatile boolean RUN = true;

	protected IStorage<Object[]> buffer;

	protected Config config;

	protected DataSource dataSource;

	protected String SQL;

	protected int index;

	public IWorker(IStorage<Object[]> buffer, Config config, int index) {
		this.buffer = buffer;
		this.config = config;
		this.index = index;
		this.SQL = buildSQL();
	}

	@Override
	public void run() {
		LOG.info(this.getClass().getSimpleName() + "_" + index + " : Started . " + new Date());
		while (RUN) {
			work();
		}
		LOG.info(this.getClass().getSimpleName() + "_" + index + " : Finished . " + new Date());
	}

	protected abstract void work();

	protected abstract String buildSQL();

	public static IWorker create(Mode md, IStorage<Object[]> buffer, Config config, int index) {
		IWorker w = null;
		if (md == Mode.SIMPLEREADER)
			w = new DefaultMysqlReader(buffer, config, index);
		if (md == Mode.SYNCREADER)
			w = new SyncMysqlReader(buffer, config, index);
		if (md == Mode.SIMPLEWRITER)
			w = new DefaultMysqlWriter(buffer, config, index);
		if (md == Mode.REPLACEWRITER)
			w = new ReplaceMysqlWriter(buffer, config, index);
		if (md == Mode.LOADDATAWRITER)
			w = new LoadDataMysqlWriter(buffer, config, index);
		if (md == Mode.NULLWRITER)
			w = new NullWriter(buffer, config, index);
		return w;
	}

}
