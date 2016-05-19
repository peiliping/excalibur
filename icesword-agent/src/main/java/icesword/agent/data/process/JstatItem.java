package icesword.agent.data.process;

import icesword.agent.data.result.AgeTableDataItem;
import icesword.agent.data.result.GCDataItem;
import icesword.agent.data.result.MemoryDataItem;
import icesword.agent.data.result.ResultData;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.Monitor;

@Setter
@Getter
public class JstatItem implements Cloneable {

    private int     PID;
    private int     S0C;
    private int     S0U;
    private int     S1C;
    private int     S1U;
    private int     EC;
    private int     EU;
    private int     OC;
    private int     OU;
    private Integer PC;
    private Integer PU;
    private Integer MC;
    private Integer MU;
    private Integer CCSC;
    private Integer CCSU;
    private String  YGCType;
    private long    YGC;
    private double  YGCT;
    private String  FGCType;
    private long    FGC;
    private double  FGCT;

    private Long[]  ageTableValues;
    private Long    desiredSurvivorSize;

    private long    timestamp = System.currentTimeMillis();

    private String  vmVersion;
    private String  processSignal;

    public JstatItem(String content, JvmItem jvmItem, List<Monitor> ageTable, Monitor desiredSurvivorSize) {
        this.vmVersion = jvmItem.vmVersion;
        this.processSignal = jvmItem.simpleDesc;
        content = content.trim().replaceAll("\\s+", "");
        String[] tmp = content.split("\001");
        this.PID = jvmItem.pid;
        this.S0C = Integer.valueOf(tmp[0]);
        this.S0U = Integer.valueOf(tmp[1]);
        this.S1C = Integer.valueOf(tmp[2]);
        this.S1U = Integer.valueOf(tmp[3]);
        this.EC = Integer.valueOf(tmp[4]);
        this.EU = Integer.valueOf(tmp[5]);
        this.OC = Integer.valueOf(tmp[6]);
        this.OU = Integer.valueOf(tmp[7]);

        if ("1.7".equals(vmVersion)) {
            this.PC = Integer.valueOf(tmp[8]);
            this.PU = Integer.valueOf(tmp[9]);
            this.YGCType = String.valueOf(tmp[10]);
            this.YGC = Long.valueOf(tmp[11]);
            this.YGCT = Double.valueOf(tmp[12]) * 1000;
            this.FGCType = String.valueOf(tmp[13]);
            this.FGC = Long.valueOf(tmp[14]);
            this.FGCT = Double.valueOf(tmp[15]) * 1000;
        } else if ("1.8".equals(jvmItem.vmVersion)) {
            this.MC = Integer.valueOf(tmp[8]);
            this.MU = Integer.valueOf(tmp[9]);
            this.CCSC = Integer.valueOf(tmp[10]);
            this.CCSU = Integer.valueOf(tmp[11]);
            this.YGCType = String.valueOf(tmp[12]);
            this.YGC = Long.valueOf(tmp[13]);
            this.YGCT = Double.valueOf(tmp[14]) * 1000;
            this.FGCType = String.valueOf(tmp[15]);
            this.FGC = Long.valueOf(tmp[16]);
            this.FGCT = Double.valueOf(tmp[17]) * 1000;
        }
        ageTableValues = new Long[ageTable.size()];
        if (ageTable != null && ageTable.size() > 0) {
            for (int i = 0; i < ageTable.size(); i++) {
                ageTableValues[i++] = ((LongMonitor) ageTable.get(i)).longValue();
            }
            if (desiredSurvivorSize != null) {
                this.desiredSurvivorSize = ((LongMonitor) desiredSurvivorSize).longValue();
            }
        }
    }

    public String buildKey(String key) {
        return vmVersion + "/" + key;
    }

    public void toIDataPool(ResultData memory, ResultData gc, ResultData age) {
        pushMemoryData(memory, "S0", S0C, S0U);
        pushMemoryData(memory, "S1", S1C, S1U);
        pushMemoryData(memory, "EDEN", EC, EU);
        pushMemoryData(memory, "OLD", OC, OU);
        pushMemoryData(memory, "PERM", PC, PU);
        pushMemoryData(memory, "META", MC, MU);
        pushMemoryData(memory, "CCS", CCSC, CCSU);

        boolean p1 = pushGCData(gc, this.YGCType, this.YGC, this.YGCT);
        boolean p2 = pushGCData(gc, this.FGCType, this.FGC, this.FGCT);

        if ((p1 || p2) && ageTableValues.length > 0) {
            for (int i = 0; i < ageTableValues.length; i++) {
                pushAgeData(age, "AgeTable", ageTableValues[i], i, ageTableValues.length, desiredSurvivorSize);
            }
        }
    }

    public void pushAgeData(ResultData rd, String key, Long v, int age, int totalAge, long desiredSurvivorSize) {
        if (v == null || v == 0) {
            return;
        }
        String tkey = buildKey(key);
        AgeTableDataItem ad =
                AgeTableDataItem.builder().times(1d).size(Double.valueOf(v)).age(Double.valueOf(age)).total_age(Double.valueOf(totalAge))
                        .desired_survivor_size(Double.valueOf(desiredSurvivorSize)).dim_age(age).timestamp(timestamp).process_signal(processSignal).build();
        rd.addOne(tkey, ad);
    }

    public boolean pushGCData(ResultData rd, String key, Long v1, Double v2) {
        if (v1 == null || v2 == null)
            return false;
        if (v1 == 0)
            return false;
        String tkey = buildKey(key);
        GCDataItem gd = GCDataItem.builder().times(Double.valueOf(v1)).cost_ms(v2).timestamp(timestamp).process_signal(processSignal).build();
        rd.addOne(tkey, gd);
        return true;
    }

    public boolean pushMemoryData(ResultData rd, String key, Integer v1, Integer v2) {
        if (v1 == null || v2 == null)
            return false;
        String tkey = buildKey(key);
        MemoryDataItem md = MemoryDataItem.builder().times(1d).capacity(Double.valueOf(v1)).used(Double.valueOf(v2)).timestamp(timestamp).process_signal(processSignal).build();
        rd.addOne(tkey, md);
        return true;
    }

    public JstatItem compare(JstatItem last) {
        try {
            JstatItem r = (JstatItem) this.clone();
            r.YGC = this.YGC - last.YGC;
            r.YGCT = this.YGCT - last.YGCT;
            r.FGC = this.FGC - last.FGC;
            r.FGCT = this.FGCT - last.FGCT;
            return r;
        } catch (Exception es) {
            es.printStackTrace();
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (JstatItem) super.clone();
    }
}
