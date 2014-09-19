package jakiro.sequence.dao;

import jakiro.sequence.seq.SequenceRange;


public class GroupSequenceDao extends AbstractSequenceDao {

    @Override
    protected SequenceRange buildReturnRange(long newV, long oldV) {
        return new SequenceRange(newV + 1, newV + step);
    }

    @Override
    protected long buildNewValue(long old, int index, int total) {
        return old + step * total;
    }

    @Override
    protected boolean check(long oldV, long newV, int index, int total) {
        return (newV % (step * total)) == (index * step + index + 1);
    }

}
