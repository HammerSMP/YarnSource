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
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityRenderer
extends EntityRenderer<ShulkerBulletEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/shulker/spark.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
    private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel();

    public ShulkerBulletEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    protected int getBlockLight(ShulkerBulletEntity arg, BlockPos arg2) {
        return 15;
    }

    @Override
    public void render(ShulkerBulletEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        float h = MathHelper.lerpAngle(arg.prevYaw, arg.yaw, g);
        float j = MathHelper.lerp(g, arg.prevPitch, arg.pitch);
        float k = (float)arg.age + g;
        arg2.translate(0.0, 0.15f, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(k * 0.1f) * 180.0f));
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.cos(k * 0.1f) * 180.0f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(k * 0.15f) * 360.0f));
        arg2.scale(-0.5f, -0.5f, 0.5f);
        this.model.setAngles(arg, 0.0f, 0.0f, 0.0f, h, j);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer lv2 = arg3.getBuffer(LAYER);
        this.model.render(arg2, lv2, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 0.15f);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(ShulkerBulletEntity arg) {
        return TEXTURE;
    }
}

