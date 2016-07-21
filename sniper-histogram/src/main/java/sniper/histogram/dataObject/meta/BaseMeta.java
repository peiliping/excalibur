package sniper.histogram.dataObject.meta;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by peiliping on 16-7-15.
 */
@Getter @Setter @ToString public final class BaseMeta extends Meta {

    @Builder public BaseMeta(String nameSpace, String metric) {
        super(nameSpace, metric);
    }

}
