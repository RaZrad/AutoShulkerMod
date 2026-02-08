package auto.shulker.razstr.team.strange.module;

public class ThemeManager {
    private static Theme currentTheme = Theme.WHITE;
    private static Theme previousTheme = Theme.WHITE;
    private static float animation = 1.0f;
    private static final float ANIMATION_SPEED = 0.08f;

    public static void setTheme(Theme theme) {
        if (theme == currentTheme) return;
        previousTheme = currentTheme;
        currentTheme = theme;
        animation = 0.0f;
    }

    public static void update() {
        if (animation < 1.0f) {
            animation += ANIMATION_SPEED;
            if (animation > 1.0f) animation = 1.0f;
        }
    }

    public static Theme getTheme() { return currentTheme; }
    public static Theme getPreTheme() { return previousTheme; }
    public static float getProgress() { return animation; }
}
