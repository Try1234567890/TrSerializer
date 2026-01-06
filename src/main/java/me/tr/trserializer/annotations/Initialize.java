package me.tr.trserializer.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Initialize {

    String[] paramNames() default {};

    boolean isSingleton() default false;

    boolean forceNames() default false;

}
