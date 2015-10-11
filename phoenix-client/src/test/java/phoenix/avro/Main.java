package phoenix.avro;

public class Main {

    public static void main(String[] args) {

         SchemaBuilder op = new SchemaBuilder(TestAvro.class);
         System.out.println(op.convert2Schema().toString(true));
//        TestAvro ta = new TestAvro();
        

    }
}
