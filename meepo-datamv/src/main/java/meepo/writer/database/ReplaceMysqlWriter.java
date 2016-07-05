package meepo.writer.database;

import meepo.Config;
import meepo.storage.IStorage;

public class ReplaceMysqlWriter extends DefaultMysqlWriter {

    public ReplaceMysqlWriter(IStorage<Object[]> buffer, Config config, int index) {
        super(buffer, config, index);
    }

    @Override protected String buildSQL() {
        String v = "?";
        for (int i = 1; i < config.getTargetColumnsArray().size(); i++) {
            v = v + ",?";
        }
        return "REPLACE INTO " + config.getTargetTableName() + " (" + config.getTargetColumnsNames() + ") VALUES ( " + v + ")";
    }
}
