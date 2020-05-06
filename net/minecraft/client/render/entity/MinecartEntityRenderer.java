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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity>
extends EntityRenderer<T> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/minecart.png");
    protected final EntityModel<T> model = new MinecartEntityModel();

    public MinecartEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.7f;
    }

    @Override
    public void render(T arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        super.render(arg, f, g, arg2, arg3, i);
        arg2.push();
        long l = (long)((Entity)arg).getEntityId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float h = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float j = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float k = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        arg2.translate(h, j, k);
        double d = MathHelper.lerp((double)g, ((AbstractMinecartEntity)arg).lastRenderX, ((Entity)arg).getX());
        double e = MathHelper.lerp((double)g, ((AbstractMinecartEntity)arg).lastRenderY, ((Entity)arg).getY());
        double m = MathHelper.lerp((double)g, ((AbstractMinecartEntity)arg).lastRenderZ, ((Entity)arg).getZ());
        double n = 0.3f;
        Vec3d lv = ((AbstractMinecartEntity)arg).method_7508(d, e, m);
        float o = MathHelper.lerp(g, ((AbstractMinecartEntity)arg).prevPitch, ((AbstractMinecartEntity)arg).pitch);
        if (lv != null) {
            Vec3d lv2 = ((AbstractMinecartEntity)arg).method_7505(d, e, m, 0.3f);
            Vec3d lv3 = ((AbstractMinecartEntity)arg).method_7505(d, e, m, -0.3f);
            if (lv2 == null) {
                lv2 = lv;
            }
            if (lv3 == null) {
                lv3 = lv;
            }
            arg2.translate(lv.x - d, (lv2.y + lv3.y) / 2.0 - e, lv.z - m);
            Vec3d lv4 = lv3.add(-lv2.x, -lv2.y, -lv2.z);
            if (lv4.length() != 0.0) {
                lv4 = lv4.normalize();
                f = (float)(Math.atan2(lv4.z, lv4.x) * 180.0 / Math.PI);
                o = (float)(Math.atan(lv4.y) * 73.0);
            }
        }
        arg2.translate(0.0, 0.375, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-o));
        float p = (float)((AbstractMinecartEntity)arg).getDamageWobbleTicks() - g;
        float q = ((AbstractMinecartEntity)arg).getDamageWobbleStrength() - g;
        if (q < 0.0f) {
            q = 0.0f;
        }
        if (p > 0.0f) {
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(p) * p * q / 10.0f * (float)((AbstractMinecartEntity)arg).getDamageWobbleSide()));
        }
        int r = ((AbstractMinecartEntity)arg).getBlockOffset();
        BlockState lv5 = ((AbstractMinecartEntity)arg).getContainedBlock();
        if (lv5.getRenderType() != BlockRenderType.INVISIBLE) {
            arg2.push();
            float s = 0.75f;
            arg2.scale(0.75f, 0.75f, 0.75f);
            arg2.translate(-0.5, (float)(r - 8) / 16.0f, 0.5);
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            this.renderBlock(arg, g, lv5, arg2, arg3, i);
            arg2.pop();
        }
        arg2.scale(-1.0f, -1.0f, 1.0f);
        this.model.setAngles(arg, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer lv6 = arg3.getBuffer(this.model.getLayer(this.getTexture(arg)));
        this.model.render(arg2, lv6, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
    }

    @Override
    public Identifier getTexture(T arg) {
        return TEXTURE;
    }

    protected void renderBlock(T arg, float f, BlockState arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(arg2, arg3, arg4, i, OverlayTexture.DEFAULT_UV);
    }
}

