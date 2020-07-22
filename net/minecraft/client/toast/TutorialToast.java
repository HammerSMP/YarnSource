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

    public TutorialToast(Type type, Text title, @Nullable Text description, boolean hasProgressBar) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.hasProgressBar = hasProgressBar;
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 96, this.getWidth(), this.getHeight());
        this.type.drawIcon(matrices, manager, 6, 6);
        if (this.description == null) {
            manager.getGame().textRenderer.draw(matrices, this.title, 30.0f, 12.0f, -11534256);
        } else {
            manager.getGame().textRenderer.draw(matrices, this.title, 30.0f, 7.0f, -11534256);
            manager.getGame().textRenderer.draw(matrices, this.description, 30.0f, 18.0f, -16777216);
        }
        if (this.hasProgressBar) {
            int j;
            DrawableHelper.fill(matrices, 3, 28, 157, 29, -1);
            float f = (float)MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(startTime - this.lastTime) / 100.0f);
            if (this.progress >= this.lastProgress) {
                int i = -16755456;
            } else {
                j = -11206656;
            }
            DrawableHelper.fill(matrices, 3, 28, (int)(3.0f + 154.0f * f), 29, j);
            this.lastProgress = f;
            this.lastTime = startTime;
        }
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void setProgress(float progress) {
        this.progress = progress;
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

        private Type(int textureSlotX, int textureSlotY) {
            this.textureSlotX = textureSlotX;
            this.textureSlotY = textureSlotY;
        }

        public void drawIcon(MatrixStack matrices, DrawableHelper helper, int x, int y) {
            RenderSystem.enableBlend();
            helper.drawTexture(matrices, x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}

