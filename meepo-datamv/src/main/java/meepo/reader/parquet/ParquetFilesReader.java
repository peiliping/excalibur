package meepo.reader.parquet;

import com.google.common.collect.Lists;
import meepo.Config;
import meepo.storage.IStorage;
import meepo.tools.IWorker;
import org.apache.commons.lang3.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ParquetEncodingException;
import org.apache.parquet.schema.MessageType;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class ParquetFilesReader extends IWorker {

    private ParquetReader<Group>[] readers;

    private MessageType schema;

    public ParquetFilesReader(IStorage<Object[]> buffer, Config config, int index) {
        super(buffer, config, index);
        String path = config.getParquetInputPath();
        File dir = Paths.get(path).toFile();
        Validate.isTrue(dir.isDirectory());
        List<String> fileNames = Lists.newArrayList(dir.list(createFileNameFilter()));
        Validate.isTrue(fileNames.size() > 0);
        Collections.sort(fileNames);
        this.readers = new ParquetReader[fileNames.size()];
        try {
            for (int i = 0; i < fileNames.size(); i++) {
                Path filePath = new org.apache.hadoop.fs.Path(path + fileNames.get(i));
                ParquetMetadata metaData = ParquetFileReader.readFooter(new Configuration(), filePath);
                this.schema = metaData.getFileMetaData().getSchema();
                GroupReadSupport grs = new GroupReadSupport();
                grs.init(new Configuration(), null, new MessageType(config.getSourceTableName(), schema.getFields()));
                this.readers[i] = ParquetReader.builder(grs, new Path(path + fileNames.get(i))).build();
            }
        } catch (Exception e) {
            LOG.error("Create ParquetFilesReader Error :", e);
        }
    }

    private Group record = null;

    private Object[] item = null;

    private int fileIndex = 0;

    @Override public void work() {
        if (this.fileIndex < this.readers.length) {
            while (true) {
                try {
                    this.record = this.readers[this.fileIndex].read();
                    if (this.record == null) {
                        this.readers[this.fileIndex].close();
                        this.fileIndex++;
                        break;
                    }
                    item = new Object[this.schema.getFieldCount()];
                    for (int i = 0; i < this.schema.getFieldCount(); i++) {
                        if (this.record.getFieldRepetitionCount(i) == 0) {
                            item[i] = null;
                            continue;
                        }
                        switch (this.schema.getFields().get(i).asPrimitiveType().getPrimitiveTypeName()) {
                            case INT32:
                                item[i] = this.record.getInteger(i, 0);
                                break;
                            case INT64:
                                item[i] = this.record.getLong(i, 0);
                                break;
                            case BOOLEAN:
                                item[i] = this.record.getBoolean(i, 0);
                                break;
                            case BINARY:
                                item[i] = this.record.getBinary(i, 0).toStringUsingUTF8();
                                break;
                            case FLOAT:
                                item[i] = this.record.getFloat(i, 0);
                                break;
                            case DOUBLE:
                                item[i] = this.record.getDouble(i, 0);
                                break;
                            default:
                                throw new ParquetEncodingException("Unsupported column type: " + this.schema.getFields().get(i));
                        }
                    }
                    buffer.add(item);
                } catch (Throwable e) {
                    LOG.error("Handle Parquet Data Error", e);
                }
            }
        } else {
            RUN = false;
        }
    }

    private FilenameFilter createFileNameFilter() {
        return new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return name.endsWith(".parquet") || name.endsWith(".pqt");
            }
        };
    }
}
