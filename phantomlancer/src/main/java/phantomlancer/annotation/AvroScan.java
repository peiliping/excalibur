package phantomlancer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvroScan {

    boolean forceStringType() default false;

    boolean camelConvert() default true;

    boolean skipStaticField() default true;

    boolean skipTransientField() default true;

    boolean nameSpaceByPackageName() default true;

    String nameSpaceByManul() default "PhantomLancer.Auto";

    boolean allCanBeNull() default true;

}
