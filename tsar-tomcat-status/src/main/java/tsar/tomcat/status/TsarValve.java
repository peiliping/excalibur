package tsar.tomcat.status;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.AccessLogValve;

public class TsarValve extends AccessLogValve {

    @Override
    public void log(Request request, Response response, long time) {
        TsarFilter.COST.addAndGet(time);
        TsarFilter.OUT.incrementAndGet();
        int u = (response.getStatus() / 100);
        if (u >= 1 && u <= 5)
            TsarFilter.HTTPCODES[u - 1].incrementAndGet();
        super.log(request, response, time);
    }

}
