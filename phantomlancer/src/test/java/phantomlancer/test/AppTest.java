package phantomlancer.test;

import phantomlancer.AvscSchemaBuilder;
import phantomlancer.test.dataobject.Dimensions;
import phantomlancer.test.dataobject.MetricDataDO;
import phantomlancer.test.dataobject.Metrics;
import phantomlancer.test.dataobject.Tags;
import phantomlancer.test.serialize.Metric;

import com.google.gson.Gson;

public class AppTest {

    public static void main(String[] args) {

        Long now = System.currentTimeMillis();

        Dimensions dms =
                Dimensions.builder().dim1(1L).dim2(1L).dim3(1L).dim4(1L).dim5(1L).dim6(1L).dim7(1L).dim8(1L).dim9(1L).dim10(1L).dim11(1L).dim12(1L).dim13(1L).dim14(1L).dim15(1L)
                        .dim16(1L).build();

        Metrics metrics =
                Metrics.builder().mtc1(1d).mtc2(1d).mtc3(1d).mtc4(1d).mtc5(1d).mtc6(1d).mtc7(1d).mtc8(1d).mtc9(1d).mtc10(1d).mtc11(1d).mtc12(1d).mtc13(1d).mtc14(1d).mtc15(1d)
                        .mtc16(1d).build();

        Tags tgs =
                Tags.builder().tag1("127.0.0.1").tag2("127.0.0.1").tag3("127.0.0.1").tag4("127.0.0.1").tag5("127.0.0.1").tag6("127.0.0.1").tag7("127.0.0.1").tag8("127.0.0.1")
                        .tag9("127.0.0.1").tag10("127.0.0.1").tag11("127.0.0.1").tag12("127.0.0.1").tag13("127.0.0.1").tag14("127.0.0.1").tag15("127.0.0.1").tag16("127.0.0.1")
                        .build();

        MetricDataDO mdd = MetricDataDO.builder().parentId(1).parentName("ROOT").metricId(2).metricName("Node").dimensions(dms).metrics(metrics).tags(tgs).build();

        mdd.setTimestampms(now);

        System.out.println((new Gson()).toJson(mdd));

        AvscSchemaBuilder sb1 = new AvscSchemaBuilder(MetricDataDO.class);

        System.out.println(sb1.getResult());

        AvscSchemaBuilder sb2 = new AvscSchemaBuilder(Metric.class);

        System.out.println(sb2.getResult());

    }

}
