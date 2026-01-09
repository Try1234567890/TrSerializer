package me.tr.trserializer.processes.process.helper;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class AddonsManager {
    private final Process process;

    public AddonsManager(Process process) {
        this.process = process;
    }

    /**
     * Executes all registered addons for this process sequentially.
     *
     * @param obj   the object to be processed by addons.
     * @param type  the generic type metadata of the object.
     * @param field the field associated with the object, or {@code null} if not applicable.
     * @return an {@link Optional} containing the first addon that returns a non-empty result,
     * paired with its processed output.
     */
    public Optional<Map.Entry<PAddon, ?>> getFirstValidAddon(Object obj, GenericType<?> type, Field field) {
        for (PAddon addon : getProcess().getContext().getAddons()) {
            String addonName = addon.getName();
            try {
                getProcess().getLogger().debug("Executing \"" + addonName + "\"");

                Optional<?> result = addon.process(getProcess(), obj, type, field);

                if (result.isEmpty()) {
                    getProcess().getLogger().debug("Process \"" + addonName + "\" returned empty");
                    continue;
                }

                return Optional.of(Map.entry(addon, getProcess().validate(obj, result.get(), type)));
            } catch (Exception e) {
                getProcess().getLogger().throwable(new RuntimeException("The addon \"" + addonName + "\" thrown an exception.", e));
            }
        }

        return Optional.empty();
    }


    public Process getProcess() {
        return process;
    }
}
