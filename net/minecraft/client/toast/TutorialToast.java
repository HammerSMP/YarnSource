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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TutorialToast
implements Toast {
    private final Type type;
    private final Text title;
    private final Text description;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastTime;
    private float lastProgress;
    private float progress;
    private final boolean hasProgressBar;

    public TutorialToast(Type arg, Text arg2, @Nullable Text arg3, boolean bl) {
        this.type = arg;
        this.title = arg2;
        this.description = arg3;
        this.hasProgressBar = bl;
    }

    @Override
    public Toast.Visibility draw(MatrixStack arg, ToastManager arg2, long l) {
        arg2.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        arg2.drawTexture(arg, 0, 0, 0, 96, this.method_29049(), this.method_29050());
        this.type.drawIcon(arg, arg2, 6, 6);
        if (this.description == null) {
            arg2.getGame().textRenderer.draw(arg, this.title, 30.0f, 12.0f, -11534256);
        } else {
            arg2.getGame().textRenderer.draw(arg, this.title, 30.0f, 7.0f, -11534256);
            arg2.getGame().textRenderer.draw(arg, this.description, 30.0f, 18.0f, -16777216);
        }
        if (this.hasProgressBar) {
            int j;
            DrawableHelper.fill(arg, 3, 28, 157, 29, -1);
            float f = (float)MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(l - this.lastTime) / 100.0f);
            if (this.progress >= this.lastProgress) {
                int i = -16755456;
            } else {
                j = -11206656;
            }
            DrawableHelper.fill(arg, 3, 28, (int)(3.0f + 154.0f * f), 29, j);
            this.lastProgress = f;
            this.lastTime = l;
        }
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void setProgress(float f) {
        this.progress = f;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        MOVEMENT_KEYS(0, 0),
        MOUSE(1, 0),
        TREE(2, 0),
        RECIPE_BOOK(0, 1),
        WOODEN_PLANKS(1, 1);

        private final int textureSlotX;
        private final int textureSlotY;

        private Type(int j, int k) {
            this.textureSlotX = j;
            this.textureSlotY = k;
        }

        public void drawIcon(MatrixStack arg, DrawableHelper arg2, int i, int j) {
            RenderSystem.enableBlend();
            arg2.drawTexture(arg, i, j, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}

