package phoenix.datasource;

import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

public class DruidMonitorFilter extends FilterEventAdapter {

	protected static Logger LOG = LoggerFactory.getLogger(DruidMonitorFilter.class);

	private String dbType;

	private String name;

	public DruidMonitorFilter() {
	}

	@Override
	public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
		ConnectionProxy connection = null;
		if (this.dbType == null) {
			this.dbType = chain.getDataSource().getDbType();
		}
		if (this.name == null) {
			this.name = chain.getDataSource().getName();
		}

		long start = System.currentTimeMillis();
		try {
			connection = chain.connection_connect(info);
			long span = System.currentTimeMillis() - start;
			if (LOG.isInfoEnabled()) {
				LOG.info("Create " + dbType + "-" + name + " Connect :" + span);
			}
		} catch (SQLException ex) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Create " + dbType + "-" + name + " Connect Error " + ex.getMessage() + " :" + 1);
			}
			throw ex;
		}
		return connection;
	}

	@Override
	protected void statementCreateAfter(StatementProxy statement) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Create Statement : " + statement.getBatchSql() != null ? statement.getBatchSql() : "");
		}
		super.statementCreateAfter(statement);
	}

	@Override
	protected void statementPrepareAfter(PreparedStatementProxy statement) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Create Statement : " + statement.getSql());
		}
		super.statementPrepareAfter(statement);
	}

	@Override
	public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
		double span = ((System.nanoTime() - resultSet.getConstructNano()) / 1000000.0d);
		if (LOG.isInfoEnabled()) {
			LOG.info("Resultset : " + resultSet.getFetchRowCount() + " cost : " + span);
		}
		chain.resultSet_close(resultSet);
	}

	@Override
	protected void statementExecuteBefore(StatementProxy statement, String sql) {
		statement.setLastExecuteStartNano();
		super.statementExecuteBefore(statement, sql);
	}

	@Override
	protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
		statement.setLastExecuteTimeNano();
		double span = statement.getLastExecuteTimeNano() / 1000000.0d;
		if (LOG.isInfoEnabled()) {
			LOG.info("Statement : " + statement.getLastExecuteType() + " cost : " + span + " sql : " + sql + " name : "
					+ name);
		}
		super.statementExecuteAfter(statement, sql, result);
	}

}
