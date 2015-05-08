package phoenix.dataObject;

import lombok.Getter;
import lombok.Setter;

public class MultiResult {

    @Setter
    @Getter
    private long   id;
    @Setter
    @Getter
    private double count;
    @Setter
    @Getter
    private double total;
    @Setter
    @Getter
    private double min;
    @Setter
    @Getter
    private double max;

}
