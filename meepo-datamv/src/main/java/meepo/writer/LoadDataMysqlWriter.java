package meepo.writer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import meepo.Config;
import meepo.dao.BasicDao;
import meepo.storage.IStorage;

public class LoadDataMysqlWriter extends DefaultMysqlWriter {

	public LoadDataMysqlWriter(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
	}

	protected boolean sendData(final List<Object[]> datas) {
		StringBuilder builder = new StringBuilder();
		for (Object[] o : datas) {
			for (int i = 0; i < o.length; i++) {
				builder.append(o[i]);
				builder.append(i == o.length - 1 ? "\n" : "\t");
			}
		}
		byte[] bytes = builder.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);
		return BasicDao.excuteLoadData(config.getTargetDataSource(), SQL, is);
	}

	@Override
	protected String buildSQL() {
		return "LOAD DATA LOCAL INFILE 'sql.csv' INTO TABLE " + config.getTargetTableName()
				+ " FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' ";
	}
}
