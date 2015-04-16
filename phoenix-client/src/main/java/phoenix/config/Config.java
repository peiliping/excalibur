package phoenix.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationContext;

public class Config {

    /**
     * 配置文件信息
     */
    @Setter
    @Getter
    private static Context            context;

    /**
     * SpringContext
     */
    @Setter
    @Getter
    private static ApplicationContext applicationContext;
}
