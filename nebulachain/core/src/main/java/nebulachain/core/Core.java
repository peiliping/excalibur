package nebulachain.core;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.github.ompc.greys.core.ClassDataSource;
import com.github.ompc.greys.core.manager.ReflectManager;

public class Core {

    private final Thread          jvmShutdownHooker = new Thread("nbc-shutdown-hooker") {
                                                        @Override
                                                        public void run() {
                                                            Core.this.destroy();
                                                        }
                                                    };

    private final ExecutorService executorService   = Executors.newCachedThreadPool(new ThreadFactory() {
                                                        @Override
                                                        public Thread newThread(Runnable r) {
                                                            final Thread t = new Thread(r, "nbc-execute-daemon");
                                                            t.setDaemon(true);
                                                            return t;
                                                        }
                                                    });

    private static volatile Core  core;

    public Core(Instrumentation inst) {
        initForManager(inst);
        Runtime.getRuntime().addShutdownHook(jvmShutdownHooker);
    }

    private void initForManager(final Instrumentation inst) {
        ReflectManager.Factory.initInstance(new ClassDataSource() {
            @Override
            public Collection<Class<?>> allLoadedClasses() {
                final Class<?>[] classArray = inst.getAllLoadedClasses();
                return null == classArray ? new ArrayList<Class<?>>() : Arrays.asList(classArray);
            }
        });
    }

    public static Core getInstance(final Instrumentation inst) {
        if (null == core) {
            synchronized (Core.class) {
                if (null == core) {
                    core = new Core(inst);
                }
            }
        }
        return core;
    }

    public void destroy() {
        Runtime.getRuntime().removeShutdownHook(jvmShutdownHooker);
        executorService.shutdown();
    }

}
