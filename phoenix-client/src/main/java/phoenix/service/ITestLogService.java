package phoenix.service;

import com.oneapm.operation.monitor.annotation.Monitor;

public interface ITestLogService {

//    @Monitor(metricName = "log")
    public String log();

//    @Monitor(metricName = "log2")
    public void log2() throws Exception;

}
