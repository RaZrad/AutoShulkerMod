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

    // Elytra equip helper
    public static boolean elytraEquipEnabled = false;
    /** клавиша для функциональности: "ё" (`/~) или "x" */
    public static String elytraEquipKey = "ё";  // выбирается через GUI
    /** Задержка между действиями (мс), 10–1000 */
    public static int elytraEquipDelay = 50;
    /** Режим работы: "excellent" или "monoton" */
    public static String elytraEquipMode = "monoton";

    // monoton mode extras (ported from ElytraHelper)
    public static boolean elytraAutoJump = true;            // Авто-прыжок
    public static boolean elytraAutoFly = true;             // Авто-взлёт при падении
    public static boolean elytraAutoFirework = false;       // Автоматически использовать фейерверк
    public static boolean elytraAutoFireworkStart = false;  // Только при взлёте
    public static String elytraFireworkKey = "f";          // кнопка для фейерверка
    public static boolean elytraSwapToOffhand = true;       // использовать фейр в левую руку

}