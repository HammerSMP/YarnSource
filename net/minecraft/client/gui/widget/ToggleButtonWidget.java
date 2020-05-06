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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ToggleButtonWidget
extends AbstractButtonWidget {
    protected Identifier texture;
    protected boolean toggled;
    protected int u;
    protected int v;
    protected int pressedUOffset;
    protected int hoverVOffset;

    public ToggleButtonWidget(int i, int j, int k, int l, boolean bl) {
        super(i, j, k, l, LiteralText.EMPTY);
        this.toggled = bl;
    }

    public void setTextureUV(int i, int j, int k, int l, Identifier arg) {
        this.u = i;
        this.v = j;
        this.pressedUOffset = k;
        this.hoverVOffset = l;
        this.texture = arg;
    }

    public void setToggled(boolean bl) {
        this.toggled = bl;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setPos(int i, int j) {
        this.x = i;
        this.y = j;
    }

    @Override
    public void renderButton(MatrixStack arg, int i, int j, float f) {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.getTextureManager().bindTexture(this.texture);
        RenderSystem.disableDepthTest();
        int k = this.u;
        int l = this.v;
        if (this.toggled) {
            k += this.pressedUOffset;
        }
        if (this.isHovered()) {
            l += this.hoverVOffset;
        }
        this.drawTexture(arg, this.x, this.y, k, l, this.width, this.height);
        RenderSystem.enableDepthTest();
    }
}

