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
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentEntityModel
extends Model {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final ModelPart field_3593 = new ModelPart(32, 32, 0, 6);

    public TridentEntityModel() {
        super(RenderLayer::getEntitySolid);
        this.field_3593.addCuboid(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f, 0.0f);
        ModelPart lv = new ModelPart(32, 32, 4, 0);
        lv.addCuboid(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f);
        this.field_3593.addChild(lv);
        ModelPart lv2 = new ModelPart(32, 32, 4, 3);
        lv2.addCuboid(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.field_3593.addChild(lv2);
        ModelPart lv3 = new ModelPart(32, 32, 0, 0);
        lv3.addCuboid(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.field_3593.addChild(lv3);
        ModelPart lv4 = new ModelPart(32, 32, 4, 3);
        lv4.mirror = true;
        lv4.addCuboid(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.field_3593.addChild(lv4);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.field_3593.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

