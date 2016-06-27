package meepo.storage;

import meepo.Config;

public abstract class IPlugin<E> {

	public Config config;

	public IPlugin(Config config) {
		this.config = config;
	}

	public abstract E convert(E e);

}
