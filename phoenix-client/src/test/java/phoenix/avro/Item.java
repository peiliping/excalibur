package phoenix.avro;

import java.lang.reflect.Field;

import phoenix.avro.annotation.LogOrder;
import lombok.Getter;
import lombok.Setter;

public class Item {

    @Setter
    @Getter
    private String   fieldName;
    @Setter
    @Getter
    private int      order;
    @Setter
    @Getter
    private Class<?> type;

    public Item(Field f) {
        this.fieldName = SchemaBuilder.underscoreName(f.getName());
        this.type = f.getType();
        this.order = f.getAnnotation(LogOrder.class).order();
    }
}
