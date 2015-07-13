package phoenix.taskmanagement.result;

import lombok.Getter;


public class SimpleResult {

    public final static int S_INIT     = 0;
    public final static int S_WAITING  = 1;
    public final static int S_RUNNING  = 2;
    public final static int S_FINISHED = 3;

    @Getter
    protected int           status     = 0;

    public boolean isFinished() {
        return this.status == S_FINISHED;
    }

    @Getter
    private boolean success;
    @Getter
    private String  reason;

    public SimpleResult(boolean s, String rs) {
        this.success = s;
        this.reason = rs;
    }

    public SimpleResult(boolean s) {
        this.success = s;
    }

}
