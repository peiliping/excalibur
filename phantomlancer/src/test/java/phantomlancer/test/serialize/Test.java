package phantomlancer.test.serialize;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import phantomlancer.AvscSchemaBuilder;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class Test {

    public static void main(String[] args) throws IOException {

        AvscSchemaBuilder asb = new AvscSchemaBuilder(Metric.class);

        final DataFileWriter<Metric2> dataFileWriter = new DataFileWriter<Metric2>(new SpecificDatumWriter<Metric2>(asb.getResult()));
        dataFileWriter.create(asb.getResult(), new File("/home/peiliping/dev/logs/v1.avro"));

        Path p = Paths.get("/home/peiliping/dev/logs/v1.log");
        Files.readLines(p.toFile(), Charset.defaultCharset(), new LineProcessor<String>() {
            int i = 1;

            @Override
            public boolean processLine(String line) throws IOException {
                String[] vals = line.split(" ");
                if (i++ % 1000000 == 0) {
                    System.out.println(i);
                }
                Metric2 mc =
                        Metric2.builder().dataVersion(Integer.valueOf(vals[0])).salt(Integer.valueOf(vals[1])).applicationId(Long.valueOf(vals[2]))
                                .timeScope(Integer.valueOf(vals[3])).metricTypeId(Long.valueOf(vals[4])).metricId(Long.valueOf(vals[5])).time(Integer.valueOf(vals[6]))
                                .agentRunId(Long.valueOf(vals[7])).uuid(vals[8]).num1(Double.valueOf(vals[9])).num2(Double.valueOf(vals[10])).num3(Double.valueOf(vals[11]))
                                .num4(Double.valueOf(vals[12])).num5(Double.valueOf(vals[13])).num6(Double.valueOf(vals[14])).timestamp(Long.valueOf(vals[15])).build();
                dataFileWriter.append(mc);
                return true;
            }

            @Override
            public String getResult() {
                return null;
            }
        });
        dataFileWriter.flush();
        dataFileWriter.close();
    }
}
