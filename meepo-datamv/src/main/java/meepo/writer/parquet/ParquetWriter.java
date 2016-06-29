package meepo.writer.parquet;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meepo.Config;
import meepo.Startup;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class ParquetWriter extends IWorker {

	private static final Logger LOG = LoggerFactory.getLogger(ParquetWriter.class);

	private ParquetWriterHelper writerHelper;

	public ParquetWriter(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
		try {
			this.writerHelper = new ParquetWriterHelper(new Path(""), null);// TODO
		} catch (IllegalArgumentException | IOException e) {
			// TODO LOG
		}
	}

	@Override
	protected void work() {
		if (Startup.agent.getFINISHED().get()) {
			try {
				this.writerHelper.close();
				super.RUN = false;
				return;
			} catch (IOException e) {
				// TODO LOG
			}
		}

		final List<Object[]> datas = buffer.get(config.getWriterStepSize());
		if (datas.isEmpty())
			return;

		long t = 0;
		while (!sendData(datas)) {
			try {
				Thread.sleep(100 * t++);
			} catch (InterruptedException e) {
			}
		}
	}

	protected boolean sendData(final List<Object[]> datas) {
		for (Object[] data : datas) {
			try {
				writerHelper.write(data);
			} catch (IOException e) {
				// TODO
			}
		}
		return true;
	}

	@Override
	protected String buildSQL() {
		return null;
	}

}
