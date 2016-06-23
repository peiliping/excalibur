package meepo.reader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.tuple.Pair;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class SyncMysqlReader extends IWorker {

	private long currentPos = 0;

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

	@Override
	protected String buildSQL() {
		return "SELECT " + config.getSourceColumnsNames() + " FROM " + config.getSourceTableName() + " WHERE "
				+ config.getPrimaryKeyName() + " > ? AND " + config.getPrimaryKeyName() + " <= ? "
				+ config.getSourceFilterSQL();
	}

	private boolean executeQuery(final long start, final long end) {
		Boolean result = BasicDao.excuteQuery(config.getSourceDataSource(), SQL, new ICallable<Boolean>() {
			@Override
			public void handleParams(PreparedStatement p) throws Exception {
				p.setLong(1, start);
				p.setLong(2, end);
			}

			@Override
			public Boolean handleResultSet(ResultSet r) throws Exception {
				while (r.next()) {
					Object[] item = new Object[config.getSourceColumnsArray().size()];
					for (int i = 1; i <= config.getSourceColumnsArray().size(); i++) {
						item[i - 1] = r.getObject(i);
					}
					buffer.add(item);
				}
				return true;
			}
		});
		return (result != null && result);
	}
}
