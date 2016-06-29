package phoenix;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class N {

	public static void main(String[] args) {

		// List<B> list = Lists.newArrayList();
		// list.add(B.builder().id(1).process("abc").ts(123456781).count(1).cost(1).build());
		// list.add(B.builder().id(2).process("abc").ts(123456782).count(1).cost(1).build());
		// list.add(B.builder().id(1).process("bcd").ts(123456783).count(1).cost(1).build());
		// list.add(B.builder().id(1).process("abc").ts(123456784).count(1).cost(1).build());
		// list.add(B.builder().id(2).process("abc").ts(123456785).count(1).cost(1).build());
		// list.add(B.builder().id(1).process("bcd").ts(123456786).count(1).cost(1).build());

		List<B> list = Lists.newArrayList();
		list.add(B.builder().id(1).ts(123456781).count(1).cost(1).build());
		list.add(B.builder().id(2).ts(123456782).count(1).cost(1).build());
		list.add(B.builder().id(1).ts(123456783).count(1).cost(1).build());
		list.add(B.builder().id(1).ts(123456784).count(1).cost(1).build());
		list.add(B.builder().id(2).ts(123456785).count(1).cost(1).build());
		list.add(B.builder().id(1).ts(123456786).count(1).cost(1).build());

		Map<String, Pair<Integer, String>> m1 = Maps.newHashMap();
		Map<String, List<Triple<Long, Double, Double>>> m2 = Maps.newHashMap();

		for (B b : list) {
			String key = b.getId() + (b.getProcess() == null ? "" : b.getProcess());
			if (!m1.containsKey(key)) {
				m1.put(key, Pair.of(b.getId(), b.getProcess()));
				m2.put(key, Lists.newArrayList());
			}
			m2.get(key).add(Triple.of(b.getTs(), b.getCount(), b.getCost()));
		}

		List<Z> result = Lists.newArrayList();
		for (Map.Entry<String, Pair<Integer, String>> item : m1.entrySet()) {
			Z z = new Z();
			z.setId(item.getValue().getLeft());
			z.setProcess(item.getValue().getRight());
			z.setData(m2.get(item.getKey()));
			result.add(z);
		}

		System.out.println(JSON.toJSON(result));
	}

}
