package phoenix.taskmanagement.result;

import lombok.Getter;


public class SimpleResult {

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
