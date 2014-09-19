package jakiro.sequence.dao;

import jakiro.sequence.SequenceException;
import jakiro.sequence.seq.SequenceRange;

import javax.sql.DataSource;

public interface SequenceDao {
	
	SequenceRange nextRange(String name,int index,int total) throws SequenceException;

	void setStep(int step);
	
	void setDataSource(DataSource dataSource);
}
