/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final ModelPart skull;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int i, int j, int k, int l) {
        super(RenderLayer::getEntityTranslucent);
        this.textureWidth = k;
        this.textureHeight = l;
        this.skull = new ModelPart(this, i, j);
        this.skull.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.skull.setPivot(0.0f, 0.0f, 0.0f);
    }

    public void method_2821(float f, float g, float h) {
        this.skull.yaw = g * ((float)Math.PI / 180);
        this.skull.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
        this.skull.render(arg, arg2, i, j, f, g, h, k);
    }
}

