package phoenix.aop;

import java.lang.reflect.Method;

public interface CheckResult {

    /**
     * 检查返回结果是否有错误
     * 
     * @param m
     * @param result
     * @return true:有错误 false:无错误
     */
    boolean checkError(Method m, Object result);

}
