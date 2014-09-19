package jakiro.sequence.dao;

import jakiro.sequence.seq.SequenceRange;


public class DefaultSequenceDao extends AbstractSequenceDao {

    @Override
    protected long buildNewValue(long old, int index, int total) {
        return old + step;
    }

    @Override
    protected SequenceRange buildReturnRange(long newV, long oldV) {
        return new SequenceRange(oldV + 1, newV);
    }

    @Override
    protected boolean check(long oldV, long newV, int index, int total) {
        return true;
    }

}
