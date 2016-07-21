package sniper.histogram.reader;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import sniper.histogram.service.TimeWindowHistogramService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

/**
 * Created by peiliping on 16-7-15.
 */
public class WindowHistogramReader {

    protected String sourceDataFilePath;

    protected String resultDataFilePath;

    protected final TimeWindowHistogramService service;

    public WindowHistogramReader(String sourceDataFilePath, String resultDataFilePath, long interval) {
        this.sourceDataFilePath = sourceDataFilePath;
        this.resultDataFilePath = resultDataFilePath;
        this.service = new TimeWindowHistogramService(interval);
    }

    public void handle() throws IOException {
        Files.readLines(Paths.get(sourceDataFilePath).toFile(), Charset.defaultCharset(), new LineProcessor<String>() {
            @Override public boolean processLine(String line) throws IOException {
                String[] splits = line.split(" ");
                service.addRecord("A", "a", Long.valueOf(splits[0]), Long.valueOf(splits[1]));
                return true;
            }

            @Override public String getResult() {
                return null;
            }
        });
        service.shuffle(service.acquireAllData(), resultDataFilePath);
    }


}
