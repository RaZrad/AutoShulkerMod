package auto.shulker.razstr.team;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Логика AutoShulkerFarm: заполнение хотбара шалкерами, продажа через /ah sell, пауза 1 мин.
 */
public class AutoShulkerFarmLogic {

    public static final AutoShulkerFarmLogic INSTANCE = new AutoShulkerFarmLogic();
    /** Выставляется миксином при нажатии ПКМ (для забора из сундука при 0 шалкеров). */
    public static volatile boolean rightMouseButtonPressed = false;

    private static final int TICKS_BETWEEN_SELLS = 40;         // ~2 сек между командами
    private static final int TICKS_DELAY_BEFORE_SELL = 10;    // 0.5 сек после взятия в руку перед /ah sell
    private static final int TICKS_BETWEEN_HOTBAR_SWAPS = 2; // быстрее перенос в хотбар (было 10)
    private static final int HOTBAR_START = 0;
    private static final int HOTBAR_END = 9;
    private static final int MAIN_INV_START = 9;
    private static final int MAIN_INV_END = 36;

    private int waitTicksLeft = 0;
    private int soldThisCycle = 0;
    private int ticksUntilNextSell = 0;
    private int nextHotbarSlotToFill = 0;
    private int ticksUntilNextSwap = 0;
    /** Ожидание 0.5 сек после выбора слота перед отправкой /ah sell */
    private int ticksBeforeSellCommand = 0;
    /** Флаг: нужно взять шалкеры из сундука по ПКМ (0 шалкеров + ПКМ нажата) */
    private boolean takeFromChestRequested = false;
    /** Задержка между shift-кликами при заборе из сундука */
    private int ticksUntilNextChestTake = 0;

    /** Сброс всех таймеров и состояния (при выключении/включении функции). */
    public void resetAllTimers() {
        waitTicksLeft = 0;
        soldThisCycle = 0;
        ticksUntilNextSell = 0;
        nextHotbarSlotToFill = 0;
        ticksUntilNextSwap = 0;
        ticksBeforeSellCommand = 0;
        takeFromChestRequested = false;
        ticksUntilNextChestTake = 0;
    }

    public void tick(MinecraftClient client) {
        if (!ModConfig.autoShulkerFarmEnabled || client.player == null || client.world == null) return;

        // Таймеры и задержки тикают всегда (даже при свёрнутом окне, Alt+Tab, Escape)
        tickSellCommandDelay(client);

        // ПКМ при 0 шалкеров — запрос на забор из сундука (флаг выставляется из MouseMixin)
        if (client.currentScreen == null && totalShulkerCount(client.player) == 0 && rightMouseButtonPressed) {
            rightMouseButtonPressed = false;
            takeFromChestRequested = true;
        }
        // Открыт сундук и был запрос — забираем шалкеры (shift-click)
        if (client.currentScreen != null && takeFromChestRequested) {
            if (tickTakeShulkersFromChest(client)) {
                takeFromChestRequested = false;
            }
        }
        // Декремент таймеров даже при открытом GUI (чтобы мод не "зависал" при Alt+Tab/Escape)
        tickTimerDecrements(client);
        // На сервере при Escape игра не ставится на паузу — продолжаем продавать. Пропускаем только наш GUI или паузу (одиночка).
        if (client.currentScreen != null && !takeFromChestRequested) {
            if (client.currentScreen instanceof auto.shulker.razstr.team.strange.ui.clickgui.GuiClient) return;
            if (client.isPaused()) return;
        }

        ClientPlayerEntity player = client.player;
        int sellCount = parsePositiveInt(ModConfig.sellCount, 9);
        String price = ModConfig.pricePerUnit.isEmpty() ? "0" : ModConfig.pricePerUnit;

        if (waitTicksLeft > 0) return;

        // Фаза: заполняем хотбар шалкерами
        if (nextHotbarSlotToFill < HOTBAR_END) {
            if (ticksUntilNextSwap > 0) return;
            if (tryFillOneHotbarSlot(player)) {
                nextHotbarSlotToFill++;
                ticksUntilNextSwap = TICKS_BETWEEN_HOTBAR_SWAPS;
            } else {
                // Нет шалкеров в инвентаре — переходим к продаже того что есть, или ждём
                if (countShulkersInHotbar(player) == 0) {
                    int waitSec = parsePositiveInt(ModConfig.waitTimeAfterLimitSeconds, 60);
                    waitTicksLeft = (waitSec <= 0) ? 0 : (waitSec * 20);
                    nextHotbarSlotToFill = 0;
                    return;
                }
                nextHotbarSlotToFill = HOTBAR_END; // переходим к продаже
            }
            return;
        }

        // Фаза: продаём
        if (soldThisCycle >= sellCount) {
            soldThisCycle = 0;
            nextHotbarSlotToFill = 0;
            int waitSec = parsePositiveInt(ModConfig.waitTimeAfterLimitSeconds, 60);
            waitTicksLeft = (waitSec <= 0) ? 0 : (waitSec * 20);
            return;
        }

        if (ticksUntilNextSell > 0) return;

        int slotWithShulker = findShulkerInHotbar(player);
        if (slotWithShulker < 0) {
            nextHotbarSlotToFill = 0;
            return;
        }

        player.getInventory().selectedSlot = slotWithShulker;
        ticksBeforeSellCommand = TICKS_DELAY_BEFORE_SELL;
        soldThisCycle++;
        ticksUntilNextSell = TICKS_BETWEEN_SELLS;
    }

    /** Декремент таймеров — выполняется всегда, даже при открытом GUI. */
    private void tickTimerDecrements(MinecraftClient client) {
        if (waitTicksLeft > 0) waitTicksLeft--;
        if (ticksUntilNextSwap > 0) ticksUntilNextSwap--;
        if (ticksUntilNextSell > 0) ticksUntilNextSell--;
    }

    /** Вызывается каждый тик когда нужно дождаться 0.5 сек и отправить /ah sell. */
    private void tickSellCommandDelay(MinecraftClient client) {
        if (ticksBeforeSellCommand <= 0) return;
        ticksBeforeSellCommand--;
        if (ticksBeforeSellCommand == 0 && client.player != null) {
            String price = ModConfig.pricePerUnit.isEmpty() ? "0" : ModConfig.pricePerUnit;
            client.player.networkHandler.sendCommand("ah sell " + price);
        }
    }

    private boolean tryFillOneHotbarSlot(ClientPlayerEntity player) {
        int invSlot = findShulkerSlotInMainInv(player);
        if (invSlot < 0) return false;
        int hotbarSlot = nextHotbarSlotToFill;
        var handler = player.currentScreenHandler;
        var packet = new ClickSlotC2SPacket(
                handler.syncId,
                handler.getRevision(),
                invSlot,
                hotbarSlot,
                SlotActionType.SWAP,
                ItemStack.EMPTY,
                new Int2ObjectOpenHashMap<>()
        );
        player.networkHandler.sendPacket(packet);
        return true;
    }

    private int findShulkerSlotInMainInv(ClientPlayerEntity player) {
        for (int i = MAIN_INV_START; i < MAIN_INV_END; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isShulkerBox(stack)) return i;
        }
        return -1;
    }

    private int findShulkerInHotbar(ClientPlayerEntity player) {
        for (int i = HOTBAR_START; i < HOTBAR_END; i++) {
            if (isShulkerBox(player.getInventory().getStack(i))) return i;
        }
        return -1;
    }

    private int countShulkersInHotbar(ClientPlayerEntity player) {
        int n = 0;
        for (int i = HOTBAR_START; i < HOTBAR_END; i++) {
            if (isShulkerBox(player.getInventory().getStack(i))) n++;
        }
        return n;
    }

    /** Всего шалкеров в хотбаре + основном инвентаре. */
    private int totalShulkerCount(ClientPlayerEntity player) {
        int n = 0;
        for (int i = HOTBAR_START; i < MAIN_INV_END; i++) {
            if (isShulkerBox(player.getInventory().getStack(i))) n++;
        }
        return n;
    }

    /**
     * Если открыт сундук — за один тик делает один shift-click по слоту с шалкером.
     * Возвращает true когда сундук закрыт или шалкеров в сундуке больше нет (забор завершён).
     */
    private boolean tickTakeShulkersFromChest(MinecraftClient client) {
        if (!(client.currentScreen instanceof GenericContainerScreen)) return true;
        if (!(client.player.currentScreenHandler instanceof GenericContainerScreenHandler handler)) return true;
        if (ticksUntilNextChestTake > 0) {
            ticksUntilNextChestTake--;
            return false;
        }
        int containerSlots = handler.getRows() * 9;
        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (!stack.isEmpty() && isShulkerBox(stack)) {
                var packet = new ClickSlotC2SPacket(
                        handler.syncId,
                        handler.getRevision(),
                        i,
                        0,
                        SlotActionType.QUICK_MOVE,
                        ItemStack.EMPTY,
                        new Int2ObjectOpenHashMap<>()
                );
                client.player.networkHandler.sendPacket(packet);
                ticksUntilNextChestTake = 2;
                return false;
            }
        }
        return true; // в сундуке шалкеров не осталось
    }

    public static boolean isShulkerBox(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        Block block = blockItem.getBlock();
        return block instanceof ShulkerBoxBlock;
    }

    private static int parsePositiveInt(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try {
            int v = Integer.parseInt(s);
            return v > 0 ? v : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
