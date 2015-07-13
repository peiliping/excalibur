package phoenix.taskmanagement.result;

import lombok.Getter;
import lombok.Setter;

import com.alibaba.fastjson.JSON;

public class SimpleProccessResult {

    @Getter
    private int     total        = 0;
    @Getter
    private int     finished     = 0;
    @Getter
    private int     failed       = 0;
    @Getter
    private int     running      = 0;
    @Getter
    private int     waiting      = 0;
    @Getter
    private boolean success      = false;
    @Getter
    @Setter
    private String  failedReason = "";

    public SimpleProccessResult(int total, int finished, int running, int waiting, int failed) {
        this.total = total;
        this.finished = finished;
        this.running = running;
        this.waiting = waiting;
        this.failed = failed;
        if (finished == total)
            this.success = true;
    }

    public boolean isProcessing() {
        return running + waiting > 0;
    }

    public boolean hasErrors() {
        return failed > 0;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
