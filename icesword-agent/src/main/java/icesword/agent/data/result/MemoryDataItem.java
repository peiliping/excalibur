package icesword.agent.data.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MemoryDataItem extends IData {

    private static final long serialVersionUID = 9037008483960292039L;

    private Double            times;

    private Double            capacity;

    private Double            used;

    private long              timestamp;

    private String            process_signal;

}
