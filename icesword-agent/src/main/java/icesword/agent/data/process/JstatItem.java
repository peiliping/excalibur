package icesword.agent.data.process;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JstatItem {

    private int    PID;
    private int    S0C;
    private int    S0U;
    private int    S1C;
    private int    S1U;
    private int    EC;
    private int    EU;
    private int    OC;
    private int    OU;
    private int    MC;
    private int    MU;
    private int    CCSC;
    private int    CCSU;
    private String YGCType;
    private long   YGC;
    private double YGCT;
    private String FGCType;
    private long   FGC;
    private double FGCT;

}
