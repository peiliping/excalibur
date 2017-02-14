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
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
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

    private Group tmp = null;

    private int fileIndex = 0;

    @Override public void work() {
        if (this.fileIndex < this.readers.length) {
            while (true) {
                try {
                    this.tmp = this.readers[this.fileIndex].read();
                    if (this.tmp == null) {
                        this.readers[this.fileIndex].close();
                        this.fileIndex++;
                        break;
                    }
                    Thread.sleep(1000);
                    System.out.println(this.tmp);
                } catch (Throwable e) {
                    LOG.error("", e);
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
