package meepo.dao;

import java.sql.PreparedStatement;

public abstract class ResultSetICallable<V> extends ICallable<V> {

    @Override public void handleParams(PreparedStatement p) throws Exception {
    }

}
