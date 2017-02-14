package meepo.reader.parquet;

import com.google.common.collect.Lists;
import meepo.Config;
import meepo.storage.IStorage;
import meepo.tools.IWorker;
import meepo.tools.TypesMapping;
import org.apache.commons.lang3.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.io.ParquetEncodingException;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

public class ParquetFilesReader extends IWorker {

    protected ParquetReader<Group>[] readers;

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
            GroupReadSupport grs = new GroupReadSupport();
            List<Type> types = TypesMapping.getTypes(config.getSourceColumnsArray(), config.getSourceColumnsType());
            grs.init(new Configuration(), null, new MessageType(config.getSourceTableName(), types));
            for (int i = 0; i < fileNames.size(); i++) {
                this.readers[i] = ParquetReader.builder(grs, new org.apache.hadoop.fs.Path(path + fileNames.get(i))).build();
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
                    item = new Object[this.config.getSourceTypesArray().size()];
                    for (int i = 0; i < this.config.getSourceTypesArray().size(); i++) {
                        if (this.record.getFieldRepetitionCount(i) == 0) {
                            item[i] = null;
                            continue;
                        }
                        switch (this.config.getSourceTypesArray().get(i)) {
                            case Types.TINYINT:
                                item[i] = this.record.getInteger(i, 0);
                                break;
                            case Types.SMALLINT:
                                item[i] = this.record.getInteger(i, 0);
                                break;
                            case Types.INTEGER:
                                item[i] = this.record.getInteger(i, 0);
                                break;
                            case Types.BIGINT:
                                item[i] = this.record.getLong(i, 0);
                                break;
                            case Types.BOOLEAN:
                                item[i] = this.record.getBoolean(i, 0);
                                break;
                            case Types.REAL:
                                item[i] = this.record.getFloat(i, 0);
                                break;
                            case Types.FLOAT:
                                item[i] = this.record.getFloat(i, 0);
                                break;
                            case Types.DOUBLE:
                                item[i] = this.record.getDouble(i, 0);
                                break;
                            case Types.TIMESTAMP:
                                item[i] = this.record.getLong(i, 0);
                                break;
                            case Types.DATE:
                                item[i] = this.record.getLong(i, 0);
                                break;
                            case Types.CHAR:
                                item[i] = this.record.getBinary(i, 0).toStringUsingUTF8();
                                break;
                            case Types.VARCHAR:
                                item[i] = this.record.getBinary(i, 0).toStringUsingUTF8();
                                System.out.println(item[i]);
                                break;
                            case Types.LONGVARCHAR:
                                item[i] = this.record.getBinary(i, 0).toStringUsingUTF8();
                                break;
                            default:
                                throw new ParquetEncodingException("Unsupported column type: " + this.config.getSourceTypesArray().get(i));
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
