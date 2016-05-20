package icesword.agent.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.alibaba.fastjson.JSON;

public class CompressUtil {

    public static <K> byte[] compress(K k) throws IOException {
        byte[] jsonData = JSON.toJSONBytes(k);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(1024);
        GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput, 1024);
        gzipOutput.write(jsonData);
        gzipOutput.close();
        return byteOutput.toByteArray();
    }

}
