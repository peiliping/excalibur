package phoenix.histogram.x;

import org.HdrHistogram.HistogramLogProcessor;

import java.io.FileNotFoundException;

/**
 * Created by peiliping on 16-7-7.
 */
public class ParseHistogram {



    public static void main(String[] args) throws FileNotFoundException {

        String[] ss = {"-i", "/home/peiliping/dev/logs/hdr.log1468217486476"};
        final HistogramLogProcessor processor = new HistogramLogProcessor(ss);
        processor.start();

    }

}
