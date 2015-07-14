package phoenix.taskmanagement.priority;

import lombok.Getter;
import lombok.Setter;

public class Priority {
    @Setter
    @Getter
    protected int level;

    protected int weight; // 0-5

    public Priority(int level, int weight) {
        this.level = level;
        this.weight = weight;
    }

    public int cal() {
        return level * weight;
    }

}
