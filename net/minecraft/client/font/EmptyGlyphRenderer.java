/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EmptyGlyphRenderer
extends GlyphRenderer {
    public EmptyGlyphRenderer() {
        super(RenderLayer.getText(new Identifier("")), RenderLayer.getTextSeeThrough(new Identifier("")), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void draw(boolean bl, float f, float g, Matrix4f arg, VertexConsumer arg2, float h, float i, float j, float k, int l) {
    }
}

