package meepo.writer.parquet;

import meepo.Config;
import meepo.storage.IStorage;
import meepo.tools.IWorker;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ParquetFilesWriter extends IWorker {

    private static final Logger LOG = LoggerFactory.getLogger(ParquetFilesWriter.class);

    private String outPutFilePath;

    private ParquetWriterHelper writerHelper;

    public ParquetFilesWriter(IStorage<Object[]> buffer, Config config, int index) {
        super(buffer, config, index);
        try {
            List<Type> types = TypesMapping.getTypes(config.getTargetColumnsArray(), config.getTargetColumnsType());
            outPutFilePath = config.getParquetOutputPath() + config.getTargetTableName() + "-" + index + "-" + System.currentTimeMillis() / 1000 + ".parquet";
            this.writerHelper = new ParquetWriterHelper(new Path(outPutFilePath), new MessageType(config.getTargetTableName(), types));
        } catch (IllegalArgumentException | IOException e) {
            LOG.error("Init Writer Helper", e);
        }
    }

    @Override protected void work() {
        final List<Object[]> datas = buffer.get(config.getWriterStepSize());
        if (datas.isEmpty())
            return;

        long t = 0;
        while (!sendData(datas)) {
            try {
                Thread.sleep(100 * t++);
            } catch (InterruptedException e) {
            }
        }
    }

    protected boolean sendData(final List<Object[]> datas) {
        for (Object[] data : datas) {
            try {
                writerHelper.write(data);
            } catch (Exception e) {
                LOG.error("ParquetFilesWriter Write Data Error :", e);
            }
        }
        return true;
    }

    @Override protected void close() {
        try {
            this.writerHelper.close();
            String crcPath = (outPutFilePath + ".crc").replaceAll(config.getTargetTableName(), "." + config.getTargetTableName());
            File crc = Paths.get(crcPath).toFile();
            if (crc.exists())
                crc.delete();
            return;
        } catch (IOException e) {
            LOG.error("Close Writer Helper", e);
        }
        super.close();
    }
}
