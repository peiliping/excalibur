package phoenix.service;

import org.springframework.stereotype.Service;

import com.oneapm.operation.monitor.annotation.Monitor;

@Service
public class TestLogService {

    @Monitor(metricName = "log")
    public void log() {
        // System.out.println(System.currentTimeMillis() + "\t" + "log in");
    }

    @Monitor(metricName = "log2")
    public void log2() throws Exception {
        // System.out.println(System.currentTimeMillis() + "\t" + "log2 in");
        throw new Exception("abc");
    }

}
