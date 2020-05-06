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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TexturedButtonWidget
extends ButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset;
    private final int textureWidth;
    private final int textureHeight;

    public TexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier arg, ButtonWidget.PressAction arg2) {
        this(i, j, k, l, m, n, o, arg, 256, 256, arg2);
    }

    public TexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier arg, int p, int q, ButtonWidget.PressAction arg2) {
        this(i, j, k, l, m, n, o, arg, p, q, arg2, LiteralText.EMPTY);
    }

    public TexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier arg, int p, int q, ButtonWidget.PressAction arg2, Text arg3) {
        super(i, j, k, l, arg3, arg2);
        this.textureWidth = p;
        this.textureHeight = q;
        this.u = m;
        this.v = n;
        this.hoveredVOffset = o;
        this.texture = arg;
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
        int k = this.v;
        if (this.isHovered()) {
            k += this.hoveredVOffset;
        }
        TexturedButtonWidget.drawTexture(arg, this.x, this.y, this.u, k, this.width, this.height, this.textureWidth, this.textureHeight);
        RenderSystem.enableDepthTest();
    }
}

