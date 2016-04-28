package phoenix.quasar;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

public class Quasar {

	public static void main(String[] args) {

		int i = 0;
		long start = System.currentTimeMillis();
		while (i++ < 100000)
			new Fiber<Void>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected Void run() throws SuspendExecution, InterruptedException {
					int k = 1 + 1;
					return super.run();
				}
			}.start();

		System.out.println(System.currentTimeMillis() - start);
	}
}
