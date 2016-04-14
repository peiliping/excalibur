package phantomlancer.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.node.NullNode;

import phantomlancer.AvscSchemaBuilder;
import phantomlancer.annotation.AvroScan;

import com.google.common.collect.Maps;

public class JavaClass2Type {

    private static final Map<Class<?>, Type> JAVACLASS2TYPE = Maps.newHashMap();
    static {
        JAVACLASS2TYPE.put(Boolean.class, Type.BOOLEAN);
        JAVACLASS2TYPE.put(boolean.class, Type.BOOLEAN);

        JAVACLASS2TYPE.put(Byte.class, Type.BYTES);
        JAVACLASS2TYPE.put(byte.class, Type.BYTES);

        JAVACLASS2TYPE.put(Short.class, Type.INT);
        JAVACLASS2TYPE.put(short.class, Type.INT);

        JAVACLASS2TYPE.put(Integer.class, Type.INT);
        JAVACLASS2TYPE.put(int.class, Type.INT);

        JAVACLASS2TYPE.put(Long.class, Type.LONG);
        JAVACLASS2TYPE.put(long.class, Type.LONG);

        JAVACLASS2TYPE.put(Float.class, Type.DOUBLE);
        JAVACLASS2TYPE.put(float.class, Type.DOUBLE);
        JAVACLASS2TYPE.put(Double.class, Type.DOUBLE);
        JAVACLASS2TYPE.put(double.class, Type.DOUBLE);

        JAVACLASS2TYPE.put(String.class, Type.STRING);
        JAVACLASS2TYPE.put(Character.class, Type.STRING);
        JAVACLASS2TYPE.put(char.class, Type.STRING);

        JAVACLASS2TYPE.put(java.util.Date.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Date.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Time.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Timestamp.class, Type.STRING);
    }

    public static Schema toAvroSchema(Class<?> c) {
        List<Schema> childSchemas = new ArrayList<Schema>();
        childSchemas.add(Schema.create(Schema.Type.NULL));
        Type p = JAVACLASS2TYPE.get(c);
        Validate.isTrue(p != null, "Java Class Type Not Found : %s", c);
        childSchemas.add(Schema.create(p));
        return Schema.createUnion(childSchemas);
    }

    public static Field toAvroField(java.lang.reflect.Field fd, String fldName, AvroScan avroScan) {
        Field field = null;
        if (JAVACLASS2TYPE.containsKey(fd.getType())) {
            Schema avroSchema = toAvroSchema(fd.getType());
            field = new Field(fldName, avroSchema, null, NullNode.getInstance());
        } else if (fd.getType().getAnnotation(AvroScan.class) != null) {
            AvscSchemaBuilder asb = new AvscSchemaBuilder(fd.getType());
            Schema avroSchema = asb.getResult();
            field = new Field(fldName, avroSchema, null, null);
        }
        return field;
    }
}
