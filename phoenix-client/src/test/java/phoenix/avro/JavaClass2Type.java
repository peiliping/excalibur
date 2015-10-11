package phoenix.avro;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.node.NullNode;

public class JavaClass2Type {

    private static final Map<Class<?>, Pair<Type, Integer>> JAVACLASS2TYPE = new HashMap<Class<?>, Pair<Type, Integer>>();
    static {
        JAVACLASS2TYPE.put(Boolean.class, Pair.of(Type.BOOLEAN, Types.BOOLEAN));
        JAVACLASS2TYPE.put(boolean.class, Pair.of(Type.BOOLEAN, Types.BOOLEAN));

        JAVACLASS2TYPE.put(Byte.class, Pair.of(Type.BYTES, Types.BINARY));
        JAVACLASS2TYPE.put(byte.class, Pair.of(Type.BYTES, Types.BINARY));

        JAVACLASS2TYPE.put(Short.class, Pair.of(Type.INT, Types.INTEGER));
        JAVACLASS2TYPE.put(short.class, Pair.of(Type.INT, Types.INTEGER));
        JAVACLASS2TYPE.put(Integer.class, Pair.of(Type.INT, Types.INTEGER));
        JAVACLASS2TYPE.put(int.class, Pair.of(Type.INT, Types.INTEGER));

        JAVACLASS2TYPE.put(Long.class, Pair.of(Type.LONG, Types.BIGINT));
        JAVACLASS2TYPE.put(long.class, Pair.of(Type.LONG, Types.BIGINT));

        JAVACLASS2TYPE.put(Float.class, Pair.of(Type.DOUBLE, Types.DOUBLE));
        JAVACLASS2TYPE.put(float.class, Pair.of(Type.DOUBLE, Types.DOUBLE));
        JAVACLASS2TYPE.put(Double.class, Pair.of(Type.DOUBLE, Types.DOUBLE));
        JAVACLASS2TYPE.put(double.class, Pair.of(Type.DOUBLE, Types.DOUBLE));

        JAVACLASS2TYPE.put(String.class, Pair.of(Type.STRING, Types.VARCHAR));
        JAVACLASS2TYPE.put(Character.class, Pair.of(Type.STRING, Types.VARCHAR));
        JAVACLASS2TYPE.put(char.class, Pair.of(Type.STRING, Types.VARCHAR));

        JAVACLASS2TYPE.put(java.util.Date.class, Pair.of(Type.STRING, Types.DATE));
        JAVACLASS2TYPE.put(java.sql.Date.class, Pair.of(Type.STRING, Types.DATE));
        JAVACLASS2TYPE.put(java.sql.Time.class, Pair.of(Type.STRING, Types.TIME));
        JAVACLASS2TYPE.put(java.sql.Timestamp.class, Pair.of(Type.STRING, Types.TIMESTAMP));
    }

    public static Type getAvroType(Class<?> clazz) {
        Pair<Type, Integer> p = JAVACLASS2TYPE.get(clazz);
        if (p != null) {
            return p.getKey();
        }
        throw new IllegalArgumentException("Java Class Type Not Found :" + clazz);
    }

    public static Schema toAvroSchema(Class<?> c) {
        List<Schema> childSchemas = new ArrayList<Schema>();
        childSchemas.add(Schema.create(Schema.Type.NULL));
        childSchemas.add(Schema.create(getAvroType(c)));
        return Schema.createUnion(childSchemas);
    }

    public static Field toAvroField(String colName, Class<?> type) {
        Schema avroSchema = toAvroSchema(type);
        Field field = new org.apache.avro.Schema.Field(colName, avroSchema, null, NullNode.getInstance());
        field.addProp("columnName", colName);
        field.addProp("sqlType", Integer.toString(getSqlType(type)));
        return field;
    }

    public static Integer getSqlType(Class<?> clazz) {
        Pair<Type, Integer> p = JAVACLASS2TYPE.get(clazz);
        if (p != null) {
            return p.getValue();
        }
        throw new IllegalArgumentException("Java Class Type Not Found :" + clazz);
    }
}
