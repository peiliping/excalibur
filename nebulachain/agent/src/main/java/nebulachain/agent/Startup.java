package nebulachain.agent;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class Startup {

    private static volatile ClassLoader CLASSLOADER;

    private static volatile Object      CORE;

    public synchronized static void resetClassLoader() {
        CLASSLOADER = null;
    }

    public synchronized static void resetCore() {
        if (CORE != null) {
            
        }
        CORE = null;
    }

    private static void defineClassLoader(String coreJar) throws Throwable {

        final ClassLoader classLoader;

        // TODO ONLY TEST
        resetClassLoader();

        if (null != CLASSLOADER) {
            classLoader = CLASSLOADER;
        } else {
            classLoader = new CoreClassLoader(coreJar);
            final Class<?> adviceWeaverClass = classLoader.loadClass("com.github.ompc.greys.core.advisor.AdviceWeaver");
            Spy.initForFirst(classLoader,
                    adviceWeaverClass.getMethod("methodOnBegin", int.class, ClassLoader.class, String.class, String.class, String.class, Object.class, Object[].class),
                    adviceWeaverClass.getMethod("methodOnReturnEnd", Object.class, int.class), adviceWeaverClass.getMethod("methodOnThrowingEnd", Throwable.class, int.class),
                    adviceWeaverClass.getMethod("methodOnInvokeBeforeTracing", int.class, Integer.class, String.class, String.class, String.class),
                    adviceWeaverClass.getMethod("methodOnInvokeAfterTracing", int.class, Integer.class, String.class, String.class, String.class),
                    adviceWeaverClass.getMethod("methodOnInvokeThrowTracing", int.class, Integer.class, String.class, String.class, String.class, String.class),
                    Startup.class.getMethod("resetClassLoader"));
        }
        CLASSLOADER = classLoader;
    }

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private static synchronized void main(final String args, final Instrumentation inst) {
        try {
            String corePath = args.trim();
            inst.appendToBootstrapClassLoaderSearch(new JarFile(Startup.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
            defineClassLoader(corePath);
            final Class<?> coreClass = CLASSLOADER.loadClass("nebulachain.core.Core");
            CORE = coreClass.getMethod("getInstance", Instrumentation.class).invoke(null, inst);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
