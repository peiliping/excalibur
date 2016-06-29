package meepo.storage.plugin;

import meepo.Config;

public abstract class IPlugin<E> {

	protected Config config;

	public IPlugin(Config config) {
		this.config = config;
	}

	public abstract E convert(E e);

}
