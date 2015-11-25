package phoenix.service;

import org.springframework.stereotype.Service;

import com.oneapm.operation.monitor.annotation.Monitor;

@Service
public class TestLogService implements ITestLogService {

    @Monitor(metricName = "log")
    @Override
    public String log() {
        return "dfdf";
    }

    @Monitor(metricName = "log2")
    @Override
    public void log2() throws Exception {
        // System.out.println(System.currentTimeMillis() + "\t" + "log2 in");
        throw new Exception("abc");
    }

}
