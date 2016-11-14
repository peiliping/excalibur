package nebulachain.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * Hello world!
 *
 */
public class Startup {

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private static synchronized void main(final String args, final Instrumentation inst) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        File test = new File("/home/peiliping/dev/logs/abc.txt");
        try {
            test.createNewFile();
        } catch (IOException e) {
        }
    }
}
