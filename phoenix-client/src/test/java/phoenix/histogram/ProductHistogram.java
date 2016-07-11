package phoenix.histogram;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;
import org.HdrHistogram.SingleWriterRecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

/**
 * Created by peiliping on 16-7-6.
 */
public class ProductHistogram {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        Random r = new Random();
        String path = "/home/peiliping/dev/logs/hdr.log" + System.currentTimeMillis();
        PrintStream log = new PrintStream(new FileOutputStream(path), false);
        HistogramLogWriter histogramLogWriter = new HistogramLogWriter(log);
        SingleWriterRecorder recorder = new SingleWriterRecorder(1000, 3600 * 1000L * 1000L, 3);

        histogramLogWriter.outputLogFormatVersion();
        long base = System.currentTimeMillis();
        histogramLogWriter.outputBaseTime(base);
        histogramLogWriter.setBaseTime(base);
        histogramLogWriter.outputLegend();

        int i = 0;
        Histogram intervalHistogram = null;
        while (i < 200000000) {
            if (i++ % 10000000 == 0) {
                System.out.println(i);
                if (intervalHistogram == null) {
                    intervalHistogram = recorder.getIntervalHistogram();
                    intervalHistogram.setAutoResize(true);
                    intervalHistogram.setStartTimeStamp(System.currentTimeMillis());
                } else {
                    intervalHistogram.setEndTimeStamp(System.currentTimeMillis());
                    histogramLogWriter.outputIntervalHistogram(intervalHistogram);
                    intervalHistogram = recorder.getIntervalHistogram();
                    intervalHistogram.setAutoResize(true);
                    intervalHistogram.setStartTimeStamp(System.currentTimeMillis());
                }
            }
            long k = r.nextLong() ;
            k = ( k < 0 ? 0-k : k );
            intervalHistogram.recordValue(k);
        }

        System.out.println("XXX");
    }

}
