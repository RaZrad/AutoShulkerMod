package auto.shulker.razstr.team;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Обрабатывает команды вида ".bind <item> <key>"
 * Пример: .bind elytra x
 * 
 * Используется для переопределения клавиш быстрого свопа предметов
 */
public class ChatCommand {

    public static void handleCommand(String command) {
        if (!command.startsWith(".bind ")) {
            return;
        }

        String args = command.substring(6).trim();
        String[] parts = args.split(" ");
        
        if (parts.length < 2) {
            printError("Использование: .bind <предмет> <клавиша>");
            printError("Пример: .bind elytra x");
            return;
        }

        String itemName = parts[0].toLowerCase();
        String keyName = parts[1].toLowerCase();

        int keyCode = getKeyCodeFromName(keyName);
        if (keyCode < 0) {
            printError("Неизвестная клавиша: " + keyName);
            return;
        }

        switch (itemName) {
            case "elytra":
            case "elitra":
                ModConfig.fastSwapItem3KeyCode = keyCode;
                printSuccess("✓ Бинд для 'elytra' установлен на: " + keyName.toUpperCase());
                break;
            case "wind_charge":
            case "wind":
                ModConfig.fastSwapItem1KeyCode = keyCode;
                printSuccess("✓ Бинд для 'wind_charge' установлен на: " + keyName.toUpperCase());
                break;
            case "ender_pearl":
            case "pearl":
                ModConfig.fastSwapItem2KeyCode = keyCode;
                printSuccess("✓ Бинд для 'ender_pearl' установлен на: " + keyName.toUpperCase());
                break;
            default:
                printError("Неизвестный предмет: " + itemName);
        }
    }

    private static int getKeyCodeFromName(String keyName) {
        // Одиночные буквы
        if (keyName.length() == 1) {
            char c = keyName.charAt(0);
            if (c >= 'a' && c <= 'z') {
                return GLFW.GLFW_KEY_A + (c - 'a');
            }
            if (c >= '0' && c <= '9') {
                return GLFW.GLFW_KEY_0 + (c - '0');
            }
        }

        // Функциональные клавиши
        switch (keyName) {
            case "f1": return GLFW.GLFW_KEY_F1;
            case "f2": return GLFW.GLFW_KEY_F2;
            case "f3": return GLFW.GLFW_KEY_F3;
            case "f4": return GLFW.GLFW_KEY_F4;
            case "f5": return GLFW.GLFW_KEY_F5;
            case "f6": return GLFW.GLFW_KEY_F6;
            case "f7": return GLFW.GLFW_KEY_F7;
            case "f8": return GLFW.GLFW_KEY_F8;
            case "f9": return GLFW.GLFW_KEY_F9;
            case "f10": return GLFW.GLFW_KEY_F10;
            case "f11": return GLFW.GLFW_KEY_F11;
            case "f12": return GLFW.GLFW_KEY_F12;
            case "space": case "spacebar": return GLFW.GLFW_KEY_SPACE;
            case "enter": case "return": return GLFW.GLFW_KEY_ENTER;
            case "shift": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "ctrl": case "control": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "alt": return GLFW.GLFW_KEY_LEFT_ALT;
            case "tab": return GLFW.GLFW_KEY_TAB;
            case "escape": case "esc": return GLFW.GLFW_KEY_ESCAPE;
            case "minus": case "-": return GLFW.GLFW_KEY_MINUS;
            case "equal": case "=": return GLFW.GLFW_KEY_EQUAL;
            case "backspace": case "delete": return GLFW.GLFW_KEY_BACKSPACE;
            default: return -1;
        }
    }

    private static void printSuccess(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.of("§a" + message), false);
        }
    }

    private static void printError(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.of("§c" + message), false);
        }
    }
}
