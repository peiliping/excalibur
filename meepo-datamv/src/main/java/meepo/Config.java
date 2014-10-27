package meepo;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

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
    private int                  stepSize;
    @Setter
    @Getter
    private String               sourceColumsNames;
    @Setter
    @Getter
    private String               targetColumsNames;
    @Setter
    @Getter
    private Map<String, Integer> sourceSchema;
    @Setter
    @Getter
    private List<String>         sourceColumsArray;
    @Setter
    @Getter
    private Map<String, Integer> targetSchema;
    @Setter
    @Getter
    private List<String>         targetColumsArray;
    @Setter
    @Getter
    private AtomicLong           start;
    @Setter
    @Getter
    private AtomicLong           end;
    @Setter
    @Getter
    private int                  bufferSize;
    @Setter
    @Getter
    private int                  readersNum = 1;
    @Setter
    @Getter
    private int                  writersNum = 1;

    public Config(Properties ps) {
        this.sourceTableName = ps.getProperty("sourcetablename");
        this.targetTableName = ps.getProperty("targettablename");
        this.primaryKeyName = ps.getProperty("primarykeyname", "id");
        this.stepSize = Integer.valueOf(ps.getProperty("stepsize", "100"));
        this.sourceColumsNames = ps.getProperty("sourcecolumsnames");
        this.targetColumsNames = ps.getProperty("targetcolumsnames");
        this.start = new AtomicLong(Long.valueOf(ps.getProperty("start", "-1")));
        this.end = new AtomicLong(Long.valueOf(ps.getProperty("end", "-1")));
        this.bufferSize = Integer.valueOf(ps.getProperty("buffersize", "1024"));
    }

    public void initStartEnd(Pair<Long, Long> ps) {
        this.start.set(ps.getLeft());
        this.end.set(ps.getRight());
    }

    public boolean needAutoInitStartEnd() {
        return start.get() < 0 && end.get() < 0;
    }

}
