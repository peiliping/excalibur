package phoenix.service;

import org.springframework.stereotype.Service;

import com.oneapm.operation.monitor.annotation.Monitor;

@Service
public class Test2LogService {

    @Monitor(metricName = "log21")
    public String log() {
        return "dfdf";
    }

    @Monitor(metricName = "log22")
    public void log2() throws Exception {
        // System.out.println(System.currentTimeMillis() + "\t" + "log2 in");
        throw new Exception("abc");
    }

}
