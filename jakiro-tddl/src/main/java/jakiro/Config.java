package jakiro;


public class Config {

    // 数据源前缀尾缀的分隔符号
    private static String SPLIT_4_DATASOURCE = "_";

    public static String getSPLIT_4_DATASOURCE() {
        return SPLIT_4_DATASOURCE;
    }

    public static void setSPLIT_4_DATASOURCE(String sPLIT_4_DATASOURCE) {
        SPLIT_4_DATASOURCE = sPLIT_4_DATASOURCE;
    }

    // 读写数据源分离的分隔符
    private static String SPLIT_4_RW = "-";

    public static String getSPLIT_4_RW() {
        return SPLIT_4_RW;
    }

    public static void setSPLIT_4_RW(String sPLIT_4_RW) {
        SPLIT_4_RW = sPLIT_4_RW;
    }

    //Sequnce的数据源名字中，序号尾缀的分隔符
    private static String SPLIT_4_SEQ_DSNAME = "_";

    public static String getSPLIT_4_SEQ_DSNAME() {
        return SPLIT_4_SEQ_DSNAME;
    }

    public static void setSPLIT_4_SEQ_DSNAME(String sPLIT_4_SEQ_DSNAME) {
        SPLIT_4_SEQ_DSNAME = sPLIT_4_SEQ_DSNAME;
    }


}
