/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type type;
    private String title;
    private String description;
    private long startTime;
    private boolean justUpdated;

    public SystemToast(Type arg, Text arg2, @Nullable Text arg3) {
        this.type = arg;
        this.title = arg2.getString();
        this.description = arg3 == null ? null : arg3.getString();
    }

    @Override
    public Toast.Visibility draw(MatrixStack arg, ToastManager arg2, long l) {
        if (this.justUpdated) {
            this.startTime = l;
            this.justUpdated = false;
        }
        arg2.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        arg2.drawTexture(arg, 0, 0, 0, 64, 160, 32);
        if (this.description == null) {
            arg2.getGame().textRenderer.draw(arg, this.title, 18.0f, 12.0f, -256);
        } else {
            arg2.getGame().textRenderer.draw(arg, this.title, 18.0f, 7.0f, -256);
            arg2.getGame().textRenderer.draw(arg, this.description, 18.0f, 18.0f, -1);
        }
        return l - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public void setContent(Text arg, @Nullable Text arg2) {
        this.title = arg.getString();
        this.description = arg2 == null ? null : arg2.getString();
        this.justUpdated = true;
    }

    public Type getType() {
        return this.type;
    }

    public static void add(ToastManager arg, Type arg2, Text arg3, @Nullable Text arg4) {
        arg.add(new SystemToast(arg2, arg3, arg4));
    }

    public static void show(ToastManager arg, Type arg2, Text arg3, @Nullable Text arg4) {
        SystemToast lv = arg.getToast(SystemToast.class, (Object)arg2);
        if (lv == null) {
            SystemToast.add(arg, arg2, arg3, arg4);
        } else {
            lv.setContent(arg3, arg4);
        }
    }

    public static void addWorldAccessFailureToast(MinecraftClient arg, String string) {
        SystemToast.add(arg.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.access_failure"), new LiteralText(string));
    }

    public static void addWorldDeleteFailureToast(MinecraftClient arg, String string) {
        SystemToast.add(arg.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.delete_failure"), new LiteralText(string));
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
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE;

    }
}

