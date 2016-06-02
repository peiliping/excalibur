package phoenix.datasource.davincicode.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfigItem {

	private long id;

	private String key;

	private long version;

	private Map<String, String> properties;

	private Account currentAccount;

	private Account previousAccount;

}
