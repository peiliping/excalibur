package phoenix.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesTool {

    public static Properties loadFile(String configPath) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(configPath));
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

}
