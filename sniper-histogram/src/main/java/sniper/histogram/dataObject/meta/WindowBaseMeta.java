package sniper.histogram.dataObject.meta;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by peiliping on 16-7-15.
 */
@Getter @Setter @ToString public class WindowBaseMeta extends Meta {

    protected long windowTime;

    @Builder public WindowBaseMeta(String nameSpace, String metric, long windowTime) {
        super(nameSpace, metric);
        this.windowTime = windowTime;
    }

    public String buildKey() {
        return windowTime + "|" + nameSpace + "|" + metric;
    }

}
