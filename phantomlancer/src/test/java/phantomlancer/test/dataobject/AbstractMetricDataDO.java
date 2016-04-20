package phantomlancer.test.dataobject;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractMetricDataDO implements Serializable {

    private static final long serialVersionUID = 2919772490904364757L;
    @Getter
    @Setter
    protected Timestamps      timestamps;

}
