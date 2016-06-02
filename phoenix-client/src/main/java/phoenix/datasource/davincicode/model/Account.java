package phoenix.datasource.davincicode.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {

	private String username;

	private String password;

	private long startTime;

	private long expireTime;

}
