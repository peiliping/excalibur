package phoenix.kmeans;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException {

        String filepath = "xxx" ;
        Kmeans<Item3> kmeans = new Kmeans<Item3>(null,5,Item3.class);
        kmeans.loadDataFile(filepath,3,Item3.class," "); 
        Kmeans<Item3>.Result R = kmeans.run();
        printResult(R);
    }
    
    private static void printResult(Kmeans<Item3>.Result R){
        List<Item3>[] results = R.classifyResults;
        List<Item3> cores = R.cores;
        for (int i = 0; i < results.length; i++) {
            System.out.println("===========类别" + (i + 1) + "================");
            System.out.print( "种子:" + Arrays.toString(cores.get(i).getDatas()) + "\t数据:");
            for (IItem op : results[i]) {
                System.out.print( Arrays.toString(op.getDatas()) );
            }
            System.out.println("");
        }
    }
}