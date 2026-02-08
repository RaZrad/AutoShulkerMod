package auto.shulker.razstr.team;

/**
 * Хранит состояние настроек мода между открытиями GUI.
 */
public class ModConfig {
    public static boolean autoShulkerFarmEnabled = false;
    public static String pricePerUnit = "0";
    public static String balance = "0";
    /** Количество шалкеров для продажи за один цикл. */
    public static String sellCount = "9";
    /** Время ожидания после достижения лимита продажи (секунды). 0 = без задержки. */
    public static String waitTimeAfterLimitSeconds = "60";

    // AutoCraft
    public static boolean autoCraftEnabled = false;
    /** "craft" = команда /craft, "workbench" = верстак по ПКМ */
    public static String autoCraftMode = "craft";

    // MaceDamageChecker
    public static boolean maceDamageCheckerEnabled = false;
    /** "all" = все, "players" = игроки, "mobs" = мобы, "nonliving" = неживые */
    public static String maceDamageCheckerEntityFilter = "all";
    /** Размер текста счетчика (1.0 = стандартный) */
    public static float maceDamageCheckerTextScale = 1.0f;
}
