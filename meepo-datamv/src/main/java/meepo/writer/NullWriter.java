package meepo.writer;

import meepo.Config;
import meepo.storage.IStorage;
import meepo.tools.IWorker;

public class NullWriter extends IWorker {

	public NullWriter(IStorage<Object[]> buffer, Config config, int index) {
		super(buffer, config, index);
	}

	@Override
	public void work() {
		buffer.get(config.getWriterStepSize());
	}

	@Override
	protected String buildSQL() {
		return null;
	}
}
