package meepo.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.apache.parquet.schema.OriginalType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Created by peiliping on 17-2-14.
 */
public class TypesMapping {

    public static final Map<Integer, PrimitiveType.PrimitiveTypeName> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(Types.TINYINT, PrimitiveType.PrimitiveTypeName.INT32);
        MAPPING.put(Types.SMALLINT, PrimitiveType.PrimitiveTypeName.INT32);
        MAPPING.put(Types.INTEGER, PrimitiveType.PrimitiveTypeName.INT32);
        MAPPING.put(Types.BIGINT, PrimitiveType.PrimitiveTypeName.INT64);

        MAPPING.put(Types.BOOLEAN, PrimitiveType.PrimitiveTypeName.BOOLEAN);

        MAPPING.put(Types.REAL, PrimitiveType.PrimitiveTypeName.FLOAT);
        MAPPING.put(Types.FLOAT, PrimitiveType.PrimitiveTypeName.FLOAT);
        MAPPING.put(Types.DOUBLE, PrimitiveType.PrimitiveTypeName.DOUBLE);

        MAPPING.put(Types.TIMESTAMP, PrimitiveType.PrimitiveTypeName.INT64);

        MAPPING.put(Types.CHAR, PrimitiveType.PrimitiveTypeName.BINARY);
        MAPPING.put(Types.VARCHAR, PrimitiveType.PrimitiveTypeName.BINARY);
        MAPPING.put(Types.LONGVARCHAR, PrimitiveType.PrimitiveTypeName.BINARY);
    }

    public static List<Type> getTypes(List<String> colsArray, Map<String, Integer> colsType) {
        List<Type> types = Lists.newArrayList();
        for (String name : colsArray) {
            Integer type = colsType.get(name);
            Validate.notNull(MAPPING.get(type));
            if (MAPPING.get(type) == PrimitiveType.PrimitiveTypeName.BINARY) {
                types.add(new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BINARY, name, OriginalType.UTF8));
            } else {
                types.add(new PrimitiveType(Type.Repetition.OPTIONAL, MAPPING.get(type), name));
            }
        }
        return types;
    }
}
