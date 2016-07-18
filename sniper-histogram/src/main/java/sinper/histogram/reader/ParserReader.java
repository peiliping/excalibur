package sinper.histogram.reader;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.HdrHistogram.Histogram;
import sinper.histogram.dataObject.result.HistogramResult;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

/**
 * Created by peiliping on 16-7-18.
 */
public class ParserReader {

    String path;

    public ParserReader(String path) {
        this.path = path;
    }

    public void parse() throws Exception {
        Files.readLines(Paths.get(path).toFile(), Charset.defaultCharset(), new LineProcessor<String>() {
            @Override public boolean processLine(String line) throws IOException {
                HistogramResult result = JSON.parseObject(line, HistogramResult.class);
                String histogramString = result.getHistogram();
                System.out.println(histogramString);
                ByteBuffer buffer = ByteBuffer.wrap(DatatypeConverter.parseBase64Binary(histogramString));
                try {
                    Histogram histogram = Histogram.decodeFromCompressedByteBuffer(buffer, 0);
                    histogram.setStartTimeStamp(result.getStartTime());
                    histogram.setEndTimeStamp(result.getEndTime());
                    //TODO
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override public String getResult() {
                return null;
            }
        });
    }

}
