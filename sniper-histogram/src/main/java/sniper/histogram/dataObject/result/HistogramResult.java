package sniper.histogram.dataObject.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;

/**
 * Created by peiliping on 16-7-15.
 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor public class HistogramResult {

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

    private long p99;

    private long[] footprint;

    private String histogram;

}
