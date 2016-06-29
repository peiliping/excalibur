package phoenix;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class B {

	private int id;

	private String process;

	private long ts;

	private double count;

	private double cost;
}
