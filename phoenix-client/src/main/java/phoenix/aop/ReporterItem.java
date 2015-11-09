package phoenix.aop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReporterItem {

    private long   timestamp;
    private String type;
    private String name;
    private long   count;
    private String ip;

    private double meanRate;
    private String rateUnit;
    private double m1;
    private double m5;
    private double m15;

    private Double min;
    private Double max;
    private Double mean;
    private Double stddev;
    private Double median;
    private Double p75;
    private Double p95;
    private Double p98;
    private Double p99;
    private Double p999;
    private String durationUnit;
}
