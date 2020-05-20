/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.text.WordUtils
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
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
import org.apache.commons.lang3.text.WordUtils;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type type;
    private String title;
    private String[] field_25037;
    private long startTime;
    private boolean justUpdated;
    private final int field_25038;

    public SystemToast(Type arg, Text arg2, @Nullable Text arg3) {
        String[] arrstring;
        if (arg3 == null) {
            arrstring = new String[]{};
        } else {
            String[] arrstring2 = new String[1];
            arrstring = arrstring2;
            arrstring2[0] = arg3.getString();
        }
        this(arg, arg2, arrstring, 160);
    }

    public static SystemToast method_29047(Type arg, Text arg2, Text arg3) {
        String[] strings = WordUtils.wrap((String)arg3.getString(), (int)80).split("\n");
        int i = Math.max(130, Arrays.stream(strings).mapToInt(string -> MinecraftClient.getInstance().textRenderer.getWidth((String)string)).max().orElse(130));
        return new SystemToast(arg, arg2, strings, i + 30);
    }

    private SystemToast(Type arg, Text arg2, String[] strings, int i) {
        this.type = arg;
        this.title = arg2.getString();
        this.field_25037 = strings;
        this.field_25038 = i;
    }

    @Override
    public int method_29049() {
        return this.field_25038;
    }

    @Override
    public Toast.Visibility draw(MatrixStack arg, ToastManager arg2, long l) {
        if (this.justUpdated) {
            this.startTime = l;
            this.justUpdated = false;
        }
        arg2.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        int i = this.method_29049();
        int j = 12;
        if (i == 160 && this.field_25037.length <= 1) {
            arg2.drawTexture(arg, 0, 0, 0, 64, i, this.method_29050());
        } else {
            int k = this.method_29050() + Math.max(0, this.field_25037.length - 1) * 12;
            int m = 28;
            int n = Math.min(4, k - 28);
            this.method_29046(arg, arg2, i, 0, 0, 28);
            for (int o = 28; o < k - n; o += 10) {
                this.method_29046(arg, arg2, i, 16, o, Math.min(16, k - o - n));
            }
            this.method_29046(arg, arg2, i, 32 - n, k - n, n);
        }
        if (this.field_25037 == null) {
            arg2.getGame().textRenderer.draw(arg, this.title, 18.0f, 12.0f, -256);
        } else {
            arg2.getGame().textRenderer.draw(arg, this.title, 18.0f, 7.0f, -256);
            for (int p = 0; p < this.field_25037.length; ++p) {
                String string = this.field_25037[p];
                arg2.getGame().textRenderer.draw(arg, string, 18.0f, (float)(18 + p * 12), -1);
            }
        }
        return l - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void method_29046(MatrixStack arg, ToastManager arg2, int i, int j, int k, int l) {
        int m = j == 0 ? 20 : 5;
        int n = Math.min(60, i - m);
        arg2.drawTexture(arg, 0, k, 0, 64 + j, m, l);
        for (int o = m; o < i - n; o += 64) {
            arg2.drawTexture(arg, o, k, 32, 64 + j, Math.min(64, i - o - n), l);
        }
        arg2.drawTexture(arg, i - n, k, 160 - n, 64 + j, n, l);
    }

    public void setContent(Text arg, @Nullable Text arg2) {
        String[] arrstring;
        this.title = arg.getString();
        if (arg2 == null) {
            arrstring = new String[]{};
        } else {
            String[] arrstring2 = new String[1];
            arrstring = arrstring2;
            arrstring2[0] = arg2.getString();
        }
        this.field_25037 = arrstring;
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
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE;

    }
}

