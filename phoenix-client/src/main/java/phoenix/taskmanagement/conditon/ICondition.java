package phoenix.taskmanagement.conditon;

import phoenix.taskmanagement.Item;
import phoenix.taskmanagement.result.SimpleResult;

public abstract class ICondition extends Item {

    public abstract SimpleResult check();

}
