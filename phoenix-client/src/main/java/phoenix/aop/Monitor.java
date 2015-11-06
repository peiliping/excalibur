package phoenix.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {
    /**
     * 指标名称 (不能包含 . _ - / 等分割符号) 此annotation必须作用在spring的bean上才能生效
     * 
     * @return
     */
    String metricName();
}
