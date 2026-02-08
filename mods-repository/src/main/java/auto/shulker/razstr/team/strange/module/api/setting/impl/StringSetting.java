package auto.shulker.razstr.team.strange.module.api.setting.impl;

import auto.shulker.razstr.team.strange.module.api.setting.Setting;

import java.util.function.Supplier;

public class StringSetting extends Setting {
    public String input;
    public String description;
    public boolean active;
    public Runnable onChange;

    public StringSetting(String name, String input) {
        this.name = name;
        this.input = input;
    }

    public String get() { return input; }

    public void set(String input) {
        this.input = input;
        triggerAutoSave();
        if (onChange != null) onChange.run();
    }

    public StringSetting hidden(Supplier<Boolean> hidden) {
        this.hidden = hidden;
        return this;
    }
}
