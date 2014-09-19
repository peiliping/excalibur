package jakiro;

import jakiro.sequence.SequenceException;
import jakiro.sequence.dao.DefaultSequenceDao;
import jakiro.sequence.dao.GroupSequenceDao;
import jakiro.sequence.dao.SequenceDao;
import jakiro.sequence.seq.DefaultSequence;
import jakiro.sequence.seq.Sequence;
import jakiro.util.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

public class SequenceService implements InitializingBean {

    public static HashMap<String, Class<? extends Sequence>>    SequenceClassMap    = new HashMap<String, Class<? extends Sequence>>();
    static {
        SequenceClassMap.put(DefaultSequence.class.getCanonicalName(), DefaultSequence.class);
    }

    public static HashMap<String, Class<? extends SequenceDao>> SequenceDaoClassMap = new HashMap<String, Class<? extends SequenceDao>>();
    static {
        SequenceDaoClassMap.put(DefaultSequenceDao.class.getCanonicalName(), DefaultSequenceDao.class);
        SequenceDaoClassMap.put(GroupSequenceDao.class.getCanonicalName(), GroupSequenceDao.class);
    }

    private int                                                 step                = 1000;

    public void setStep(int step) {
        this.step = step;
    }

    private int sequenceNum = 1;

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    private DynamicDataSource dataSource;

    public void setDataSource(DynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String sequenceClazzName = DefaultSequence.class.getCanonicalName();

    public void setSequenceClazzName(String sequenceClazzName) {
        this.sequenceClazzName = sequenceClazzName;
    }

    private String sequenceDaoClazzName = DefaultSequenceDao.class.getCanonicalName();

    public void setSequenceDaoClazzName(String sequenceDaoClazzName) {
        this.sequenceDaoClazzName = sequenceDaoClazzName;
    }

    private String sequenceName; // seq表中name列的值

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    private String dsNameSeries; // 数据源序列的公共部分 后面是_0, _1, _2

    public void setDsNameSeries(String dsNameSeries) {
        this.dsNameSeries = dsNameSeries;
    }

    private List<Sequence> sequenceList = new ArrayList<Sequence>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.isTrue(sequenceNum >= 1);
        Validate.isTrue(dataSource != null);
        Validate.isTrue(sequenceClazzName != null);

        for (int i = 0; i < sequenceNum; i++) {
            SequenceDao sd = SequenceDaoClassMap.get(sequenceDaoClazzName).newInstance();
            sd.setDataSource((DataSource) dataSource.allDataSources().get(dsNameSeries + Config.getSPLIT_4_SEQ_DSNAME() + i));
            sd.setStep(step);

            Sequence s = SequenceClassMap.get(sequenceClazzName).newInstance();
            s.setKeyName(sequenceName);
            s.setSequenceDao(sd);
            sequenceList.add(s);
        }
    }

    public long nextValue() throws SequenceException {
        return nextValue(null);
    }

    public long nextValue(String keyName) throws SequenceException {
        int i = 0;
        if (sequenceNum > 1) {
            Random random = new Random();
            i = random.nextInt(sequenceNum);
        }
        return sequenceList.get(i).nextValue(keyName, i, sequenceNum);
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
