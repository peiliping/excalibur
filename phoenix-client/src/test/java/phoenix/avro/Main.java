package phoenix.avro;

import java.lang.reflect.Field;

public class Main {

    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        // SchemaBuilder op = new SchemaBuilder(TestAvro.class);
        // System.out.println(op.convert2Schema().toString(true));
        TestAvro ta = new TestAvro();
        Basic b = new Basic();
        b.setIdSvvvv(23);
        b.setVn("sdfsdf");

        Class c = ta.getClass().getSuperclass();
        Field f = c.getDeclaredField("vn");
        f.setAccessible(true);
        System.out.println(f.get(ta));
    }
}
