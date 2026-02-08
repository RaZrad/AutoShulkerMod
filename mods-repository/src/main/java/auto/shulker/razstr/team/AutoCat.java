package auto.shulker.razstr.team;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * AutoCat: по нажатию 0 показывается фото настоящего кота из интернета (The Cat API).
 * 0.5 сек появление, 3 сек на экране, 0.5 сек исчезновение. Кд 1 сек после исчезновения.
 */
public class AutoCat {

    public static final AutoCat INSTANCE = new AutoCat();

    /** Cataas.com — отдаёт случайное фото кота напрямую (без ключа и JSON). */
    private static final String CAT_IMAGE_URL = "https://cataas.com/cat?width=256&height=256";
    private static final Identifier TEXTURE_ID = Identifier.of("autoshulkerspooky", "cat_dynamic");

    private static final int FADE_IN_TICKS = 10;
    private static final int VISIBLE_TICKS = 60;
    private static final int FADE_OUT_TICKS = 10;
    private static final int COOLDOWN_TICKS = 20;

    private static final int CAT_SIZE = 256;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private int phase = 0;
    private int tickInPhase = 0;
    private int cooldownTicksLeft = 0;
    private boolean textureReady = false;
    private boolean loading = false;
    private int texWidth = 256;
    private int texHeight = 256;

    public void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context, tickCounter.getTickDelta(true)));
    }

    public void tick(MinecraftClient client) {
        if (phase == 0) {
            if (cooldownTicksLeft > 0) cooldownTicksLeft--;
            return;
        }

        tickInPhase++;

        if (phase == 1) {
            if (tickInPhase >= FADE_IN_TICKS) {
                phase = 2;
                tickInPhase = 0;
            }
        } else if (phase == 2) {
            if (tickInPhase >= VISIBLE_TICKS) {
                phase = 3;
                tickInPhase = 0;
            }
        } else if (phase == 3) {
            if (tickInPhase >= FADE_OUT_TICKS) {
                phase = 0;
                tickInPhase = 0;
                cleanupTexture(client);
                textureReady = false;
                cooldownTicksLeft = COOLDOWN_TICKS;
            }
        }
    }

    public void onKeyZeroPressed(MinecraftClient client) {
        if (phase != 0 || cooldownTicksLeft > 0 || loading) return;

        loading = true;
        phase = 1;
        tickInPhase = 0;
        textureReady = false;

        Thread fetchThread = new Thread(() -> {
            try {
                byte[] imageBytes = fetchCatImageBytes();
                if (imageBytes == null) {
                    client.execute(() -> client.inGameHud.getChatHud().addMessage(net.minecraft.text.Text.literal("§c[AutoCat] Не удалось загрузить картинку (проверь интернет)")));
                    loading = false;
                    phase = 0;
                    return;
                }
                byte[] finalBytes = imageBytes;
                client.execute(() -> loadTexture(client, finalBytes));
            } catch (Exception e) {
                client.execute(() -> client.inGameHud.getChatHud().addMessage(net.minecraft.text.Text.literal("§c[AutoCat] Ошибка: " + e.getMessage())));
                loading = false;
                phase = 0;
            }
        });
        fetchThread.setDaemon(true);
        fetchThread.setName("AutoCat-Fetch");
        fetchThread.start();
    }

    private byte[] fetchCatImageBytes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CAT_IMAGE_URL))
                    .header("User-Agent", "AutoShulkerMod/1.0")
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return response.statusCode() == 200 ? response.body() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadTexture(MinecraftClient client, byte[] imageBytes) {
        try {
            var image = net.minecraft.client.texture.NativeImage.read(new ByteArrayInputStream(imageBytes));
            texWidth = image.getWidth();
            texHeight = image.getHeight();
            var texture = new NativeImageBackedTexture(image);
            var manager = client.getTextureManager();
            if (manager.getTexture(TEXTURE_ID) != null) {
                manager.destroyTexture(TEXTURE_ID);
            }
            manager.registerTexture(TEXTURE_ID, texture);
            texture.upload();
            textureReady = true;
        } catch (IOException e) {
            client.execute(() -> client.inGameHud.getChatHud().addMessage(net.minecraft.text.Text.literal("§c[AutoCat] Ошибка загрузки текстуры")));
            phase = 0;
        } finally {
            loading = false;
        }
    }

    private void cleanupTexture(MinecraftClient client) {
        client.execute(() -> {
            var manager = client.getTextureManager();
            if (manager.getTexture(TEXTURE_ID) != null) {
                manager.destroyTexture(TEXTURE_ID);
            }
        });
    }

    private void render(DrawContext context, float tickDelta) {
        if (phase == 0 || (!textureReady && phase != 1)) return;

        if (!textureReady) {
            int w = context.getScaledWindowWidth();
            int h = context.getScaledWindowHeight();
            var tr = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(tr, net.minecraft.text.Text.literal("Загрузка котика..."), w / 2, h / 2 - 4, 0xFFFFFF);
            return;
        }

        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();
        int x = (w - CAT_SIZE) / 2;
        int y = (h - CAT_SIZE) / 2;

        float alpha;
        if (phase == 1) {
            alpha = Math.min(1f, (tickInPhase + tickDelta) / FADE_IN_TICKS);
        } else if (phase == 2) {
            alpha = 1f;
        } else {
            alpha = Math.max(0f, 1f - (tickInPhase + tickDelta) / FADE_OUT_TICKS);
        }

        int color = ((int) (alpha * 255) << 24) | 0x00FFFFFF;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_ID, x, y, 0f, 0f, CAT_SIZE, CAT_SIZE, texWidth, texHeight, color);
    }
}
