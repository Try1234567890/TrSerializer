package me.tr.trserializer.annotations.process.serializer;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Type {

    Class<?> as();

    Class<?>[] arguments();

}
