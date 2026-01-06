package me.tr.trserializer.annotations.naming;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
public @interface Naming {

    NamingStrategy strategy();

    NamingStrategy from() default NamingStrategy.NOTHING;

}
