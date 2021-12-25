package com.pleek3.minecraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParameterInfo {

    String defaultValue() default "";

    boolean wildcard() default false;

    String[] tabCompleteFlags() default "";

}
