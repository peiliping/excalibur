package phoenix.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Kmeans<T extends IItem> {
    
    /** 
     * 所有数据列表 
     */   
    private List<T> items ;  
  
    /** 
     * 数据类别 
     */    
    private Class<T> clazz ;  
  
    /** 
     * 中心点集合 
     */  
    private List<T> seedList;  

    /** 
     * 分类数 
     */  
    private int k = 1;  

    public Kmeans(List<T> list, int k,Class<T> clazz) {  
        this.items = list;  
        this.k = k;
        this.clazz = clazz;
    }  

    /**
     * 执行聚类运算
     * @return
     */
    public Result run() throws InstantiationException, IllegalAccessException {  
        (clazz.newInstance()).prehandle(items);
        seedList = new ArrayList<T>(items.subList(0,k)); //默认选几个数据点当中心  
        @SuppressWarnings("unchecked")
        List<T>[] results = new ArrayList[k];  
        boolean centerChanged = true;  
        while (centerChanged) {  
            centerChanged = false; 
            //清空结果数组
            for (int i = 0; i < k; i++) {  
                if(results[i]==null){
                    results[i]=new ArrayList<T>();
                }else{
                    results[i].clear();
                } 
            }  
            //运算每个数据点与种子的距离，投放到距离近的种子对应的结果集中
            T tmp_item;
            int min_index=0;
            double min_dist=Double.MAX_VALUE,tmp_dist;
            for (int i = 0; i < items.size(); i++) {  
                tmp_item = items.get(i);
                min_dist=Double.MAX_VALUE;
                for (int j = 0; j < seedList.size(); j++) {  
                    tmp_dist = seedList.get(j).distance(tmp_item);  
                    if(tmp_dist<min_dist){
                        min_index=j;
                        min_dist=tmp_dist;
                    }  
                }  
                results[min_index].add(tmp_item);  
            } 
            //找新的中心点，更换掉种子
            for (int i = 0; i < k; i++) {  
                if(results[i]==null || results[i].size()==0){
                    continue;
                }
                T t_new = findNewCenter(results[i]);  
                if (!seedList.get(i).equals(t_new)){  
                    centerChanged = true;  
                    seedList.set(i, t_new);  
                }  
            }  
            System.out.println("==");
        }  
        return new Result(true, results, seedList);  
    }    
  
    /** 
     * 得到新聚类中心对象 
     * @param ps 
     * @return 
     */  
    public T findNewCenter(List<T> ps) throws InstantiationException,IllegalAccessException {
        T t = clazz.newInstance();
        int fieldnum = t.getDimensionNum();
        double[] ds = new double[fieldnum];
        double[] tmpd;
        for (T vo : ps) {
            tmpd = vo.getDatas();
            for (int i = 0; i < fieldnum; i++) {
                ds[i] += tmpd[i];
            }
        }
        for (int i = 0; i < fieldnum; i++) {
            ds[i] = ds[i] / ps.size();
        }
        t.initPoint(ds);
        return t;
    }
    
    public void loadDataFile(String filepath,int dimensionNum,Class<T> clazz,String split)
            throws NumberFormatException, IOException, InstantiationException, IllegalAccessException{
        List<T> list = new ArrayList<T>();
        File file = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        T p =  null ;
        while ((tempString = reader.readLine()) != null) {
            p = clazz.newInstance();
            double[] s = new double[dimensionNum];
            for(int i=0; i <dimensionNum ;i++){
                s[i]=Double.parseDouble(tempString.split(split)[i]);
            }
            p.initPoint(s);
            list.add(p);
        }
        reader.close();
        items = list ;
    }
     
    public class Result {
        /**
         * 处理结果
         */
        public boolean success = true ;
        /**
         * 数据分组后的结果
         */
        public List<T>[] classifyResults ;
        /**
         * 中心点集合
         */
        public List<T> cores ;

        public Result(boolean success,List<T>[] classifyResults,List<T> cores){
            this.success = success;
            this.classifyResults = classifyResults;
            this.cores = cores;
        }
    }
}