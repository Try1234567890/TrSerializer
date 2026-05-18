package me.tr.trserializer.translator;

import java.lang.reflect.Field;

/**
 * This interface define the basics of a
 * field task for a translator.
 * <p>
 * A field task is an extension of {@link TranslatorTask} that contains
 * a reference to the current {@link Field}.
 * In default implemented translator is created when an object
 * is serializing as a map or an object is deserializing from a map.
 * <p>
 * All translator field task should implement it.
 */
public interface FieldTask extends TranslatorTask {

    Field getField();

}
