package phoenix.kmeans;

import java.util.List;

public abstract class IItem {
    
    public IItem(){}
    
    /**
     * 比较两个数据是不是相等
     * @param obj
     * @return
     */
    public abstract boolean equals(IItem obj);
    
    /**
     * 求两个数据之前的距离
     * @param obj
     * @return
     */
    public abstract double distance(IItem obj);
    
    /**
     * 将数据以数组的形式返回
     * @return
     */
    public abstract double[] getDatas();
    
    /**
     * 维度的个数
     * @return
     */
    public abstract int getDimensionNum();
    
    /**
     * 初始化用途
     * @param ds
     */
    public abstract void initPoint(double[] ds);
    
    /**
     * 打印用途
     * @return
     */
    public abstract String toLog();
    
    /**
     * 归一化处理
     * @param tmp_items
     */
    public abstract void prehandle(List<? extends IItem> items);
    
}