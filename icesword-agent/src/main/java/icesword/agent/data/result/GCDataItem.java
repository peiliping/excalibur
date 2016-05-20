package icesword.agent.data.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GCDataItem extends IData {

    private static final long serialVersionUID = 9037008483960292039L;

    private Double            times;

    private Double            cost_ms;

    private Double            cost_ms_p75;

    private Double            cost_ms_p95;

    private Double            cost_ms_min;

    private Double            cost_ms_max;

    private long              timestamp;

    private String            process_signal;

}
