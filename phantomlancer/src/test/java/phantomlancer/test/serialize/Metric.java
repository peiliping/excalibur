package phantomlancer.test.serialize;

import phantomlancer.annotation.AvroScan;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AvroScan(camelConvert = true, nameSpace = "phantomlancer.test.serialize")
public class Metric {

    private int    dataVersion;
    private int    salt;
    private long   applicationId;
    private int    timeScope;
    private long   metricTypeId;
    private long   metricId;
    private int    time;
    private long   agentRunId;
    private String uuid;
    private double num1;
    private double num2;
    private double num3;
    private double num4;
    private double num5;
    private double num6;
    private long   timestamp;

}
