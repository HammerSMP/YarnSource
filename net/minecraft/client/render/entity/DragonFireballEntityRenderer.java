/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    public DragonFireballEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    protected int getBlockLight(DragonFireballEntity arg, float f) {
        return 15;
    }

    @Override
    public void render(DragonFireballEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.scale(2.0f, 2.0f, 2.0f);
        arg2.multiply(this.dispatcher.getRotation());
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry lv = arg2.peek();
        Matrix4f lv2 = lv.getModel();
        Matrix3f lv3 = lv.getNormal();
        VertexConsumer lv4 = arg3.getBuffer(LAYER);
        DragonFireballEntityRenderer.produceVertex(lv4, lv2, lv3, i, 0.0f, 0, 0, 1);
        DragonFireballEntityRenderer.produceVertex(lv4, lv2, lv3, i, 1.0f, 0, 1, 1);
        DragonFireballEntityRenderer.produceVertex(lv4, lv2, lv3, i, 1.0f, 1, 1, 0);
        DragonFireballEntityRenderer.produceVertex(lv4, lv2, lv3, i, 0.0f, 1, 0, 0);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    private static void produceVertex(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, int i, float f, int j, int k, int l) {
        arg.vertex(arg2, f - 0.5f, (float)j - 0.25f, 0.0f).color(255, 255, 255, 255).texture(k, l).overlay(OverlayTexture.DEFAULT_UV).light(i).normal(arg3, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(DragonFireballEntity arg) {
        return TEXTURE;
    }
}

