/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractButtonWidget
extends DrawableHelper
implements Drawable,
Element {
    public static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
    protected int width;
    protected int height;
    public int x;
    public int y;
    private Text message;
    private boolean wasHovered;
    protected boolean hovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    protected long nextNarration = Long.MAX_VALUE;
    private boolean focused;

    public AbstractButtonWidget(int x, int y, int width, int height, Text message) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
    }

    public int getHeight() {
        return this.height;
    }

    protected int getYImage(boolean hovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (hovered) {
            i = 2;
        }
        return i;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        boolean bl = this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
                if (this.focused) {
                    this.queueNarration(200);
                } else {
                    this.queueNarration(750);
                }
            } else {
                this.nextNarration = Long.MAX_VALUE;
            }
        }
        if (this.visible) {
            this.renderButton(matrices, mouseX, mouseY, delta);
        }
        this.narrate();
        this.wasHovered = this.isHovered();
    }

    protected void narrate() {
        String string;
        if (this.active && this.isHovered() && Util.getMeasuringTimeMs() > this.nextNarration && !(string = this.getNarrationMessage().getString()).isEmpty()) {
            NarratorManager.INSTANCE.narrate(string);
            this.nextNarration = Long.MAX_VALUE;
        }
    }

    protected MutableText getNarrationMessage() {
        return new TranslatableText("gui.narrate.button", this.getMessage());
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient lv = MinecraftClient.getInstance();
        TextRenderer lv2 = lv.textRenderer;
        lv.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        int k = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
        this.renderBg(matrices, lv, mouseX, mouseY);
        int l = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.drawCenteredText(matrices, lv2, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
    }

    public void onClick(double mouseX, double mouseY) {
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(button) && (bl = this.clicked(mouseX, mouseY))) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        }
        return false;
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
    }

    public boolean isHovered() {
        return this.hovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        if (!this.active || !this.visible) {
            return false;
        }
        this.focused = !this.focused;
        this.onFocusedChanged(this.focused);
        return this.focused;
    }

    protected void onFocusedChanged(boolean bl) {
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
    }

    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int value) {
        this.width = value;
    }

    public void setAlpha(float value) {
        this.alpha = value;
    }

    public void setMessage(Text arg) {
        if (!Objects.equals(arg, this.message)) {
            this.queueNarration(250);
        }
        this.message = arg;
    }

    public void queueNarration(int delay) {
        this.nextNarration = Util.getMeasuringTimeMs() + (long)delay;
    }

    public Text getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    protected void setFocused(boolean focused) {
        this.focused = focused;
    }
}

