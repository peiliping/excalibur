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
    private String          packageName;
    private String          className;
    private AvroScan        avroScan;
    private volatile Schema schema;

    public AvscSchemaBuilder(Class<?> clazz) {
        this.clazz = clazz;
        this.packageName = clazz.getPackage().getName();
        this.avroScan = clazz.getAnnotation(AvroScan.class);
        this.className = StrUtils.camelConvert(avroScan, clazz.getSimpleName());
        Validate.notNull(avroScan, "[%s] not found annotation(AvroScan.class)", clazz);
    }

    public Schema getResult() {
        if (schema == null) {
            schema = convert2Schema();
        }
        return schema;
    }

    private synchronized Schema convert2Schema() {
        Schema sc = Schema.createRecord(className, "Auto Schema " + className, avroScan.nameSpace(), false);
        List<org.apache.avro.Schema.Field> avrofields = new ArrayList<org.apache.avro.Schema.Field>();
        HashSet<String> fieldNames = Sets.newHashSet();
        for (Class<?> tclazz = clazz; tclazz != Object.class; tclazz = tclazz.getSuperclass()) {
            Field[] fs = tclazz.getDeclaredFields();
            for (Field fld : fs) {
                if ((avroScan.skipStaticField() && Modifier.isStatic(fld.getModifiers()) || (avroScan.skipTransientField() && Modifier.isTransient(fld.getModifiers())))) {
                    continue;
                }
                String fdName = StrUtils.camelConvert(avroScan, fld.getName());
                org.apache.avro.Schema.Field avroField = null;
                if (!fieldNames.contains(fdName)) {
                    fieldNames.add(fdName);
                    avroField = JavaClass2Type.toAvroField(fld, fdName, avroScan);
                }
                if (avroField != null) {
                    avrofields.add(avroField);
                }
            }
        }
        sc.setFields(avrofields);
        return sc;
    }
}
