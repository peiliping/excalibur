package phoenix.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.Getter;

import org.apache.commons.lang3.Validate;

public class HBasePartitionUtil {
    Random      random = new Random();
    @Getter
    private int nodes;
    @Getter
    private int partitions;

    public HBasePartitionUtil(int nodes, int partitions) {
        this.nodes = nodes;
        this.partitions = partitions;
    }

    public Set<Integer> getNodeIds(long start, long end, long applicationId, long metricTypeId) {
        Set<Integer> mm = new HashSet<Integer>();
        for (int i = 0; i <= end - start; i++) {
            int[] tmp = getNodeId((start + i) * 3600000, applicationId, metricTypeId);
            for (int t : tmp) {
                mm.add(t);
            }
        }
        return mm;
    }

    public int[] getNodeId(long ts, long applicationId, long metricTypeId) {
        Validate.isTrue(metricTypeId != 0);
        metricTypeId = Math.abs(metricTypeId);
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        c.get(Calendar.DAY_OF_YEAR);
        int partition = (int) (c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.DAY_OF_YEAR) + metricTypeId) % this.getPartitions();
        return this.getNodeIds(partition);
    }

    public int[] getNodeIds(int partition) {
        int size = this.getNodes() / this.getPartitions() + (this.getNodes() % this.getPartitions() > partition ? 1 : 0);
        int[] nodes = new int[size];
        int idx = partition;
        while (idx < this.getNodes()) {
            nodes[idx / this.getPartitions()] = idx + 1;
            idx += this.partitions;
        }
        return nodes;
    }

    public static int[]      CONS_SALT = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
                    37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76,
                    77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
                    113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
                    145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176,
                    177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208,
                    209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240,
                    241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256};

    public static List<Long> CON_SALTS = new ArrayList<Long>();
    static {
        for (int i : CONS_SALT) {
            CON_SALTS.add(Long.valueOf(i));
        }
    }
}
