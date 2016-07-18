package sinper.histogram.dataObject.meta;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by peiliping on 16-7-15.
 */
@Getter @Setter @ToString public class Meta {

    protected String nameSpace;
    protected String metric;
    protected AtomicLong lastModifyTime = new AtomicLong(0);

    public Meta(String nameSpace, String metric) {
        this.nameSpace = nameSpace;
        this.metric = metric;
    }

    public String buildKey() {
        return nameSpace + "|" + metric;
    }

}
