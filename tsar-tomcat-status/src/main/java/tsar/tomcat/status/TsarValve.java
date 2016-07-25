package tsar.tomcat.status;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.AccessLogValve;

public class TsarValve extends AccessLogValve {

    @Override
    public void log(Request request, Response response, long time) {
        TsarFilter.COST.addAndGet(time);
        super.log(request, response, time);
    }

}
