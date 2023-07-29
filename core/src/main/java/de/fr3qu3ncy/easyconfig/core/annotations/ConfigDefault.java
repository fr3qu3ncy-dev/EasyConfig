package de.fr3qu3ncy.easyconfig.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDefault {

    DefaultType value();
    String defaultString() default "";
    int defaultInt() default 0;
    double defaultDouble() default 0D;
    boolean defaultBoolean() default false;
}
