package phoenix.quasar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import phoenix.datasource.DataSourceProxy;

public class Quasar2 {

	public static void main(final ApplicationContext applicationContext, int tn)
			throws SuspendExecution, InterruptedException {

		final ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 1000, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		final AtomicInteger total = new AtomicInteger(0);
		long start = System.currentTimeMillis();
		System.out.println("==============");
		int i = 0;
		while (i++ < tn)
			pool.submit(new Runnable() {

				@Override
				public void run() {
					int k = 0;
					while (k++ < 10) {
						DataSourceProxy phoenixDS = (DataSourceProxy) applicationContext.getBean("phoenixDS");
						excuteQuery(phoenixDS, "select count(1) from metric_data_entity_pt1m_2");
					}
					total.addAndGet(10);
				}
			});
		while (total.get() < tn * 10) {
			Strand.sleep(2);
		}
		System.out.println("T" + (System.currentTimeMillis() - start));
		pool.shutdown();
	}

	public static <E> E excuteQuery(DataSource ds, String sql) {
		Connection c = null;
		PreparedStatement p = null;
		try {
			c = ds.getConnection();
			p = c.prepareStatement(sql);
			ResultSet r = p.executeQuery();
			r.close();
			return null;
		} catch (Exception e) {

		} finally {
			try {
				if (p != null)
					p.close();
				if (c != null)
					c.close();
			} catch (SQLException e) {

			}
		}
		return null;
	}
}