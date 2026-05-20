package me.tr.trserializer.annotations.translator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateFormat {

    String format() default "dd-MM-yyyy";

    boolean timestamp() default false;

}
