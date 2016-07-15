package phoenix.histogram;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by peiliping on 16-7-14.
 */
@Getter @Setter @Builder public class HistogramResult {

    private String nameSpace;

    private String metric;

    private Long timeWindow;

    private long startTime;

    private long endTime;

    private long totalCount;

    private double mean;

    private long min;

    private long max;

    private long p1;

    private long p5;

    private long p25;

    private long p50;

    private long p75;

    private long p95;

    private long p99;

    private String histogram;

}
