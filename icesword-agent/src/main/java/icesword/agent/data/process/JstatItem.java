package icesword.agent.data.process;

import icesword.agent.data.result.GCDataItem;
import icesword.agent.data.result.MemoryDataItem;
import icesword.agent.data.result.ResultData;
import lombok.Getter;
import lombok.Setter;

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

    private long    timestamp = System.currentTimeMillis();

    private String  vmVersion;
    private String  processSignal;

    public JstatItem(String content, JvmItem jvmItem) {
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
            this.YGCT = Double.valueOf(tmp[12]);
            this.FGCType = String.valueOf(tmp[13]);
            this.FGC = Long.valueOf(tmp[14]);
            this.FGCT = Double.valueOf(tmp[15]);
        } else if ("1.8".equals(jvmItem.vmVersion)) {
            this.MC = Integer.valueOf(tmp[8]);
            this.MU = Integer.valueOf(tmp[9]);
            this.CCSC = Integer.valueOf(tmp[10]);
            this.CCSU = Integer.valueOf(tmp[11]);
            this.YGCType = String.valueOf(tmp[12]);
            this.YGC = Long.valueOf(tmp[13]);
            this.YGCT = Double.valueOf(tmp[14]);
            this.FGCType = String.valueOf(tmp[15]);
            this.FGC = Long.valueOf(tmp[16]);
            this.FGCT = Double.valueOf(tmp[17]);
        }
    }

    public String buildKey(String key) {
        return vmVersion + "/" + key;
    }

    public void toIDataPool(ResultData memory, ResultData gc) {
        pushMemoryData(memory, "S0", S0C, S0U);
        pushMemoryData(memory, "S1", S1C, S1U);
        pushMemoryData(memory, "EDEN", EC, EU);
        pushMemoryData(memory, "OLD", OC, OU);
        pushMemoryData(memory, "PERM", PC, PU);
        pushMemoryData(memory, "META", MC, MU);
        pushMemoryData(memory, "CCS", CCSC, CCSU);

        pushGCData(gc, this.YGCType, this.YGC, this.YGCT);
        pushGCData(gc, this.FGCType, this.FGC, this.FGCT);
    }

    public void pushGCData(ResultData rd, String key, Long v1, Double v2) {
        if (v1 == null || v2 == null)
            return;
        if (v1 == 0)
            return;
        String tkey = buildKey(key);
        GCDataItem gd = GCDataItem.builder().times(Double.valueOf(v1)).cost_ms(v2).timestamp(timestamp).process_signal(processSignal).build();
        rd.addOne(tkey, gd);
    }

    public void pushMemoryData(ResultData rd, String key, Integer v1, Integer v2) {
        if (v1 == null || v2 == null)
            return;
        String tkey = buildKey(key);
        MemoryDataItem md = MemoryDataItem.builder().times(1d).capacity(Double.valueOf(v1)).used(Double.valueOf(v2)).timestamp(timestamp).process_signal(processSignal).build();
        rd.addOne(tkey, md);
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
