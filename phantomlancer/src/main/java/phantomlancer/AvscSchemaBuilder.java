package phantomlancer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import phantomlancer.annotation.AvroScan;
import phantomlancer.tools.JavaClass2Type;
import phantomlancer.tools.StrUtils;

import com.google.common.collect.Sets;

@Setter
@Getter
public class AvscSchemaBuilder {

    private Class<?>        clazz;
    private AvroScan        avroScan;
    private volatile Schema schema;
    private boolean         nameSpaceByPKN;

    public AvscSchemaBuilder(Class<?> clazz) {
        this.clazz = clazz;
        this.avroScan = clazz.getAnnotation(AvroScan.class);
        Validate.notNull(avroScan, "[%s] not found annotation(AvroScan.class)", clazz);
    }

    public synchronized Schema createSchema() {

        String tableName = StrUtils.camelConvert(avroScan, clazz.getSimpleName());
        String nameSpace = (avroScan.nameSpaceByPackageName() ? clazz.getPackage().getName() : avroScan.nameSpaceByManul());

        Schema sc = Schema.createRecord(tableName, "Auto Schema " + tableName, nameSpace, false);
        List<org.apache.avro.Schema.Field> avroFields = new ArrayList<org.apache.avro.Schema.Field>();
        HashSet<String> fieldNames = Sets.newHashSet();

        for (Class<?> tclazz = clazz; tclazz != Object.class; tclazz = tclazz.getSuperclass()) {
            Field[] fs = tclazz.getDeclaredFields();
            for (Field field : fs) {
                if ((avroScan.skipStaticField() && Modifier.isStatic(field.getModifiers()) || (avroScan.skipTransientField() && Modifier.isTransient(field.getModifiers())))) {
                    continue;
                }
                String fieldName = StrUtils.camelConvert(avroScan, field.getName());
                org.apache.avro.Schema.Field avroField = null;
                if (!fieldNames.contains(fieldName)) {
                    fieldNames.add(fieldName);
                    avroField = JavaClass2Type.toAvroField(field, avroScan);
                }
                if (avroField != null) {
                    avroFields.add(avroField);
                }
            }
        }
        sc.setFields(avroFields);
        schema = sc;
        return getSchema();
    }
}
