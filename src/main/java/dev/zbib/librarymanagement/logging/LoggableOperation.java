package dev.zbib.librarymanagement.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggableOperation {
    String operationType() default "GENERAL";

    String description() default "";

    LogLevel level() default LogLevel.INFO;

    boolean includeParameters() default true;

    boolean includeResult() default false;

    boolean maskSensitiveData() default true;
}