package phoenix.histogram;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by peiliping on 16-7-14.
 */

@Getter @Setter @ToString public class WindowMeta extends Meta {

    protected long windowTime;

    @Builder public WindowMeta(String nameSpace, String metric, long windowTime) {
        super(nameSpace, metric);
        this.windowTime = windowTime;
    }

    public String buildKey() {
        return windowTime + "|" + nameSpace + "|" + metric;
    }

}
