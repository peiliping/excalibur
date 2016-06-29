package phoenix.parquet;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

public class MeepoParquetWriter extends ParquetWriter<String[]> {

	@SuppressWarnings("deprecation")
	public MeepoParquetWriter(Path file, MessageType schema) throws IOException {
		super(file, new MeepoWriteSupport(schema), CompressionCodecName.SNAPPY, ParquetWriter.DEFAULT_BLOCK_SIZE,
				ParquetWriter.DEFAULT_PAGE_SIZE);
	}

}
