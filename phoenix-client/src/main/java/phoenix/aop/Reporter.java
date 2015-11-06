package phoenix.aop;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

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
        private Logger               loggerMeter;
        private Logger               loggerTimer;
        private String               prefix;
        private TimeUnit             rateUnit;
        private TimeUnit             durationUnit;
        private MetricFilter         filter;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.prefix = "";
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        /**
         * Log metrics to the given logger.
         *
         * @param logger an SLF4J {@link Logger}
         * @return {@code this}
         */
        public Builder outputTo(Logger loggerMeter, Logger loggerTimer) {
            this.loggerMeter = loggerMeter;
            this.loggerTimer = loggerTimer;
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
         * Builds a {@link Slf4jReporter} with the given properties.
         *
         * @return a {@link Slf4jReporter}
         */
        public Reporter build() {
            return new Reporter(registry, prefix, rateUnit, durationUnit, filter, loggerMeter, loggerTimer);
        }
    }

    private final String prefix;
    private final String ip;
    private final Logger meterLogger;
    private final Logger timerLogger;

    private Reporter(MetricRegistry registry, String prefix, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter, Logger meter, Logger timer) {
        super(registry, "logger-reporter", filter, rateUnit, durationUnit);
        this.prefix = prefix;
        this.meterLogger = meter;
        this.timerLogger = timer;
        this.ip = HttpUtil.getLocalIP();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
            SortedMap<String, Timer> timers) {
        for (Entry<String, Meter> entry : meters.entrySet()) {
            logMeter(entry.getKey(), entry.getValue());
        }
        for (Entry<String, Timer> entry : timers.entrySet()) {
            logTimer(entry.getKey(), entry.getValue());
        }
    }

    private void logTimer(String name, Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();
        timerLogger.info(JSON.toJSONString(ReporterItem.builder().type("TIMER").name(prefix(name)).ip(this.ip).count(timer.getCount()).meanRate(convertRate(timer.getMeanRate()))
                .m1(convertRate(timer.getOneMinuteRate())).m5(convertRate(timer.getFiveMinuteRate())).m15(convertRate(timer.getFifteenMinuteRate())).rateUnit(getRateUnit())
                .min(convertDuration(snapshot.getMin())).max(convertDuration(snapshot.getMax())).mean(convertDuration(snapshot.getMean()))
                .stddev(convertDuration(snapshot.getStdDev())).median(convertDuration(snapshot.getMedian())).p75(convertDuration(snapshot.get75thPercentile()))
                .p95(convertDuration(snapshot.get95thPercentile())).p98(convertDuration(snapshot.get98thPercentile())).p99(convertDuration(snapshot.get99thPercentile()))
                .p999(convertDuration(snapshot.get999thPercentile())).durationUnit(getDurationUnit()).build()));
    }

    private void logMeter(String name, Meter meter) {
        meterLogger.info(JSON.toJSONString(ReporterItem.builder().type("METER").name(prefix(name)).ip(this.ip).count(meter.getCount()).meanRate(convertRate(meter.getMeanRate()))
                .m1(convertRate(meter.getOneMinuteRate())).m5(convertRate(meter.getFiveMinuteRate())).m15(convertRate(meter.getFifteenMinuteRate())).rateUnit(getRateUnit())
                .build()));
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

}
