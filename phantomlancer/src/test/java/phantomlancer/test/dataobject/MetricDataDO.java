package phantomlancer.test.dataobject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import phantomlancer.annotation.AvroScan;

@AvroScan
@Getter
@Setter
@Builder
public class MetricDataDO extends AbstractMetricDataDO {

    private static final long serialVersionUID = 5806385316864751488L;

    private long              parentId;
    private transient String  parentName;
    private long              metricId;
    private transient String  metricName;
    private Dimensions        dimensions;
    private Metrics           metrics;
    private Tags              tags;

}
