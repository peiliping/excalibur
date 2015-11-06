package phoenix.aop;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;


public class Reporter extends ScheduledReporter {
    /**
     * Returns a new {@link Builder} for {@link Slf4jReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link Slf4jReporter}
     */
    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public enum LoggingLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    /**
     * A builder for {@link Slf4jReporter} instances. Defaults to logging to {@code metrics}, not
     * using a marker, converting rates to events/second, converting durations to milliseconds, and
     * not filtering metrics.
     */
    public static class Builder {
        private final MetricRegistry registry;
        private Logger               logger;
        private LoggingLevel         loggingLevel;
        private Marker               marker;
        private String               prefix;
        private TimeUnit             rateUnit;
        private TimeUnit             durationUnit;
        private MetricFilter         filter;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.logger = LoggerFactory.getLogger("metrics");
            this.marker = null;
            this.prefix = "";
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.loggingLevel = LoggingLevel.INFO;
        }

        /**
         * Log metrics to the given logger.
         *
         * @param logger an SLF4J {@link Logger}
         * @return {@code this}
         */
        public Builder outputTo(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Mark all logged metrics with the given marker.
         *
         * @param marker an SLF4J {@link Marker}
         * @return {@code this}
         */
        public Builder markWith(Marker marker) {
            this.marker = marker;
            return this;
        }

        /**
         * Prefix all metric names with the given string.
         *
         * @param prefix the prefix for all metric names
         * @return {@code this}
         */
        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Use Logging Level when reporting.
         *
         * @param loggingLevel a (@link Slf4jReporter.LoggingLevel}
         * @return {@code this}
         */
        public Builder withLoggingLevel(LoggingLevel loggingLevel) {
            this.loggingLevel = loggingLevel;
            return this;
        }

        /**
         * Builds a {@link Slf4jReporter} with the given properties.
         *
         * @return a {@link Slf4jReporter}
         */
        public Reporter build() {
            LoggerProxy loggerProxy;
            switch (loggingLevel) {
                case TRACE:
                    loggerProxy = new TraceLoggerProxy(logger);
                    break;
                case INFO:
                    loggerProxy = new InfoLoggerProxy(logger);
                    break;
                case WARN:
                    loggerProxy = new WarnLoggerProxy(logger);
                    break;
                case ERROR:
                    loggerProxy = new ErrorLoggerProxy(logger);
                    break;
                default:
                case DEBUG:
                    loggerProxy = new DebugLoggerProxy(logger);
                    break;
            }
            return new Reporter(registry, loggerProxy, marker, prefix, rateUnit, durationUnit, filter);
        }
    }

    private final LoggerProxy loggerProxy;
    private final Marker      marker;
    private final String      prefix;
    private final String      ip;

    private Reporter(MetricRegistry registry, LoggerProxy loggerProxy, Marker marker, String prefix, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter) {
        super(registry, "logger-reporter", filter, rateUnit, durationUnit);
        this.loggerProxy = loggerProxy;
        this.marker = marker;
        this.prefix = prefix;
        this.ip = getLocalIP();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
            SortedMap<String, Timer> timers) {
        if (loggerProxy.isEnabled(marker)) {
            for (Entry<String, Meter> entry : meters.entrySet()) {
                logMeter(entry.getKey(), entry.getValue());
            }
            for (Entry<String, Timer> entry : timers.entrySet()) {
                logTimer(entry.getKey(), entry.getValue());
            }
        }
    }

    private void logTimer(String name, Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();
        loggerProxy.log(
                marker,
                JSON.toJSONString(ReporterItem.builder().type("TIMER").name(prefix(name)).ip(this.ip).count(timer.getCount()).meanRate(convertRate(timer.getMeanRate()))
                        .m1(convertRate(timer.getOneMinuteRate())).m5(convertRate(timer.getFiveMinuteRate())).m15(convertRate(timer.getFifteenMinuteRate()))
                        .rateUnit(getRateUnit()).min(convertDuration(snapshot.getMin())).max(convertDuration(snapshot.getMax())).mean(convertDuration(snapshot.getMean()))
                        .stddev(convertDuration(snapshot.getStdDev())).median(convertDuration(snapshot.getMedian())).p75(convertDuration(snapshot.get75thPercentile()))
                        .p95(convertDuration(snapshot.get95thPercentile())).p98(convertDuration(snapshot.get98thPercentile())).p99(convertDuration(snapshot.get99thPercentile()))
                        .p999(convertDuration(snapshot.get999thPercentile())).durationUnit(getDurationUnit()).build()));
    }

    private void logMeter(String name, Meter meter) {
        loggerProxy.log(
                marker,
                JSON.toJSONString(ReporterItem.builder().type("METER").name(prefix(name)).ip(this.ip).count(meter.getCount()).meanRate(convertRate(meter.getMeanRate()))
                        .m1(convertRate(meter.getOneMinuteRate())).m5(convertRate(meter.getFiveMinuteRate())).m15(convertRate(meter.getFifteenMinuteRate()))
                        .rateUnit(getRateUnit()).build()));
    }

    @Override
    protected double convertDuration(double duration) {
        return (new BigDecimal(super.convertDuration(duration))).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    protected double convertRate(double rate) {
        return (new BigDecimal(super.convertRate(rate))).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    protected String getRateUnit() {
        return "events/" + super.getRateUnit();
    }

    private String prefix(String... components) {
        return MetricRegistry.name(prefix, components);
    }

    /* private class to allow logger configuration */
    static abstract class LoggerProxy {
        protected final Logger logger;

        public LoggerProxy(Logger logger) {
            this.logger = logger;
        }

        abstract void log(Marker marker, String format, Object... arguments);

        abstract boolean isEnabled(Marker marker);
    }

    /* private class to allow logger configuration */
    private static class DebugLoggerProxy extends LoggerProxy {
        public DebugLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format, Object... arguments) {
            logger.debug(marker, format, arguments);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isDebugEnabled(marker);
        }
    }

    /* private class to allow logger configuration */
    private static class TraceLoggerProxy extends LoggerProxy {
        public TraceLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format, Object... arguments) {
            logger.trace(marker, format, arguments);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isTraceEnabled(marker);
        }
    }

    /* private class to allow logger configuration */
    private static class InfoLoggerProxy extends LoggerProxy {
        public InfoLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format, Object... arguments) {
            logger.info(marker, format, arguments);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isInfoEnabled(marker);
        }
    }

    /* private class to allow logger configuration */
    private static class WarnLoggerProxy extends LoggerProxy {
        public WarnLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format, Object... arguments) {
            logger.warn(marker, format, arguments);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isWarnEnabled(marker);
        }
    }

    /* private class to allow logger configuration */
    private static class ErrorLoggerProxy extends LoggerProxy {
        public ErrorLoggerProxy(Logger logger) {
            super(logger);
        }

        @Override
        public void log(Marker marker, String format, Object... arguments) {
            logger.error(marker, format, arguments);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isErrorEnabled(marker);
        }
    }

    public static String getLocalIP() {
        String localIP = null;
        String netIP = null;
        Enumeration<NetworkInterface> nInterfaces = null;
        try {
            nInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
        }
        boolean finded = false;
        while (nInterfaces.hasMoreElements() && !finded) {
            Enumeration<InetAddress> inetAddress = nInterfaces.nextElement().getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress address = inetAddress.nextElement();
                if (!address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                    netIP = address.getHostAddress();
                    finded = true;
                    break;
                } else if (address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                    localIP = address.getHostAddress();
                }
            }
        }
        return (netIP != null && !"".equals(netIP)) ? netIP : localIP;
    }

}
