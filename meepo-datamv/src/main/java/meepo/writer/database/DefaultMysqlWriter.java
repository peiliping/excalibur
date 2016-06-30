package meepo.writer.database;

import java.sql.PreparedStatement;
import java.util.List;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.dao.ParamsICallable;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class DefaultMysqlWriter extends IWorker {

	public DefaultMysqlWriter(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
	}

	@Override
	public void work() {
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
		return BasicDao.excuteBatchAdd(config.getTargetDataSource(), SQL, new ParamsICallable<Object>() {
			@Override
			public void handleParams(PreparedStatement p) throws Exception {
				for (Object[] data : datas) {
					for (int i = 0; i < data.length; i++) {
						p.setObject(i + 1, data[i], config.getTargetTypesArray().get(i));
					}
					p.addBatch();
				}
			}
		});
	}

	@Override
	protected String buildSQL() {
		String v = "?";
		for (int i = 1; i < config.getTargetColumnsArray().size(); i++) {
			v = v + ",?";
		}
		return "INSERT INTO " + config.getTargetTableName() + " (" + config.getTargetColumnsNames() + ") VALUES ( " + v
				+ ")";
	}
}
