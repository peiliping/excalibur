package phoenix.quasar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import phoenix.datasource.DataSourceProxy;

public class Quasar {

	public static void main(final ApplicationContext applicationContext, int tn)
			throws SuspendExecution, InterruptedException, ExecutionException {

		final AtomicInteger total = new AtomicInteger(0);
		long start = System.currentTimeMillis();
		System.out.println("==============");
		int i = 0;
		while (i++ < tn) {
			Fiber f = new Fiber<Void>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected Void run() throws SuspendExecution, InterruptedException {
					int k = 0;
					while (k++ < 10) {
						DataSourceProxy phoenixDS = (DataSourceProxy) applicationContext.getBean("phoenixDS");
						excuteQuery(phoenixDS, "select count(1) from metric_data_entity_pt1m_2");
					}
					total.addAndGet(10);
					return super.run();
				}
			};
			f.start();
		}
		while (total.get() < tn * 10) {
			Strand.sleep(2);
		}
		System.out.println("F" + (System.currentTimeMillis() - start));
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