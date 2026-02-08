package auto.shulker.razstr.team.strange.module.impl;

import auto.shulker.razstr.team.ModConfig;
import auto.shulker.razstr.team.FastSwapLogic;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;

public class FastSwapModule extends Module {

    public StringSetting item1Name;
    public StringSetting item1Delay;
    public StringSetting item2Name;
    public StringSetting item2Delay;
    public StringSetting item3Name;
    public StringSetting item3Delay;
    public StringSetting mainDelay;

    public FastSwapModule() {
        super("FastSwap", Category.Utilities);
        this.enable = ModConfig.fastSwapEnabled;

        mainDelay = new StringSetting("Задержка (мс)", String.valueOf(ModConfig.fastSwapDelay));
        mainDelay.onChange = () -> {
            try {
                int value = Integer.parseInt(mainDelay.get());
                if (value < 0) value = 0;
                if (value > 1000) value = 1000;
                ModConfig.fastSwapDelay = value;
                mainDelay.set(String.valueOf(value));
            } catch (NumberFormatException e) {
                ModConfig.fastSwapDelay = 50;
                mainDelay.set("50");
            }
        };

        item1Name = new StringSetting("Предмет 1", ModConfig.fastSwapItem1Name);
        item1Name.onChange = () -> ModConfig.fastSwapItem1Name = item1Name.get();

        item1Delay = new StringSetting("Задержка 1 (мс)", String.valueOf(ModConfig.fastSwapItem1Delay));
        item1Delay.onChange = () -> {
            try {
                int value = Integer.parseInt(item1Delay.get());
                if (value < 0) value = 0;
                if (value > 1000) value = 1000;
                ModConfig.fastSwapItem1Delay = value;
                item1Delay.set(String.valueOf(value));
            } catch (NumberFormatException e) {
                ModConfig.fastSwapItem1Delay = 50;
                item1Delay.set("50");
            }
        };

        item2Name = new StringSetting("Предмет 2", ModConfig.fastSwapItem2Name);
        item2Name.onChange = () -> ModConfig.fastSwapItem2Name = item2Name.get();

        item2Delay = new StringSetting("Задержка 2 (мс)", String.valueOf(ModConfig.fastSwapItem2Delay));
        item2Delay.onChange = () -> {
            try {
                int value = Integer.parseInt(item2Delay.get());
                if (value < 0) value = 0;
                if (value > 1000) value = 1000;
                ModConfig.fastSwapItem2Delay = value;
                item2Delay.set(String.valueOf(value));
            } catch (NumberFormatException e) {
                ModConfig.fastSwapItem2Delay = 50;
                item2Delay.set("50");
            }
        };

        item3Name = new StringSetting("Предмет 3", ModConfig.fastSwapItem3Name);
        item3Name.onChange = () -> ModConfig.fastSwapItem3Name = item3Name.get();

        item3Delay = new StringSetting("Задержка 3 (мс)", String.valueOf(ModConfig.fastSwapItem3Delay));
        item3Delay.onChange = () -> {
            try {
                int value = Integer.parseInt(item3Delay.get());
                if (value < 0) value = 0;
                if (value > 1000) value = 1000;
                ModConfig.fastSwapItem3Delay = value;
                item3Delay.set(String.valueOf(value));
            } catch (NumberFormatException e) {
                ModConfig.fastSwapItem3Delay = 50;
                item3Delay.set("50");
            }
        };

        addSettings(
            mainDelay,
            item1Name, item1Delay,
            item2Name, item2Delay,
            item3Name, item3Delay
        );
    }

    @Override
    public void onEnable() {
        ModConfig.fastSwapEnabled = true;
        FastSwapLogic.INSTANCE.register();
    }

    @Override
    public void onDisable() {
        ModConfig.fastSwapEnabled = false;
        FastSwapLogic.INSTANCE.reset();
    }
}
