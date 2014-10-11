package pudge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.InitializingBean;

import pudge.entity.Message;
import pudge.entity.Topic;
import pudge.schedule.Schedule;

public class Subscriber implements InitializingBean, SubscriberListener {

    private AtomicReference<Message> mes = new AtomicReference<Message>();

    private Topic                    topic;

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    private Schedule     schedule;

    private ClientConfig conf;

    public void setConf(ClientConfig conf) {
        this.conf = conf;
    }

    private List<SubscriberListener> listeners = new ArrayList<SubscriberListener>();

    public void addListener(SubscriberListener sl) {
        this.listeners.add(sl);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (topic == null)
            throw new IllegalArgumentException("topic is null");

        mes.set(new Message());
        mes.get().setTopic(topic);
        if (conf == null)
            conf = new ClientConfig();

        schedule = new Schedule();
        schedule.run(conf, mes, this);
    }

    @Override
    public boolean notify(Message m) {
        if (m.getTimestamp() > mes.get().getTimestamp()) {
            mes.set(m);
            for (SubscriberListener sl : listeners)
                sl.notify(m);
        }
        return true;
    }
}
