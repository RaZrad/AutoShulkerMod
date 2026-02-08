package auto.shulker.razstr.team.strange.utils;

public class ScrollUtil {
    private float target, scroll, max;
    private float speed = 8f;

    public void update() {
        scroll = lerp(scroll, target, speed / 100f);
    }

    public void handleScroll(double scrollY) {
        float wheel = (float) scrollY * (speed * 10f);
        target = Math.min(Math.max(target + (wheel / 2f), max), 0);
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    public float getScroll() { return scroll; }
    public void setMax(float contentHeight, float viewHeight) {
        this.max = -Math.max(0, contentHeight - viewHeight);
    }
}
