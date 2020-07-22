/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type type;
    private StringRenderable title;
    private List<StringRenderable> lines;
    private long startTime;
    private boolean justUpdated;
    private final int width;

    public SystemToast(Type type, Text title, @Nullable Text description) {
        this(type, title, (List<StringRenderable>)SystemToast.getTextAsList(description), 160);
    }

    public static SystemToast create(MinecraftClient client, Type type, Text title, Text description) {
        TextRenderer lv = client.textRenderer;
        List<StringRenderable> list = lv.getTextHandler().wrapLines(description, 200, Style.EMPTY);
        int i = Math.max(200, list.stream().mapToInt(lv::getWidth).max().orElse(200));
        return new SystemToast(type, title, list, i + 30);
    }

    private SystemToast(Type type, Text title, List<StringRenderable> lines, int width) {
        this.type = type;
        this.title = title;
        this.lines = lines;
        this.width = width;
    }

    private static ImmutableList<StringRenderable> getTextAsList(@Nullable Text text) {
        return text == null ? ImmutableList.of() : ImmutableList.of((Object)text);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        int i = this.getWidth();
        int j = 12;
        if (i == 160 && this.lines.size() <= 1) {
            manager.drawTexture(matrices, 0, 0, 0, 64, i, this.getHeight());
        } else {
            int k = this.getHeight() + Math.max(0, this.lines.size() - 1) * 12;
            int m = 28;
            int n = Math.min(4, k - 28);
            this.drawPart(matrices, manager, i, 0, 0, 28);
            for (int o = 28; o < k - n; o += 10) {
                this.drawPart(matrices, manager, i, 16, o, Math.min(16, k - o - n));
            }
            this.drawPart(matrices, manager, i, 32 - n, k - n, n);
        }
        if (this.lines == null) {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 12.0f, -256);
        } else {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 7.0f, -256);
            for (int p = 0; p < this.lines.size(); ++p) {
                manager.getGame().textRenderer.draw(matrices, this.lines.get(p), 18.0f, (float)(18 + p * 12), -1);
            }
        }
        return startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void drawPart(MatrixStack matrices, ToastManager manager, int width, int textureV, int y, int height) {
        int m = textureV == 0 ? 20 : 5;
        int n = Math.min(60, width - m);
        manager.drawTexture(matrices, 0, y, 0, 64 + textureV, m, height);
        for (int o = m; o < width - n; o += 64) {
            manager.drawTexture(matrices, o, y, 32, 64 + textureV, Math.min(64, width - o - n), height);
        }
        manager.drawTexture(matrices, width - n, y, 160 - n, 64 + textureV, n, height);
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title;
        this.lines = SystemToast.getTextAsList(description);
        this.justUpdated = true;
    }

    public Type getType() {
        return this.type;
    }

    public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
        manager.add(new SystemToast(type, title, description));
    }

    public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
        SystemToast lv = manager.getToast(SystemToast.class, (Object)type);
        if (lv == null) {
            SystemToast.add(manager, type, title, description);
        } else {
            lv.setContent(title, description);
        }
    }

    public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.access_failure"), new LiteralText(worldName));
    }

    public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.delete_failure"), new LiteralText(worldName));
    }

    public static void addPackCopyFailure(MinecraftClient client, String directory) {
        SystemToast.add(client.getToastManager(), Type.PACK_COPY_FAILURE, new TranslatableText("pack.copyFailure"), new LiteralText(directory));
    }

    @Override
    public /* synthetic */ Object getType() {
        return this.getType();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE;

    }
}

