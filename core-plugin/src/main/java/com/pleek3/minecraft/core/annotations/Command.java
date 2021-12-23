package com.pleek3.minecraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {



    String[] commands();

    String permission() default "";

    String description() default "";

    boolean async() default false;

}
