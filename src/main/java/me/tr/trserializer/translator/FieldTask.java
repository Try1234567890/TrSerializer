package me.tr.trserializer.translator;

import java.lang.reflect.Field;

public interface TranslatorFieldTask extends TranslatorTask {

    Field getField();

}
