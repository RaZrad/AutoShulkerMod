package auto.shulker.razstr.team.strange.module.impl;

import auto.shulker.razstr.team.AutoShulkerFarmLogic;
import auto.shulker.razstr.team.ModConfig;
import auto.shulker.razstr.team.strange.module.api.Category;
import auto.shulker.razstr.team.strange.module.api.Module;
import auto.shulker.razstr.team.strange.module.api.setting.impl.StringSetting;

public class AutoShulkerFarmModule extends Module {

    public StringSetting pricePerUnit;
    public StringSetting balance;
    public StringSetting sellCount;
    public StringSetting waitTimeAfterLimit;

    public AutoShulkerFarmModule() {
        super("AutoShulkerFarm", Category.Utilities);
        this.enable = ModConfig.autoShulkerFarmEnabled;
        addSettings(
            pricePerUnit = new StringSetting("Цена за штуку", ModConfig.pricePerUnit),
            balance = new StringSetting("Баланс", ModConfig.balance),
            sellCount = new StringSetting("Кол-во для продажи", ModConfig.sellCount),
            waitTimeAfterLimit = new StringSetting("Время после лимита (сек)", ModConfig.waitTimeAfterLimitSeconds)
        );
    }

    @Override
    public void onEnable() {
        ModConfig.autoShulkerFarmEnabled = true;
        AutoShulkerFarmLogic.INSTANCE.resetAllTimers();
    }

    @Override
    public void onDisable() {
        ModConfig.autoShulkerFarmEnabled = false;
    }
}
