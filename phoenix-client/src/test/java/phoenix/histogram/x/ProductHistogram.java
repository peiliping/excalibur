package phoenix.histogram.x;

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


    private final static Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        final long basicTime = System.currentTimeMillis();
        final String path = "/home/peiliping/dev/logs/hdr.log" + basicTime;
        final int total = 100000000;

        HistogramLogWriter histogramLogWriter = new HistogramLogWriter(new PrintStream(new FileOutputStream(path), false));
        histogramLogWriter.setBaseTime(basicTime);

        histogramLogWriter.outputLogFormatVersion();
        histogramLogWriter.outputBaseTime(basicTime);
        histogramLogWriter.outputLegend();

        SingleWriterRecorder recorder = new SingleWriterRecorder(1000 * 100, 3600 * 1000 * 1000L * 1000L, 3);
        Histogram intervalHistogram = null;

        int i = 0;
        while (i < total) {
            if (i % (total / 10) == 0) {
                Histogram tmp = intervalHistogram;
                intervalHistogram = buildHistogram(recorder);

                if (tmp != null) {
                    tmp.setEndTimeStamp(System.currentTimeMillis());
                    histogramLogWriter.outputIntervalHistogram(tmp);
                }

            }
            long k = Math.abs(RANDOM.nextLong());
            intervalHistogram.recordValue(k);
            i++;
        }
        System.out.println("XXX");
    }

    private static Histogram buildHistogram(SingleWriterRecorder recorder) {
        Histogram intervalHistogram = recorder.getIntervalHistogram();
        intervalHistogram.setAutoResize(true);
        intervalHistogram.setStartTimeStamp(System.currentTimeMillis());
        return intervalHistogram;
    }

}
