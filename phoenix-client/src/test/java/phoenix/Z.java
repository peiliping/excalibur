package phoenix;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Z {

	private int id;

	private String process;

	private List<Triple<Long, Double, Double>> data = Lists.newArrayList();

}
