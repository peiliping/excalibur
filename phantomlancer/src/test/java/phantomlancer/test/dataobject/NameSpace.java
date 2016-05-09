package phantomlancer.test.dataobject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import phantomlancer.annotation.AvroScan;

@AvroScan
@Getter
@Setter
@Builder
public class NameSpace {

    private long             parentId;
    private transient String parentName;
    private long             metricId;
    private transient String metricName;

}
