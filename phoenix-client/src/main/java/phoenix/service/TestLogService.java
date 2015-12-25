package phoenix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import phoenix.repo.RepoTest;

import com.oneapm.operation.monitor.annotation.Monitor;

@Service
public class TestLogService implements ITestLogService {

    @Autowired
    private RepoTest repoTest ;
    
    @Override
    @Monitor(metricName = "log")
    public String log() {
        return repoTest.v();
    }

    @Monitor(metricName = "log2")
    @Override
    public void log2() throws Exception {
        // System.out.println(System.currentTimeMillis() + "\t" + "log2 in");
        throw new Exception("abc");
    }

}
