package phoenix.datasource;

import jakiro.DynamicDataSource;
import lombok.Getter;
import lombok.Setter;

public class VDynamicDataSource extends DynamicDataSource {

	@Setter
	@Getter
	private Object condition;

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
	}

}
