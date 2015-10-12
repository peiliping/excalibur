package phoenix.avro;

import phoenix.avro.annotation.LogOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames = false)
public class Basic {
    @Setter
    @Getter
    @LogOrder(order = 1000, skip = false)
    private String vn;
    @Setter
    @Getter
    @LogOrder(order = 100, skip = false)
    private int    idSvvvv;
}
