package auto.shulker.razstr.team.strange.utils;

public class KeyUtil {
    public static String getKey(int key) {
        if (key == -1) return "null";
        if (key == 0) return "LMB";
        if (key == 1) return "RMB";
        if (key == 2) return "MMB";
        if (key == 256) return "Esc";
        if (key == 257) return "Enter";
        if (key == 259) return "BackSp";
        if (key >= 65 && key <= 90) return String.valueOf((char) key);
        if (key >= 290 && key <= 301) return "F" + (key - 289);
        return "K" + key;
    }
}
