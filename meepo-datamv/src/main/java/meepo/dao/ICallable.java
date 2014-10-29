package meepo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class ICallable<V> {

    public abstract void handleParams(PreparedStatement p) throws Exception;

    public abstract V handleResultSet(ResultSet r) throws Exception;


}
