/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ToastManager
extends DrawableHelper {
    private final MinecraftClient client;
    private final Entry<?>[] visibleEntries = new Entry[5];
    private final Deque<Toast> toastQueue = Queues.newArrayDeque();

    public ToastManager(MinecraftClient arg) {
        this.client = arg;
    }

    public void draw(MatrixStack arg) {
        if (this.client.options.hudHidden) {
            return;
        }
        for (int i = 0; i < this.visibleEntries.length; ++i) {
            Entry<?> lv = this.visibleEntries[i];
            if (lv != null && lv.draw(this.client.getWindow().getScaledWidth(), i, arg)) {
                this.visibleEntries[i] = null;
            }
            if (this.visibleEntries[i] != null || this.toastQueue.isEmpty()) continue;
            this.visibleEntries[i] = new Entry(this, this.toastQueue.removeFirst());
        }
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> class_, Object object) {
        for (Entry<?> lv : this.visibleEntries) {
            if (lv == null || !class_.isAssignableFrom(lv.getInstance().getClass()) || !lv.getInstance().getType().equals(object)) continue;
            return (T)lv.getInstance();
        }
        for (Toast lv2 : this.toastQueue) {
            if (!class_.isAssignableFrom(lv2.getClass()) || !lv2.getType().equals(object)) continue;
            return (T)lv2;
        }
        return null;
    }

    public void clear() {
        Arrays.fill(this.visibleEntries, null);
        this.toastQueue.clear();
    }

    public void add(Toast arg) {
        this.toastQueue.add(arg);
    }

    public MinecraftClient getGame() {
        return this.client;
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry<T extends Toast> {
        private final T instance;
        private long field_2243 = -1L;
        private long field_2242 = -1L;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;
        final /* synthetic */ ToastManager field_2245;

        private Entry(T arg2) {
            this.field_2245 = arg;
            this.instance = arg2;
        }

        public T getInstance() {
            return this.instance;
        }

        private float getDisappearProgress(long l) {
            float f = MathHelper.clamp((float)(l - this.field_2243) / 600.0f, 0.0f, 1.0f);
            f *= f;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - f;
            }
            return f;
        }

        public boolean draw(int i, int j, MatrixStack arg) {
            long l = Util.getMeasuringTimeMs();
            if (this.field_2243 == -1L) {
                this.field_2243 = l;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && l - this.field_2243 <= 600L) {
                this.field_2242 = l;
            }
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)i - (float)this.instance.getWidth() * this.getDisappearProgress(l), j * this.instance.getHeight(), 800 + j);
            Toast.Visibility lv = this.instance.draw(arg, this.field_2245, l - this.field_2242);
            RenderSystem.popMatrix();
            if (lv != this.visibility) {
                this.field_2243 = l - (long)((int)((1.0f - this.getDisappearProgress(l)) * 600.0f));
                this.visibility = lv;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && l - this.field_2243 > 600L;
        }
    }
}

