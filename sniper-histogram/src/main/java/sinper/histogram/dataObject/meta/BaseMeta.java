package sinper.histogram.dataObject.meta;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by peiliping on 16-7-15.
 */
@Getter @Setter @ToString public final class BaseMeta extends Meta {

    @Builder public BaseMeta(String nameSpace, String metric) {
        super(nameSpace, metric);
    }

}
