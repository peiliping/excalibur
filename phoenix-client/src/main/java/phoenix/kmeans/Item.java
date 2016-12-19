package phoenix.kmeans;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class Item extends IItem {

    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private double[] datas;

    @Override
    public boolean equals(IItem obj) {
        if (obj == null || getDimensionNum() != obj.getDimensionNum()) {
            return false;
        }
        for(int i=0; i<getDimensionNum() ;i++){
            if(datas[i] != obj.getDatas()[i])
                return false ;
        }
        return true;
    }

    @Override
    public double distance(IItem obj) {
        Item i = (Item) obj;
        double r = 0;
        for (int t = 0; t < datas.length; t++) {
            r = r + Math.pow((datas[t] - i.getDatas()[t]), 2);
        }
        return Math.sqrt(r);
    }

    @Override
    public void initPoint(double[] ds) {
        this.datas = ds;
    }

    @Override
    public String toLog() {
        return "id:" + id + " name:" + name + " data:" + Arrays.toString(datas) ;
    }

    @Override
    public void prehandle(List<? extends IItem> items) {

        double[] maxs = getArray(getDimensionNum(), Double.MIN_VALUE);
        double[] minxs = getArray(getDimensionNum(), Double.MAX_VALUE);

        Item p;
        for (IItem i : items) {
            p = (Item) i;
            for (int j = 0; j < p.getDimensionNum(); j++) {
                maxs[j] = Math.max(p.getDatas()[j], maxs[j]);
                minxs[j] = Math.min(p.getDatas()[j], minxs[j]);
            }
        }

        for (IItem tmp : items) {
            p = (Item) tmp;
            for(int j = 0; j < p.getDimensionNum(); j++){
                p.getDatas()[j] = (p.getDatas()[j] -minxs[j]) /(maxs[j] - minxs[j] );
            }
        }
    }

    public static double[] getArray(int num, double def) {
        double[] array = new double[num];
        for (int i = 0; i < num; i++) {
            array[i] = def;
        }
        return array;
    }

}