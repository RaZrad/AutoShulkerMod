package auto.shulker.razstr.team;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GUI в стиле "отсюда пастить" (панель 225×217, тень, шапка, карточки-модули),
 * логика остаётся нашей: AutoShulkerFarm, AutoCraft, настройки по ПКМ.
 */
public class GuiScreen extends Screen {

    // Размеры как у их GuiClient
    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 217;
    private static final int PANEL_RADIUS = 8;
    private static final int CARD_RADIUS = 5;
    private static final int PILL_RADIUS = 3;

    private static final int HEADER_HEIGHT = 48;
    private static final int MODULES_X = 7;
    private static final int MODULES_Y = 48;
    private static final int MODULES_WIDTH = 211;
    private static final int CARD_HEIGHT = 26;
    private static final int GAP = 4;

    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int FIELD_MAX_LEN = 15;

    // Цвета в стиле их темы (тёмный)
    private static final int BG_COLOR = 0xE0181818;
    private static final int SHADOW_COLOR = 0x40000000;
    private static final int CARD_BG = 0xCC1a1a1a;
    private static final int CARD_BORDER = 0x14000000;
    private static final int PILL_ON = 0x5500DD44;
    private static final int PILL_OFF = 0x55DD2222;
    private static final int TEXT_COLOR = 0xCCCCCC;
    private static final int TEXT_DIM = 0x808080;

    private TextFieldWidget priceField;
    private TextFieldWidget balanceField;
    private TextFieldWidget sellCountField;
    private TextFieldWidget waitTimeAfterLimitField;

    private boolean settingsExpanded = false;
    private boolean settingsExpandedCraft = false;
    private boolean craftModeMenuOpen = false;

    private float openProgress = 0f;
    private static final float OPEN_ANIM_DURATION = 0.2f;

    private static final int DOTS_COUNT = 28;
    private static final float DOT_LINE_DISTANCE = 90f;
    private static final int DOT_COLOR = 0x40FFFFFF;
    private static final int LINE_COLOR = 0x20FFFFFF;
    private final List<FloatingDot> dots = new ArrayList<>();
    private static final Random RND = new Random();

    protected GuiScreen() {
        super(Text.literal("AutoShulker"));
    }

    private static class FloatingDot {
        float x, y, vx, vy;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int panelLeft() { return (width - PANEL_WIDTH) / 2; }
    private int panelTop() { return (height - PANEL_HEIGHT) / 2; }
    private int rightPanelLeft() { return panelLeft() + PANEL_WIDTH + 10; }
    private int rightPanelTop() { return panelTop(); }

    private void drawRound(DrawContext ctx, int left, int top, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        int right = left + w;
        int bottom = top + h;
        ctx.fill(left + r, top, right - r, bottom, color);
        ctx.fill(left, top + r, left + r, bottom - r, color);
        ctx.fill(right - r, top + r, right, bottom - r, color);
        for (int dy = 0; dy < r; dy++) {
            for (int dx = 0; dx < r; dx++) {
                int cx = dx - r;
                int cy = dy - r;
                if (cx * cx + cy * cy <= r * r) {
                    ctx.fill(left + dx, top + dy, left + dx + 1, top + dy + 1, color);
                }
                int rx = r - 1 - dx;
                if (rx * rx + cy * cy <= r * r) {
                    ctx.fill(right - 1 - dx, top + dy, right - dx, top + dy + 1, color);
                }
                int by = r - 1 - dy;
                if (cx * cx + by * by <= r * r) {
                    ctx.fill(left + dx, bottom - 1 - dy, left + dx + 1, bottom - dy, color);
                }
                if (rx * rx + by * by <= r * r) {
                    ctx.fill(right - 1 - dx, bottom - 1 - dy, right - dx, bottom - dy, color);
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        openProgress = 0f;
        if (dots.isEmpty()) {
            for (int i = 0; i < DOTS_COUNT; i++) {
                FloatingDot d = new FloatingDot();
                d.x = RND.nextFloat() * width;
                d.y = RND.nextFloat() * height;
                d.vx = (RND.nextFloat() - 0.5f) * 24f;
                d.vy = (RND.nextFloat() - 0.5f) * 24f;
                dots.add(d);
            }
        }
        int rLeft = rightPanelLeft();
        int rTop = rightPanelTop();
        priceField = new TextFieldWidget(textRenderer, rLeft + 10, rTop + 28, RIGHT_PANEL_WIDTH - 20, FIELD_HEIGHT, null, Text.literal(""));
        priceField.setMaxLength(FIELD_MAX_LEN);
        priceField.setText(ModConfig.pricePerUnit);
        priceField.setPlaceholder(Text.literal("0"));
        priceField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*"));
        priceField.setChangedListener(s -> ModConfig.pricePerUnit = s);
        addDrawableChild(priceField);

        balanceField = new TextFieldWidget(textRenderer, rLeft + 10, rTop + 68, RIGHT_PANEL_WIDTH - 20, FIELD_HEIGHT, null, Text.literal(""));
        balanceField.setMaxLength(FIELD_MAX_LEN);
        balanceField.setText(ModConfig.balance);
        balanceField.setPlaceholder(Text.literal("0"));
        balanceField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*"));
        balanceField.setChangedListener(s -> ModConfig.balance = s);
        addDrawableChild(balanceField);

        sellCountField = new TextFieldWidget(textRenderer, rLeft + 10, rTop + 108, RIGHT_PANEL_WIDTH - 20, FIELD_HEIGHT, null, Text.literal(""));
        sellCountField.setMaxLength(FIELD_MAX_LEN);
        sellCountField.setText(ModConfig.sellCount);
        sellCountField.setPlaceholder(Text.literal("9"));
        sellCountField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*"));
        sellCountField.setChangedListener(s -> ModConfig.sellCount = s);
        addDrawableChild(sellCountField);

        waitTimeAfterLimitField = new TextFieldWidget(textRenderer, rLeft + 10, rTop + 148, RIGHT_PANEL_WIDTH - 20, FIELD_HEIGHT, null, Text.literal(""));
        waitTimeAfterLimitField.setMaxLength(FIELD_MAX_LEN);
        waitTimeAfterLimitField.setText(ModConfig.waitTimeAfterLimitSeconds);
        waitTimeAfterLimitField.setPlaceholder(Text.literal("60"));
        waitTimeAfterLimitField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*"));
        waitTimeAfterLimitField.setChangedListener(s -> ModConfig.waitTimeAfterLimitSeconds = s);
        addDrawableChild(waitTimeAfterLimitField);
    }

    @Override
    public void removed() {
        ModConfig.pricePerUnit = priceField != null ? priceField.getText() : ModConfig.pricePerUnit;
        ModConfig.balance = balanceField != null ? balanceField.getText() : ModConfig.balance;
        ModConfig.sellCount = sellCountField != null ? sellCountField.getText() : ModConfig.sellCount;
        ModConfig.waitTimeAfterLimitSeconds = waitTimeAfterLimitField != null ? waitTimeAfterLimitField.getText() : ModConfig.waitTimeAfterLimitSeconds;
        super.removed();
    }

    private void updateDots(float delta) {
        for (FloatingDot d : dots) {
            d.x += d.vx * delta;
            d.y += d.vy * delta;
            if (d.x < 0) { d.x = 0; d.vx = Math.abs(d.vx); }
            if (d.x >= width) { d.x = width - 1; d.vx = -Math.abs(d.vx); }
            if (d.y < 0) { d.y = 0; d.vy = Math.abs(d.vy); }
            if (d.y >= height) { d.y = height - 1; d.vy = -Math.abs(d.vy); }
        }
    }

    private void renderDotsAndLines(DrawContext context, int mouseX, int mouseY) {
        for (FloatingDot d : dots) {
            context.fill((int) d.x, (int) d.y, (int) d.x + 2, (int) d.y + 2, DOT_COLOR);
        }
        if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
            context.fill(mouseX, mouseY, mouseX + 2, mouseY + 2, DOT_COLOR);
            for (FloatingDot a : dots) {
                float dx = (float) mouseX - a.x;
                float dy = (float) mouseY - a.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 0 && dist < DOT_LINE_DISTANCE) {
                    int steps = Math.max(1, (int) dist);
                    for (int s = 0; s <= steps; s++) {
                        float t = s / (float) steps;
                        int x = (int) (a.x + dx * t);
                        int y = (int) (a.y + dy * t);
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            context.fill(x, y, x + 1, y + 1, LINE_COLOR);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < dots.size(); i++) {
            for (int j = i + 1; j < dots.size(); j++) {
                FloatingDot a = dots.get(i);
                FloatingDot b = dots.get(j);
                float dx = b.x - a.x;
                float dy = b.y - a.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 0 && dist < DOT_LINE_DISTANCE) {
                    int steps = Math.max(1, (int) dist);
                    for (int s = 0; s <= steps; s++) {
                        float t = s / (float) steps;
                        int x = (int) (a.x + dx * t);
                        int y = (int) (a.y + dy * t);
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            context.fill(x, y, x + 1, y + 1, LINE_COLOR);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        openProgress = Math.min(1f, openProgress + delta / OPEN_ANIM_DURATION);
        updateDots(delta);
        super.render(ctx, mouseX, mouseY, delta);
        renderDotsAndLines(ctx, mouseX, mouseY);

        float scale = 0.6f + 0.4f * openProgress;
        float alpha = openProgress;
        int cx = width / 2;
        int cy = height / 2;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, cy, 0);
        ctx.getMatrices().scale(scale, scale, 1f);
        ctx.getMatrices().translate(-cx, -cy, 0);

        int px = panelLeft();
        int py = panelTop();
        int bg = (BG_COLOR & 0x00FFFFFF) | ((int) (0xE0 * alpha) << 24);
        int shadow = (SHADOW_COLOR & 0x00FFFFFF) | ((int) (0x40 * alpha) << 24);

        // Тень (как у них)
        drawRound(ctx, px - 2, py - 2, PANEL_WIDTH + 4, PANEL_HEIGHT + 4, PANEL_RADIUS + 2, shadow);

        // Основная панель
        drawRound(ctx, px, py, PANEL_WIDTH, PANEL_HEIGHT, PANEL_RADIUS, bg);

        // Шапка: название + подпись
        ctx.drawText(textRenderer, Text.literal("AutoShulker"), px + 10, py + 14, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24), false);
        ctx.drawText(textRenderer, Text.literal("MOD"), px + 10, py + 24, (TEXT_DIM & 0x00FFFFFF) | ((int)(180*alpha) << 24), false);

        // Карточка 1: AutoShulkerFarm (название слева, таблетка ВКЛ/ВЫКЛ справа, три точки)
        int card1Y = py + MODULES_Y;
        int card1H = CARD_HEIGHT;
        int card1Bg = (CARD_BG & 0x00FFFFFF) | ((int)(255*alpha) << 24);
        drawRound(ctx, px + MODULES_X, card1Y, MODULES_WIDTH, card1H, CARD_RADIUS, card1Bg);
        boolean shulkerOn = ModConfig.autoShulkerFarmEnabled;
        String pill1 = shulkerOn ? "ВКЛ" : "ВЫКЛ";
        int pill1W = textRenderer.getWidth(pill1) + 10;
        int pill1X = px + MODULES_X + MODULES_WIDTH - pill1W - 22;
        drawRound(ctx, pill1X, card1Y + 8, pill1W, 10, PILL_RADIUS, shulkerOn ? PILL_ON : PILL_OFF);
        ctx.drawText(textRenderer, Text.literal("AutoShulkerFarm"), px + MODULES_X + 8, card1Y + 9, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24), false);
        ctx.drawText(textRenderer, Text.literal(pill1), pill1X + 5, card1Y + 10, shulkerOn ? 0xFF226E2C : 0xFF920009, false);
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card1Y + 10, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card1Y + 14, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card1Y + 18, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));

        // Карточка 2: AutoCraft
        int card2Y = py + MODULES_Y + CARD_HEIGHT + GAP;
        drawRound(ctx, px + MODULES_X, card2Y, MODULES_WIDTH, CARD_HEIGHT, CARD_RADIUS, card1Bg);
        boolean craftOn = ModConfig.autoCraftEnabled;
        String pill2 = craftOn ? "ВКЛ" : "ВЫКЛ";
        int pill2W = textRenderer.getWidth(pill2) + 10;
        int pill2X = px + MODULES_X + MODULES_WIDTH - pill2W - 22;
        drawRound(ctx, pill2X, card2Y + 8, pill2W, 10, PILL_RADIUS, craftOn ? PILL_ON : PILL_OFF);
        ctx.drawText(textRenderer, Text.literal("AutoCraft"), px + MODULES_X + 8, card2Y + 9, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24), false);
        ctx.drawText(textRenderer, Text.literal(pill2), pill2X + 5, card2Y + 10, craftOn ? 0xFF226E2C : 0xFF920009, false);
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card2Y + 10, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card2Y + 14, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));
        drawRound(ctx, px + MODULES_X + MODULES_WIDTH - 14, card2Y + 18, 3, 3, 1, (TEXT_COLOR & 0x00FFFFFF) | ((int)(255*alpha) << 24));

        // Правая панель: настройки AutoShulkerFarm
        if (settingsExpanded) {
            int rLeft = rightPanelLeft();
            int rTop = rightPanelTop();
            int panelH = 220;
            drawRound(ctx, rLeft, rTop, RIGHT_PANEL_WIDTH, panelH, CARD_RADIUS, card1Bg);
            ctx.drawText(textRenderer, Text.literal("Цена за штуку"), rLeft + 10, rTop + 12, TEXT_DIM, false);
            ctx.drawText(textRenderer, Text.literal("Баланс"), rLeft + 10, rTop + 52, TEXT_DIM, false);
            ctx.drawText(textRenderer, Text.literal("Кол-во для продажи"), rLeft + 10, rTop + 92, TEXT_DIM, false);
            ctx.drawText(textRenderer, Text.literal("Время после лимита (сек)"), rLeft + 10, rTop + 132, TEXT_DIM, false);
            priceField.setX(rLeft + 10);
            priceField.setY(rTop + 28);
            balanceField.setX(rLeft + 10);
            balanceField.setY(rTop + 68);
            sellCountField.setX(rLeft + 10);
            sellCountField.setY(rTop + 108);
            waitTimeAfterLimitField.setX(rLeft + 10);
            waitTimeAfterLimitField.setY(rTop + 148);
        } else {
            priceField.setX(-10000);
            priceField.setY(-10000);
            balanceField.setX(-10000);
            balanceField.setY(-10000);
            sellCountField.setX(-10000);
            sellCountField.setY(-10000);
            waitTimeAfterLimitField.setX(-10000);
            waitTimeAfterLimitField.setY(-10000);
        }

        // Правая панель: настройки AutoCraft (режим крафта)
        if (settingsExpandedCraft) {
            int rLeft = rightPanelLeft();
            int rTop = rightPanelTop();
            int panelH = 80;
            drawRound(ctx, rLeft, rTop, RIGHT_PANEL_WIDTH, panelH, CARD_RADIUS, card1Bg);
            String modeLabel = "craft".equals(ModConfig.autoCraftMode) ? "/craft" : "Верстак";
            ctx.drawText(textRenderer, Text.literal("Режим крафта: "), rLeft + 10, rTop + 14, TEXT_DIM, false);
            int modeClickLeft = rLeft + 10 + textRenderer.getWidth("Режим крафта: ");
            ctx.drawText(textRenderer, Text.literal(modeLabel), modeClickLeft, rTop + 14, TEXT_COLOR, false);
            if (craftModeMenuOpen) {
                int menuLeft = modeClickLeft;
                int menuTop = rTop + 26;
                int menuW = 100;
                int menuH = 36;
                drawRound(ctx, menuLeft, menuTop, menuW, menuH, PILL_RADIUS, card1Bg);
                ctx.drawText(textRenderer, Text.literal("/craft"), menuLeft + 6, menuTop + 6, "craft".equals(ModConfig.autoCraftMode) ? 0x80FF80 : TEXT_COLOR, false);
                ctx.drawText(textRenderer, Text.literal("Верстак"), menuLeft + 6, menuTop + 20, "workbench".equals(ModConfig.autoCraftMode) ? 0x80FF80 : TEXT_COLOR, false);
            }
        }

        ctx.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int px = panelLeft();
        int py = panelTop();
        int card1Y = py + MODULES_Y;
        int card2Y = py + MODULES_Y + CARD_HEIGHT + GAP;
        int mx = px + MODULES_X;
        int mw = MODULES_WIDTH;
        int ch = CARD_HEIGHT;

        boolean overCard1 = mouseX >= mx && mouseX < mx + mw && mouseY >= card1Y && mouseY < card1Y + ch;
        boolean overCard2 = mouseX >= mx && mouseX < mx + mw && mouseY >= card2Y && mouseY < card2Y + ch;

        if (overCard1) {
            if (button == 0) {
                ModConfig.autoShulkerFarmEnabled = !ModConfig.autoShulkerFarmEnabled;
                AutoShulkerFarmLogic.INSTANCE.resetAllTimers();
                if (ModConfig.autoShulkerFarmEnabled) {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("AutoShulkerFarm включена"));
                }
                return true;
            }
            if (button == 1) {
                settingsExpanded = !settingsExpanded;
                if (settingsExpanded) settingsExpandedCraft = false;
                return true;
            }
        }
        if (overCard2) {
            if (button == 0) {
                ModConfig.autoCraftEnabled = !ModConfig.autoCraftEnabled;
                if (ModConfig.autoCraftEnabled) {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("AutoCraft включен"));
                }
                return true;
            }
            if (button == 1) {
                settingsExpandedCraft = !settingsExpandedCraft;
                if (settingsExpandedCraft) {
                    settingsExpanded = false;
                    craftModeMenuOpen = false;
                }
                return true;
            }
        }

        int rLeft = rightPanelLeft();
        int rTop = rightPanelTop();
        int modeClickLeft = rLeft + 10 + textRenderer.getWidth("Режим крафта: ");
        int modeClickRight = modeClickLeft + Math.max(textRenderer.getWidth("/craft"), textRenderer.getWidth("Верстак"));
        boolean overModeText = settingsExpandedCraft && mouseX >= modeClickLeft && mouseX <= modeClickRight && mouseY >= rTop + 10 && mouseY <= rTop + 24;
        if (overModeText && button == 0) {
            craftModeMenuOpen = !craftModeMenuOpen;
            return true;
        }
        if (craftModeMenuOpen && button == 0) {
            int menuLeft = modeClickLeft;
            int menuTop = rTop + 26;
            int menuW = 100;
            int menuH = 36;
            if (mouseX >= menuLeft && mouseX < menuLeft + menuW && mouseY >= menuTop && mouseY < menuTop + 18) {
                ModConfig.autoCraftMode = "craft";
                craftModeMenuOpen = false;
                return true;
            }
            if (mouseX >= menuLeft && mouseX < menuLeft + menuW && mouseY >= menuTop + 18 && mouseY < menuTop + menuH) {
                ModConfig.autoCraftMode = "workbench";
                craftModeMenuOpen = false;
                return true;
            }
        }

        craftModeMenuOpen = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_0) {
            AutoCat.INSTANCE.onKeyZeroPressed(MinecraftClient.getInstance());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
