package phoenix.avro;

import phoenix.avro.annotation.LogOrder;
import lombok.Getter;
import lombok.Setter;

public class Basic {

    @Setter
    @Getter
    @LogOrder(order = 1, skip = false)
    private int idSvvvv;


}
