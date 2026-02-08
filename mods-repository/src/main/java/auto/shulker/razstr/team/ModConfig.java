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

    // FastSwap
    public static boolean fastSwapEnabled = false;
    /** Задержка между действиями (миллисекунды) */
    public static int fastSwapDelay = 50;
    /** Название предмета 1 (например "wind_charge" или "wind charge") */
    public static String fastSwapItem1Name = "wind_charge";
    /** Задержка зарядки 1 */
    public static int fastSwapItem1Delay = 50;
    /** Название предмета 2 */
    public static String fastSwapItem2Name = "ender_pearl";
    /** Задержка зарядки 2 */
    public static int fastSwapItem2Delay = 50;
    /** Название предмета 3 */
    public static String fastSwapItem3Name = "elytra";
    /** Задержка зарядки 3 */
    public static int fastSwapItem3Delay = 50;}