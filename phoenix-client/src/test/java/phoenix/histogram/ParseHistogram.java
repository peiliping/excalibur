package phoenix.histogram;

import org.HdrHistogram.HistogramLogProcessor;

import java.io.FileNotFoundException;

/**
 * Created by peiliping on 16-7-7.
 */
public class ParseHistogram {



    public static void main(String[] args) throws FileNotFoundException {

        String[] ss = {"-i" , "/home/peiliping/dev/logs/hdr.log1467882064544"};

        final HistogramLogProcessor processor = new HistogramLogProcessor(ss);
        processor.start();

    }

}
