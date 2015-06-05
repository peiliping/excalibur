package phoenix.config;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

public abstract class IVariables implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchConfigFileService.class);

    @Getter
    @Setter
    private String              _variablesGroupName;

    @Getter
    @Setter
    private String              _variablesConfigFilePath;

    @Override
    public void afterPropertiesSet() throws Exception {
        WatchConfigFileService wcfs = new WatchConfigFileService(_variablesGroupName, _variablesConfigFilePath);
        handle(wcfs.getConfig());
        wcfs.regist(this);
    }

    @Subscribe
    public synchronized void handle(Context ct) throws IllegalArgumentException, IllegalAccessException {
        Field[] fs = this.getClass().getFields();
        for (Field f : fs) {
            String fn = f.getName();
            if (ct.get(fn) == null) {
                continue;
            }
            Class<?> c = f.getType();
            Object val = null;
            if (c == Boolean.class) {
                val = ct.getBoolean(fn);
            } else if (c == boolean.class) {
                val = ct.getBoolean(fn);
            } else if (c == Integer.class) {
                val = ct.getInteger(fn);
            } else if (c == int.class) {
                val = ct.getInteger(fn);
            } else if (c == Long.class) {
                val = ct.getLong(fn);
            } else if (c == long.class) {
                val = ct.getLong(fn);
            } else if (c == Double.class) {
                val = ct.getDouble(fn);
            } else if (c == double.class) {
                val = ct.getDouble(fn);
            } else if (c == String.class) {
                val = ct.getString(fn);
            }
            Validate.isTrue(val != null);
            f.set(this, val);
        }
        Map<String, Object> r = Maps.newHashMap();
        for (Field f : this.getClass().getFields()) {
            r.put(f.getName(), f.get(this));
        }
        LOGGER.info(_variablesGroupName + " handle result : " + r.toString());
    }

    public void toLog() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> r = Maps.newHashMap();
        for (Field f : this.getClass().getFields()) {
            r.put(f.getName(), f.get(this));
        }
        LOGGER.info(_variablesGroupName + " handle result : " + r.toString());
    }

}
