package phoenix.taskmanagement.resource;

import lombok.Getter;
import lombok.Setter;


public class Resource {

    @Getter
    @Setter
    protected ResourceType type;
    @Getter
    @Setter
    protected String       path;

    @Override
    public String toString() {
        return type.toString() + "" + path;
    }

}
