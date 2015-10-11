package phoenix.avro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.avro.Schema;

import com.google.common.collect.Lists;

import phoenix.avro.annotation.AvroScan;
import phoenix.avro.annotation.LogOrder;

public class SchemaBuilder {

    @Setter
    @Getter
    private String      className;
    @Setter
    @Getter
    private String      packageName;
    @Setter
    @Getter
    private List<Field> fields = new ArrayList<Field>();
    @Setter
    @Getter
    private Class<?>    clazz;

    public SchemaBuilder(Class<?> clazz) {
        Annotation an = clazz.getAnnotation(AvroScan.class);
        if (an == null) {
            throw new IllegalArgumentException("Annotation AvroScan Not Found :" + clazz);
        }
        this.clazz = clazz;
        this.className = underscoreName(clazz.getSimpleName());
        this.packageName = clazz.getPackage().getName();
        for (Class<?> tclazz = clazz; tclazz != Object.class; tclazz = tclazz.getSuperclass()) {
            fields.addAll(Lists.newArrayList(tclazz.getDeclaredFields()));
        }
    }

    public Schema convert2Schema() {
        List<Item> targetFieldsList = new ArrayList<Item>();
        for (Field f : fields) {
            LogOrder lo = f.getAnnotation(LogOrder.class);
            if (lo == null || lo.skip()) {
                continue;
            } else {
                targetFieldsList.add(new Item(f));
            }
        }
        Collections.sort(targetFieldsList, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getOrder() > o2.getOrder() ? 1 : -1;
            }
        });
        List<org.apache.avro.Schema.Field> avrofields = new ArrayList<org.apache.avro.Schema.Field>();
        for (Item i : targetFieldsList) {
            avrofields.add(JavaClass2Type.toAvroField(i.getFieldName(), i.getType()));
        }
        Schema schema = Schema.createRecord(className, "Auto Schema " + className, null, false);
        schema.setFields(avrofields);
        schema.addProp("tableName", className);
        return schema;
    }

    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    private class Item {
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
}
