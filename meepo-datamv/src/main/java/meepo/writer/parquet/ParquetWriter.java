package meepo.writer.parquet;

import java.io.IOException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.OriginalType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type.Repetition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import meepo.Config;
import meepo.Startup;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class ParquetWriter extends IWorker {

	private static final Logger LOG = LoggerFactory.getLogger(ParquetWriter.class);

	private static final Map<Integer, PrimitiveTypeName> MAPPING = Maps.newHashMap();

	static {
		MAPPING.put(Types.TINYINT, PrimitiveTypeName.INT32);
		MAPPING.put(Types.SMALLINT, PrimitiveTypeName.INT32);
		MAPPING.put(Types.INTEGER, PrimitiveTypeName.INT32);
		MAPPING.put(Types.BIGINT, PrimitiveTypeName.INT64);

		MAPPING.put(Types.BOOLEAN, PrimitiveTypeName.BOOLEAN);

		MAPPING.put(Types.REAL, PrimitiveTypeName.FLOAT);
		MAPPING.put(Types.FLOAT, PrimitiveTypeName.FLOAT);
		MAPPING.put(Types.DOUBLE, PrimitiveTypeName.DOUBLE);

		MAPPING.put(Types.CHAR, PrimitiveTypeName.BINARY);
		MAPPING.put(Types.VARCHAR, PrimitiveTypeName.BINARY);
		MAPPING.put(Types.LONGVARCHAR, PrimitiveTypeName.BINARY);

	}

	private ParquetWriterHelper writerHelper;

	public ParquetWriter(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
		try {
			List<Type> types = Lists.newArrayList();
			for (Map.Entry<String, Integer> item : config.getTargetColumnsType().entrySet()) {
				Validate.notNull(MAPPING.get(item.getValue()));
				if (MAPPING.get(item.getValue()) == PrimitiveTypeName.BINARY) {
					types.add(new PrimitiveType(Repetition.OPTIONAL, PrimitiveTypeName.BINARY, item.getKey(),
							OriginalType.UTF8));
				} else {
					types.add(new PrimitiveType(Repetition.OPTIONAL, MAPPING.get(item.getValue()), item.getKey()));
				}
			}
			String path = config.getParquetOutputPath() + config.getTargetTableName() + "-" + index + "-"
					+ System.currentTimeMillis()/1000 + ".parquet";
			this.writerHelper = new ParquetWriterHelper(new Path(path),
					new MessageType(config.getTargetTableName(), types));
		} catch (IllegalArgumentException | IOException e) {
			LOG.error("Init Writer Helper", e);
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
				LOG.error("Close Writer Helper", e);
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
			} catch (Exception e) {
				LOG.error("ParquetWriter Write Data Error :", e);
			}
		}
		return true;
	}

	@Override
	protected String buildSQL() {
		return null;
	}

}
