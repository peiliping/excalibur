package phoenix;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import phoenix.config.Config;
import phoenix.config.Context;
import phoenix.service.DualService;
import phoenix.util.Constants;
import phoenix.util.InitTool;

import com.google.common.base.Preconditions;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        Preconditions.checkNotNull(args, "Missing Params");
        Config.setContext(new Context(InitTool.loadFile(args[0])));

        InitTool.initLogBack(Config.getContext().getString(Constants.CONF_LOGCONFIG_ITEM));
        Config.setApplicationContext(new ClassPathXmlApplicationContext(Constants.CONF_SPRING_ITEM));

        LOG.info("=================START=================");

        while (true) {
            DualService ds = Config.getApplicationContext().getBean(DualService.class);
            Arrays.toString(ds.queryDual().toArray());
            Thread.sleep(5);
        }

        //
        // System.out.println(Arrays.toString(ds.queryDual().toArray()));
        // Thread.sleep(1000 * 60);
        // Thread.sleep(1000 * 60 * 30);

        // Files.readLines(Paths.get("/home/peiliping/dev/logs/cc2").toFile(),
        // Charset.defaultCharset(), new LineProcessor<String>() {
        // @Override
        // public String getResult() {
        // return null;
        // }
        //
        // @Override
        // public boolean processLine(String line) throws IOException {
        // JSONObject jo = JSON.parseObject(line).getJSONObject("params");
        // String m = jo.toJSONString();
        // String v = jo.getString("err");
        // if (v != null) {
        // try {
        // JSON.parseArray(URLDecoder.decode(v, "utf-8"));
        // } catch (Exception e) {
        // } catch (Throwable e) {
        // e.printStackTrace();
        // System.out.println("1" + line);
        // System.out.println("2" + m);
        // System.out.println("3" + v);
        // }
        // }
        // return true;
        // }
        // });
        // System.out.println("END");
    }
}
