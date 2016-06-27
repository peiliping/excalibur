package meepo.reader;

import org.apache.commons.lang3.tuple.Pair;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.storage.IStorage;

public class SyncMysqlReader extends DefaultMysqlReader {

	public SyncMysqlReader(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
		this.currentPos = config.getStart();
	}

	@Override
	public void work() {
		Pair<Long, Long> p = BasicDao.autoGetStartEndPoint(config.getSourceDataSource(), config.getSourceTableName(),
				config.getPrimaryKeyName());
		long thisLoopEndPoint = (config.getEndDelay() == null ? p.getRight() : p.getRight() - config.getEndDelay());
		while (currentPos < thisLoopEndPoint) {
			long theEnd = (thisLoopEndPoint - currentPos >= config.getReaderStepSize())
					? currentPos + config.getReaderStepSize() : thisLoopEndPoint;
			boolean status = executeQuery(currentPos, theEnd);
			if (status) {
				currentPos = theEnd;
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
