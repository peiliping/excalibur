package phoenix.parquet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.Footer;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

public class ParquetText {

	public static void main(String[] args) throws IOException {

		String input = "/home/peiliping/dev/logs/account.parquet";
		String output = "/home/peiliping/dev/logs/abc.parquet";

		Configuration conf = new Configuration();
		Path inputPath = new Path(input);
		FileStatus inputFileStatus = inputPath.getFileSystem(conf).getFileStatus(inputPath);
		List<Footer> footers = ParquetFileReader.readFooters(conf, inputFileStatus, false);
		Paths.get(output).toFile().delete();
		MessageType mt = MessageTypeParser
				.parseMessageType(footers.get(0).getParquetMetadata().getFileMetaData().getSchema().toString());
		MeepoParquetWriter w = new MeepoParquetWriter(new Path(output), mt);
		Object[] datas = { 1L, "ss", "dd", 1L, 1L, 1L, 1, "sss" };
		w.write(datas);
		w.close();

	}

}
