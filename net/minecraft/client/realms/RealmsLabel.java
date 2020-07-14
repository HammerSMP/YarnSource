/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsLabel
implements Element {
    private final Text text;
    private final int x;
    private final int y;
    private final int color;

    public RealmsLabel(Text arg, int x, int y, int color) {
        this.text = arg;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void render(Screen screen, MatrixStack arg2) {
        screen.drawCenteredText(arg2, MinecraftClient.getInstance().textRenderer, this.text, this.x, this.y, this.color);
    }

    public String getText() {
        return this.text.getString();
    }
}

