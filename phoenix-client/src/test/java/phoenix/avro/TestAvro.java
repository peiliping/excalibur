package phoenix.avro;

import java.sql.Timestamp;
import java.util.Date;

import phoenix.avro.annotation.AvroScan;
import phoenix.avro.annotation.LogOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AvroScan
@ToString(includeFieldNames = true)
public class TestAvro extends Basic {

    @Setter
    @Getter
    @LogOrder(order = 0, skip = false)
    private boolean       s1;
    @Setter
    @Getter
    @LogOrder(order = 1, skip = false)
    private Boolean       s11;
    @Setter
    @Getter
    @LogOrder(order = 2, skip = false)
    private short         v;
    @Setter
    @Getter
    @LogOrder(order = 3, skip = false)
    private Short         v1;
    @Setter
    @Getter
    @LogOrder(order = 4, skip = false)
    private long          id;
    @Setter
    @Getter
    @LogOrder(order = 5, skip = false)
    private Long          id2;
    @Setter
    @Getter
    @LogOrder(order = 6, skip = false)
    private double        val1;
    @Setter
    @Getter
    @LogOrder(order = 7, skip = false)
    private Double        val11;
    @Setter
    @Getter
    @LogOrder(order = 8, skip = false)
    private float         val2;
    @Setter
    @Getter
    @LogOrder(order = 9, skip = false)
    private Float         val21;
    @Setter
    @Getter
    @LogOrder(order = 10, skip = false)
    private int           val3;
    @Setter
    @Getter
    @LogOrder(order = 11, skip = false)
    private Integer       val31;
    @Setter
    @Getter
    @LogOrder(order = 12, skip = false)
    private String        name;
    @Setter
    @Getter
    @LogOrder(order = 13, skip = false)
    private char          m1;
    @Setter
    @Getter
    @LogOrder(order = 14, skip = false)
    private Character     m11;
    @Setter
    @Getter
    @LogOrder(order = 15, skip = false)
    private byte          b1;
    @Setter
    @Getter
    @LogOrder(order = 16, skip = false)
    private Byte          b11;
    @Setter
    @Getter
    @LogOrder(order = 17, skip = false)
    private Date          d1;
    @Setter
    @Getter
    @LogOrder(order = 18, skip = false)
    private java.sql.Date d2Gda;
    @Setter
    @Getter
    @LogOrder(order = 19, skip = false)
    private Timestamp     t1;
}
