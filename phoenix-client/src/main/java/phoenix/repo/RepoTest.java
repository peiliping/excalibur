package phoenix.repo;

import org.springframework.stereotype.Repository;

import com.oneapm.operation.monitor.annotation.Monitor;

@Repository
public class RepoTest {

    @Monitor(metricName = "abc")
    public String v() {
        return "abc";
    }

}
