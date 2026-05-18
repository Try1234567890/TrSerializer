package me.tr.trserializer.instancer;

import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.translator.Translator;

import java.util.Map;

/**
 * An instancer is a system that provide a new instance of a provided class.
 * <p>
 * This implementation does not have only the class and the optional params
 * provided in the {@link #instance(Class, Map)} method, but it can access
 * to the information provided from the {@link Translator} too.
 * Like, for example, the {@link Translator#getContext()} or {@link Translator#getOptions()}
 */
public class TranslatorInstancer implements Instancer {
    private final Translator translator;
    public final InstancerOptions options;


    public TranslatorInstancer(Translator translator, InstancerOptions options) {
        this.translator = translator;
        this.options = options;
    }

    public TranslatorInstancer(Translator translator) {
        this(translator, new InstancerOptions());
    }

    public Translator getTranslator() {
        return translator;
    }

    @Override
    public <T> T instance(Class<T> cls, Map<String, Object> params) throws InstancerError {
        // TODO: Add the translator options support

        return StaticInstancer.newInstance(cls, params);
    }

    @Override
    public InstancerOptions getOptions() {
        return options;
    }
}
