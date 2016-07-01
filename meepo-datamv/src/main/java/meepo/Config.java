package meepo;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import meepo.dao.BasicDao;
import meepo.tools.Mode;

@Setter
@Getter
public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	private Mode sourceMode;
	private transient DataSource sourceDataSource;
	private String sourceTableName;
	private String primaryKeyName;
	private int readerStepSize;
	private int readersNum;
	private String sourceColumnsNames;
	private Map<String, Integer> sourceColumnsType = Maps.newIdentityHashMap();
	private transient List<String> sourceColumnsArray = Lists.newArrayList();
	private transient List<Integer> sourceTypesArray = Lists.newArrayList();
	private String sourceExtraSQL;

	// ----------------------------------------------------------------------

	private Mode targetMode;
	private transient DataSource targetDataSource;
	private String targetTableName;
	private int writerStepSize;
	private int writersNum;
	private String targetColumnsNames;
	private Map<String, Integer> targetColumnsType = Maps.newIdentityHashMap();
	private transient List<String> targetColumnsArray = Lists.newArrayList();
	private transient List<Integer> targetTypesArray = Lists.newArrayList();

	private String parquetOutputPath;

	// ----------------------------------------------------------------------

	private int bufferSize;
	private Long start; // start为实际最小值-1
	private Long end; // end为实际最大值
	private Long endDelay; // 用于sync 控制时间的参数
	private Class<?> pluginClass;

	public Config(Properties ps) throws Exception {
		// ==================Required Config Item===================
		this.sourceTableName = ps.getProperty("sourceTableName");
		this.targetTableName = ps.getProperty("targetTableName");
		this.sourceColumnsNames = ps.getProperty("sourceColumnsNames");
		this.targetColumnsNames = ps.getProperty("targetColumnsNames");
		Validate.notNull(this.sourceTableName);
		Validate.notNull(this.targetTableName);
		Validate.notNull(this.sourceColumnsNames);
		Validate.notNull(this.targetColumnsNames);
		// =========================================================
		this.sourceMode = Mode.valueOf(ps.getProperty("sourceMode", Mode.SIMPLEREADER.name()));
		this.primaryKeyName = ps.getProperty("primaryKeyName", "id");
		this.readerStepSize = Integer.valueOf(ps.getProperty("readerStepSize", "100"));
		this.readersNum = Integer.valueOf(ps.getProperty("readersNum", "1"));
		this.sourceExtraSQL = ps.getProperty("sourceExtraSQL", "");
		this.targetMode = Mode.valueOf(ps.getProperty("targetMode", Mode.SIMPLEWRITER.name()));
		this.writerStepSize = Integer.valueOf(ps.getProperty("writerStepSize", "100"));
		this.writersNum = Integer.valueOf(ps.getProperty("writersNum", "1"));

		this.parquetOutputPath = ps.getProperty("parquetOutputPath");

		this.bufferSize = Integer.valueOf(ps.getProperty("bufferSize", "8192"));
		this.start = ps.getProperty("start") == null ? null : Long.valueOf(ps.getProperty("start"));
		this.end = ps.getProperty("end") == null ? null : Long.valueOf(ps.getProperty("end"));
		this.endDelay = ps.getProperty("endDelay") == null ? null : Long.valueOf(ps.getProperty("endDelay"));

		if (this.sourceMode == Mode.SYNCREADER) {
			Validate.isTrue(this.readersNum == 1, "Mode Sync ReadersNum Must Be 1 .");
			this.end = Long.MAX_VALUE;
		}

		this.pluginClass = ps.getProperty("pluginClass") == null ? null : Class.forName(ps.getProperty("pluginClass"));
	}

	public Config init() {
		// handle Start & End
		if (this.start == null || this.end == null) {
			Pair<Long, Long> ps = BasicDao.autoGetStartEndPoint(this.sourceDataSource, this.sourceTableName,
					this.primaryKeyName);
			if (this.sourceMode == Mode.SYNCREADER) {
				if (this.start == null)
					this.start = ps.getRight();
			} else {
				if (this.start == null)
					this.start = ps.getLeft();
				if (this.end == null)
					this.end = ps.getRight();
			}
		}
		// handle Columns
		handleColumns(sourceDataSource, sourceTableName, sourceColumnsNames, sourceColumnsType, sourceColumnsArray,
				sourceTypesArray);
		handleColumns(targetDataSource, targetTableName, targetColumnsNames, targetColumnsType, targetColumnsArray,
				targetTypesArray);
		return this;
	}

	private void handleColumns(DataSource ds, String tableName, String cols, Map<String, Integer> columnsType,
			List<String> columns, List<Integer> types) {
		Triple<List<String>, List<Integer>, Map<String, Integer>> result = BasicDao.parserSchema(ds, tableName, cols);
		columns.addAll(result.getLeft());
		types.addAll(result.getMiddle());
		columnsType.putAll(result.getRight());
	}

	public Config printConfig() {
		LOG.info("  Config JSON : " + JSON.toJSONString(this, SerializerFeature.PrettyFormat));
		return this;
	}
}
