package jakiro.datasource.threadlocal;

import jakiro.Config;
import jakiro.util.StringUtils;

public class DataSourceBody {

    public String prefix = "";

    public String name   = "";

    public String suffix = "";

    public String rw     = "";

    public String buildName() {
        String result = "";
        if (StringUtils.isNotBlank(prefix)) {
            result = result + prefix + Config.getSPLIT_4_DATASOURCE();
        }
        if (StringUtils.isNotBlank(name)) {
            result = result + name;
        }
        if (StringUtils.isNotBlank(suffix)) {
            result = result + Config.getSPLIT_4_DATASOURCE() + suffix;
        }
        if (StringUtils.isNotBlank(rw)) {
            result = result + Config.getSPLIT_4_RW() + rw;
        }
        return result;
    }

}
