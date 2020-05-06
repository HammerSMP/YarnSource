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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class DragonHeadEntityModel
extends SkullEntityModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadEntityModel(float f) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        float g = -16.0f;
        this.head = new ModelPart(this);
        this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, f, 176, 44);
        this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, f, 112, 30);
        this.head.mirror = true;
        this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.head.mirror = false;
        this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
        this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
        this.jaw = new ModelPart(this);
        this.jaw.setPivot(0.0f, 4.0f, -8.0f);
        this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, f, 176, 65);
        this.head.addChild(this.jaw);
    }

    @Override
    public void method_2821(float f, float g, float h) {
        this.jaw.pitch = (float)(Math.sin(f * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = g * ((float)Math.PI / 180);
        this.head.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
        arg.push();
        arg.translate(0.0, -0.374375f, 0.0);
        arg.scale(0.75f, 0.75f, 0.75f);
        this.head.render(arg, arg2, i, j, f, g, h, k);
        arg.pop();
    }
}

