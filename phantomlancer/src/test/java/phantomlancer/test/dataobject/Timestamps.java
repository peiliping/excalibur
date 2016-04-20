package phantomlancer.test.dataobject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import phantomlancer.annotation.AvroScan;

@AvroScan
@Getter
@Setter
@Builder
public class Timestamps {

    private long timestampms;

    private int  timestampmt;

    private int  timestamphr;

}
