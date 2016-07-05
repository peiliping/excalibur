package meepo.writer.parquet;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

public class ParquetWriterHelper extends ParquetWriter<Object[]> {

    @SuppressWarnings("deprecation") public ParquetWriterHelper(Path file, MessageType schema) throws IOException {
        super(file, new ParquetWriterSupport(schema), CompressionCodecName.SNAPPY, ParquetWriter.DEFAULT_BLOCK_SIZE / 4, ParquetWriter.DEFAULT_PAGE_SIZE);
    }

}
