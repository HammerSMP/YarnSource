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
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class SliderWidget
extends AbstractButtonWidget {
    protected double value;

    public SliderWidget(int x, int y, int width, int height, Text arg, double value) {
        super(x, y, width, height, arg);
        this.value = value;
    }

    @Override
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return new TranslatableText("gui.narrate.slider", this.getMessage());
    }

    @Override
    protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        client.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = (this.isHovered() ? 2 : 1) * 20;
        this.drawTexture(matrices, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + k, 4, 20);
        this.drawTexture(matrices, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + k, 4, 20);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl;
        boolean bl2 = bl = keyCode == 263;
        if (bl || keyCode == 262) {
            float f = bl ? -1.0f : 1.0f;
            this.setValue(this.value + (double)(f / (float)(this.width - 8)));
        }
        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double)(this.x + 4)) / (double)(this.width - 8));
    }

    private void setValue(double mouseX) {
        double e = this.value;
        this.value = MathHelper.clamp(mouseX, 0.0, 1.0);
        if (e != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}

