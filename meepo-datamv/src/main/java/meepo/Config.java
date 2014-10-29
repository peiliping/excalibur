package meepo;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    @Setter
    @Getter
    private String               sourceTableName;
    @Setter
    @Getter
    private String               targetTableName;
    @Setter
    @Getter
    private String               primaryKeyName;
    @Setter
    @Getter
    private int                  readerStepSize;
    @Setter
    @Getter
    private int                  writerStepSize;
    @Setter
    @Getter
    private String               sourceColumsNames;
    @Setter
    @Getter
    private String               targetColumsNames;
    @Setter
    @Getter
    private Map<String, Integer> sourceColumsType;
    @Setter
    @Getter
    private List<String>         sourceColumsArray;
    @Setter
    @Getter
    private Map<String, Integer> targetColumsType;
    @Setter
    @Getter
    private List<String>         targetColumsArray;
    @Setter
    @Getter
    private AtomicLong           start;            // start为实际最小值-1
    @Setter
    @Getter
    private AtomicLong           end;              // end为实际最大值
    @Setter
    @Getter
    private boolean              syncMode;
    @Setter
    @Getter
    private int                  bufferSize;
    @Setter
    @Getter
    private int                  readersNum;
    @Setter
    @Getter
    private int                  writersNum;

    public Config(Properties ps) {
        // ==================Required Config Item===================
        this.sourceTableName = ps.getProperty("sourcetablename");
        this.targetTableName = ps.getProperty("targettablename");
        this.sourceColumsNames = ps.getProperty("sourcecolumsnames");
        this.targetColumsNames = ps.getProperty("targetcolumsnames");
        Validate.notNull(this.sourceTableName);
        Validate.notNull(this.targetTableName);
        Validate.notNull(this.sourceColumsNames);
        Validate.notNull(this.targetColumsNames);
        // =========================================================
        this.primaryKeyName = ps.getProperty("primarykeyname", "id");
        this.readerStepSize = Integer.valueOf(ps.getProperty("readerstepsize", "100"));
        this.writerStepSize = Integer.valueOf(ps.getProperty("writerstepsize", "100"));
        this.start = ps.getProperty("start") == null ? null : new AtomicLong(Long.valueOf(ps.getProperty("start")));
        this.end = ps.getProperty("end") == null ? null : new AtomicLong(Long.valueOf(ps.getProperty("end")));
        this.syncMode = Boolean.valueOf(ps.getProperty("syncmode", "false"));
        this.bufferSize = Integer.valueOf(ps.getProperty("buffersize", "1024"));
        this.readersNum = Integer.valueOf(ps.getProperty("readersnum", "1"));
        this.writersNum = Integer.valueOf(ps.getProperty("writersnum", "1"));
    }

    public void initStartEnd(Pair<Long, Long> ps) {
        this.start = new AtomicLong(ps.getLeft());
        this.end = new AtomicLong(ps.getRight());
    }

    public boolean needAutoInitStartEnd() {
        return start == null;
    }

}
