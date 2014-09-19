package jakiro.sequence.seq;

import jakiro.sequence.SequenceException;
import jakiro.sequence.dao.SequenceDao;

public interface Sequence {

    long nextValue(String keyName,int index, int total) throws SequenceException;

    void setKeyName(String name);

    void setSequenceDao(SequenceDao sequenceDao);

    SequenceDao getSequenceDao();
}
