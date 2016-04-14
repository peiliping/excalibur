package phantomlancer.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import phantomlancer.annotation.AvroScan;

public class StrUtils {

    public static String camelConvert(AvroScan avroScan, String name) {
        Validate.isTrue(StringUtils.isNotBlank(name), "Camel Convert Error , Params is Blank!");
        if (!avroScan.camelConvert()) {
            return name;
        }
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1).toLowerCase());
        boolean isUppering = false;
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                if (!isUppering) {
                    result.append("_");
                    isUppering = true;
                }
            } else {
                isUppering = false;
            }
            result.append(s.toLowerCase());
        }
        return result.toString();
    }

}
