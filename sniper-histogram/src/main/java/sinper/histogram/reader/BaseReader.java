package sinper.histogram.reader;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import sinper.histogram.service.BaseHistogramService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

/**
 * Created by peiliping on 16-7-15.
 */
public class BaseReader {

    protected String sourceDataFilePath;

    protected String resultDataFilePath;

    protected final BaseHistogramService service;

    public BaseReader(String sourceDataFilePath, String resultDataFilePath) {
        this.sourceDataFilePath = sourceDataFilePath;
        this.resultDataFilePath = resultDataFilePath;
        this.service = new BaseHistogramService();
    }

    public void handle() throws IOException {
        Files.readLines(Paths.get(sourceDataFilePath).toFile(), Charset.defaultCharset(), new LineProcessor<String>() {
            @Override public boolean processLine(String line) throws IOException {
                service.addRecord("A", "a", System.currentTimeMillis(), Long.valueOf(line));
                return true;
            }

            @Override public String getResult() {
                return null;
            }
        });
        service.shuffle(service.acquireAllData(), resultDataFilePath);
    }

}
