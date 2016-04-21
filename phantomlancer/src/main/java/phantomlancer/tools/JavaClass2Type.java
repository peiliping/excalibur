package phantomlancer.tools;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroTypeException;
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
        JAVACLASS2TYPE.put(Byte.TYPE, Type.BYTES);

        JAVACLASS2TYPE.put(Short.class, Type.INT);
        JAVACLASS2TYPE.put(Short.TYPE, Type.INT);

        JAVACLASS2TYPE.put(Integer.class, Type.INT);
        JAVACLASS2TYPE.put(Integer.TYPE, Type.INT);

        JAVACLASS2TYPE.put(Long.class, Type.LONG);
        JAVACLASS2TYPE.put(Long.TYPE, Type.LONG);

        JAVACLASS2TYPE.put(Float.class, Type.FLOAT);
        JAVACLASS2TYPE.put(Float.TYPE, Type.FLOAT);
        JAVACLASS2TYPE.put(Double.class, Type.DOUBLE);
        JAVACLASS2TYPE.put(Double.TYPE, Type.DOUBLE);

        JAVACLASS2TYPE.put(String.class, Type.STRING);
        JAVACLASS2TYPE.put(Character.class, Type.STRING);
        JAVACLASS2TYPE.put(Character.TYPE, Type.STRING);

        JAVACLASS2TYPE.put(java.util.Date.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Date.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Time.class, Type.STRING);
        JAVACLASS2TYPE.put(java.sql.Timestamp.class, Type.STRING);

        JAVACLASS2TYPE.put(Void.class, Type.NULL);
        JAVACLASS2TYPE.put(Void.TYPE, Type.NULL);

    }

    private static Schema toAvroSchema(Class<?> c, boolean allCanBeNull) {
        Type p = JAVACLASS2TYPE.get(c);
        Validate.isTrue(p != null, "Java Class Type Not Found : %s", c);
        if (allCanBeNull) {
            List<Schema> childSchemas = new ArrayList<Schema>();
            childSchemas.add(Schema.create(Schema.Type.NULL));
            childSchemas.add(Schema.create(p));
            return Schema.createUnion(childSchemas);
        } else {
            return Schema.create(p);
        }
    }

    public static Field toAvroField(java.lang.reflect.Field field, AvroScan avroScan) {
        java.lang.reflect.Type type = field.getGenericType();
        Class<?> fieldClass = field.getType();
        String fieldName = field.getName();
        return toAvroField(type, fieldClass, fieldName, avroScan);
    }

    public static Field toAvroField(java.lang.reflect.Type genericType, Class<?> fieldClass, String fieldName, AvroScan avroScan) {
        Field avscField = null;
        if (JAVACLASS2TYPE.containsKey(fieldClass)) {
            Schema avroSchema = toAvroSchema(fieldClass, avroScan.allCanBeNull());
            avscField = new Field(fieldName, avroSchema, null, avroScan.allCanBeNull() ? NullNode.getInstance() : null);
        } else if (fieldClass.isArray() || Collection.class.isAssignableFrom(fieldClass)) {
            Field item = null;
            if (fieldClass.isArray()) {
                item = toAvroField(fieldClass.getComponentType(), fieldClass.getComponentType(), fieldName, avroScan);
            }
            if (Collection.class.isAssignableFrom(fieldClass)) {
                ParameterizedType ptype = (ParameterizedType) genericType;
                java.lang.reflect.Type[] params = ptype.getActualTypeArguments();
                Class<?> nextC = (params[0] instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) params[0]).getRawType() : (Class<?>) params[0]);
                item = toAvroField(params[0], nextC, fieldName, avroScan);
            }
            if (item != null) {
                Schema arraySchema = Schema.createArray(item.schema());
                avscField = new Field(fieldName, arraySchema, null, null);
            }
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            ParameterizedType ptype = (ParameterizedType) genericType;
            java.lang.reflect.Type[] params = ptype.getActualTypeArguments();
            java.lang.reflect.Type key = params[0];
            java.lang.reflect.Type value = params[1];
            if (!(key instanceof Class && CharSequence.class.isAssignableFrom((Class<?>) key))) {
                throw new AvroTypeException("Map key class not CharSequence: " + key);
            }
            Class<?> nextC = (value instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) value).getRawType() : (Class<?>) value);
            Field item = toAvroField(value, nextC, fieldName, avroScan);
            if (item != null) {
                Schema arraySchema = Schema.createMap(item.schema());
                avscField = new Field(fieldName, arraySchema, null, null);
            }
        } else if (fieldClass.getAnnotation(AvroScan.class) != null) {
            AvscSchemaBuilder asb = new AvscSchemaBuilder(fieldClass);
            Schema avroSchema = asb.createSchema();
            avscField = new Field(fieldName, avroSchema, null, null);
        }
        return avscField;
    }
}
