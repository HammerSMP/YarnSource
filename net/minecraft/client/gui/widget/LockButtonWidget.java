/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class LockButtonWidget
extends ButtonWidget {
    private boolean locked;

    public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
        super(x, y, 20, 20, new TranslatableText("narrator.button.difficulty_lock"), action);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return super.getNarrationMessage().append(". ").append(this.isLocked() ? new TranslatableText("narrator.button.difficulty_lock.locked") : new TranslatableText("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        IconLocation lv3;
        MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (!this.active) {
            IconLocation lv = this.locked ? IconLocation.LOCKED_DISABLED : IconLocation.UNLOCKED_DISABLED;
        } else if (this.isHovered()) {
            IconLocation lv2 = this.locked ? IconLocation.LOCKED_HOVER : IconLocation.UNLOCKED_HOVER;
        } else {
            lv3 = this.locked ? IconLocation.LOCKED : IconLocation.UNLOCKED;
        }
        this.drawTexture(matrices, this.x, this.y, lv3.getU(), lv3.getV(), this.width, this.height);
    }

    @Environment(value=EnvType.CLIENT)
    static enum IconLocation {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int u;
        private final int v;

        private IconLocation(int j, int k) {
            this.u = j;
            this.v = k;
        }

        public int getU() {
            return this.u;
        }

        public int getV() {
            return this.v;
        }
    }
}

