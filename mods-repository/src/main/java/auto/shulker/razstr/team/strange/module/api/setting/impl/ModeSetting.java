package auto.shulker.razstr.team.strange.module.api.setting.impl;

import auto.shulker.razstr.team.strange.module.api.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting {
    public final List<String> modes;
    public String currentMode;
    public String description;
    public int index;
    public boolean opened;
    public Runnable onChange;

    public ModeSetting(String name, String currentMode, String... options) {
        this.name = name;
        this.modes = Arrays.asList(options);
        this.index = modes.indexOf(currentMode);
        if (index < 0) index = 0;
        this.currentMode = modes.get(index);
    }

    public String get() { return currentMode; }

    public boolean is(String mode) { return currentMode.equalsIgnoreCase(mode); }

    public void set(String mode) {
        if (modes.contains(mode)) {
            currentMode = mode;
            index = modes.indexOf(mode);
            triggerAutoSave();
            if (onChange != null) onChange.run();
        }
    }

    public ModeSetting hidden(Supplier<Boolean> hidden) {
        this.hidden = hidden;
        return this;
    }
}
