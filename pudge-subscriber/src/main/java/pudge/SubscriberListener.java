package pudge;

import pudge.entity.Message;

public interface SubscriberListener {

    public boolean notify(Message m);

}
