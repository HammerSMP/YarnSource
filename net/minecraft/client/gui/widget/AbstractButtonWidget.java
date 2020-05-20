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

    public AbstractButtonWidget(int i, int j, int k, int l, Text arg) {
        this.x = i;
        this.y = j;
        this.width = k;
        this.height = l;
        this.message = arg;
    }

    public int getHeight() {
        return this.height;
    }

    protected int getYImage(boolean bl) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (bl) {
            i = 2;
        }
        return i;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (!this.visible) {
            return;
        }
        boolean bl = this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
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
            this.renderButton(arg, i, j, f);
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

    public void renderButton(MatrixStack arg, int i, int j, float f) {
        MinecraftClient lv = MinecraftClient.getInstance();
        TextRenderer lv2 = lv.textRenderer;
        lv.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        int k = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(arg, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
        this.drawTexture(arg, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
        this.renderBg(arg, lv, i, j);
        int l = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.drawCenteredText(arg, lv2, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    protected void renderBg(MatrixStack arg, MinecraftClient arg2, int i, int j) {
    }

    public void onClick(double d, double e) {
    }

    public void onRelease(double d, double e) {
    }

    protected void onDrag(double d, double e, double f, double g) {
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean bl;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(i) && (bl = this.clicked(d, e))) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick(d, e);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (this.isValidClickButton(i)) {
            this.onRelease(d, e);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int i) {
        return i == 0;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.isValidClickButton(i)) {
            this.onDrag(d, e, f, g);
            return true;
        }
        return false;
    }

    protected boolean clicked(double d, double e) {
        return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
    }

    public boolean isHovered() {
        return this.hovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean bl) {
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
    public boolean isMouseOver(double d, double e) {
        return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
    }

    public void renderToolTip(MatrixStack arg, int i, int j) {
    }

    public void playDownSound(SoundManager arg) {
        arg.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public void setAlpha(float f) {
        this.alpha = f;
    }

    public void setMessage(Text arg) {
        if (!Objects.equals(arg, this.message)) {
            this.queueNarration(250);
        }
        this.message = arg;
    }

    public void queueNarration(int i) {
        this.nextNarration = Util.getMeasuringTimeMs() + (long)i;
    }

    public Text getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    protected void setFocused(boolean bl) {
        this.focused = bl;
    }
}

