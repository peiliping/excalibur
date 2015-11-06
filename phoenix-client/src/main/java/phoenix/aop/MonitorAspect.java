package phoenix.aop;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

@Aspect
public class MonitorAspect implements InitializingBean {

    private final MetricRegistry CORE = new MetricRegistry();

    private Reporter             REPORT;

    /**
     * 业务、团队名称（ex： AI、BI、MI）
     */
    @Getter
    @Setter
    private String               groupName;

    /**
     * 系统名称（ex : DC、DV、Consumer）
     */
    @Getter
    @Setter
    private String               appName;

    /**
     * 自定义结果检查方法
     */
    @Getter
    @Setter
    private CheckResult          cr;

    /**
     * meterLogger的名字
     */
    @Getter
    @Setter
    private String               meterLoggerName;

    /**
     * timerLogger的名字
     */
    @Getter
    @Setter
    private String               timerLoggerName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.isTrue(StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(appName));
        Validate.isTrue(StringUtils.isNotBlank(meterLoggerName) && StringUtils.isNotBlank(timerLoggerName));
        this.REPORT =
                Reporter.forRegistry(CORE).outputTo(LoggerFactory.getLogger(meterLoggerName), LoggerFactory.getLogger(timerLoggerName))
                        .prefixedWith(MetricRegistry.name(groupName, appName)).build();
        this.REPORT.start(1, TimeUnit.MINUTES);
    }

    @Pointcut(value = "@annotation(phoenix.aop.Monitor)")
    public void _base() {}

    @Around(value = "_base()")
    public void around(ProceedingJoinPoint p) throws Throwable {
        Method md = getMethod(p);
        String baseName = getMetricName(md);
        Object r = null;

        final Timer tr = CORE.timer(baseName);
        final Timer.Context context = tr.time();

        try {
            r = p.proceed();
        } catch (Throwable e) {
            CORE.meter(baseName + "_EXCEPTION").mark();;
            throw e;
        } finally {
            if (r != null && cr != null) {
                if (cr.checkError(md, r)) {
                    CORE.meter(baseName + "_ERROR").mark();;
                }
            }
            context.stop();
        }
    }

    public <T> Object custom4Timer(String metricName, Callable<T> call) throws Exception {
        final Timer tr = this.CORE.timer(metricName);
        Timer.Context ct = tr.time();
        try {
            return call.call();
        } finally {
            ct.stop();
        }
    }

    public void custom4Mark(String metricName) {
        this.CORE.meter(metricName).mark();
    }
    
    public void stopReport() {
        if (this.REPORT != null) {
            this.REPORT.stop();
        }
    }

    private static Method getMethod(final ProceedingJoinPoint p) {
        MethodSignature ms = (MethodSignature) p.getSignature();
        return ms.getMethod();
    }

    private static String getMetricName(Method md) {
        Monitor ma = md.getAnnotation(Monitor.class);
        return ma.metricName();
    }
}
