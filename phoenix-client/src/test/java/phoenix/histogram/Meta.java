package phoenix.histogram;

import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by peiliping on 16-7-14.
 */
@Getter @Setter @ToString public class Meta {

    protected String nameSpace;
    protected String metric;
    protected AtomicLong lastModifyTime = new AtomicLong(0);

    @Builder public Meta(String nameSpace, String metric) {
        this.nameSpace = nameSpace;
        this.metric = metric;
    }

    public String buildKey() {
        return nameSpace + "|" + metric;
    }

}
