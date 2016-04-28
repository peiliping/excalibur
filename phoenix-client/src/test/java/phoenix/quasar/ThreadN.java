package phoenix.quasar;

public class ThreadN {

	public static void main(String[] args) {

		int i = 0;
		long start = System.currentTimeMillis();
		while (i++ < 100000)
			new Thread(new Runnable() {
				public void run() {
					int k = 1 + 1;
				}
			}).start();

		System.out.println(System.currentTimeMillis() - start);
	}
}
