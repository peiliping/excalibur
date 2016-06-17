package phoenix.datasource.davincicode.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Param {

	private String instanceIp;

	private String dbName;

	private String privilege;

	private long version;

}
