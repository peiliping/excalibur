package meepo.reader.parquet;

import com.google.common.collect.Lists;
import meepo.Config;
import meepo.storage.IStorage;
import meepo.tools.IWorker;
import org.apache.commons.lang3.Validate;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
            for (int i = 0; i < fileNames.size(); i++) {
                this.readers[i] = ParquetReader.builder(new GroupReadSupport(), new org.apache.hadoop.fs.Path(fileNames.get(i))).build();
            }
        } catch (Exception e) {
            LOG.error("Create ParquetFilesReader Error :", e);
        }
    }

    @Override public void work() {
        Group r = null;
        for (ParquetReader<Group> pr : this.readers) {
            while (true) {
                try {
                    r = pr.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
