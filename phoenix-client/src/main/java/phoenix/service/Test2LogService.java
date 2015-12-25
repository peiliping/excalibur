package phoenix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import phoenix.repo.RepoTest;

import com.oneapm.operation.monitor.annotation.Monitor;

@Service
public class Test2LogService {

    @Autowired
    private RepoTest repoTest;

    @Monitor(metricName = "log21")
    public String log() {
        return repoTest.v();
    }

    @Monitor(metricName = "log22")
    public void log2() throws Exception {
        // System.out.println(System.currentTimeMillis() + "\t" + "log2 in");
        throw new Exception("abc");
    }

}
