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
		
		System.out.println(footers.get(0).getParquetMetadata().getFileMetaData().getSchema().toString());
		
		MeepoParquetWriter w = new MeepoParquetWriter(new Path(output), mt);
		String[] datas = { "1", "ss", "dd", "1", "1", "1", "1", "sss" };
		long i = 0;
		long l = System.currentTimeMillis();
		while (i++ < 10000) {
			if (i % 1000000 == 0) {
				System.out.println(System.currentTimeMillis() - l);
				l = System.currentTimeMillis();
			}
			datas[0] = i + "";
			datas[3] = i + "";
			datas[4] = i + "";
			datas[5] = i + "";
			datas[6] = i + "";
			w.write(datas);
		}
		w.close();

	}

}
