package meepo.dao;

import java.sql.ResultSet;

public abstract class ParamsICallable<V> extends ICallable<V> {

	@Override
	public V handleResultSet(ResultSet r) throws Exception {
		return null;
	}

}
