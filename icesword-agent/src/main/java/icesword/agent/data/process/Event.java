package icesword.agent.data.process;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {

    private int    statusCode; // 0 = error 1 = normal

    private String msg;

    private long   timestamp;

    public Event(int status, String msg) {
        this.statusCode = status;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

}
