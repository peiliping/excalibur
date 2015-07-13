package phoenix.taskmanagement.basic;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public abstract class Item {

    @Getter
    @Setter
    protected String              name;
    @Getter
    @Setter
    protected Long                startTime;
    @Getter
    @Setter
    protected Long                endTime;
    @Getter
    @Setter
    protected Long                expireInterval;
    @Getter
    @Setter
    protected Map<String, String> attributes = new HashMap<String, String>();

}
