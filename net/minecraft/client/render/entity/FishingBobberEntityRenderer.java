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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);

    public FishingBobberEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(FishingBobberEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float w;
        double v;
        double u;
        double t;
        PlayerEntity lv = arg.getOwner();
        if (lv == null) {
            return;
        }
        arg2.push();
        arg2.push();
        arg2.scale(0.5f, 0.5f, 0.5f);
        arg2.multiply(this.dispatcher.getRotation());
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry lv2 = arg2.peek();
        Matrix4f lv3 = lv2.getModel();
        Matrix3f lv4 = lv2.getNormal();
        VertexConsumer lv5 = arg3.getBuffer(LAYER);
        FishingBobberEntityRenderer.method_23840(lv5, lv3, lv4, i, 0.0f, 0, 0, 1);
        FishingBobberEntityRenderer.method_23840(lv5, lv3, lv4, i, 1.0f, 0, 1, 1);
        FishingBobberEntityRenderer.method_23840(lv5, lv3, lv4, i, 1.0f, 1, 1, 0);
        FishingBobberEntityRenderer.method_23840(lv5, lv3, lv4, i, 0.0f, 1, 0, 0);
        arg2.pop();
        int j = lv.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack lv6 = lv.getMainHandStack();
        if (lv6.getItem() != Items.FISHING_ROD) {
            j = -j;
        }
        float h = lv.getHandSwingProgress(g);
        float k = MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
        float l = MathHelper.lerp(g, lv.prevBodyYaw, lv.bodyYaw) * ((float)Math.PI / 180);
        double d = MathHelper.sin(l);
        double e = MathHelper.cos(l);
        double m = (double)j * 0.35;
        double n = 0.8;
        if (this.dispatcher.gameOptions != null && this.dispatcher.gameOptions.perspective > 0 || lv != MinecraftClient.getInstance().player) {
            double o = MathHelper.lerp((double)g, lv.prevX, lv.getX()) - e * m - d * 0.8;
            double p = lv.prevY + (double)lv.getStandingEyeHeight() + (lv.getY() - lv.prevY) * (double)g - 0.45;
            double q = MathHelper.lerp((double)g, lv.prevZ, lv.getZ()) - d * m + e * 0.8;
            float r = lv.isInSneakingPose() ? -0.1875f : 0.0f;
        } else {
            double s = this.dispatcher.gameOptions.fov;
            Vec3d lv7 = new Vec3d((double)j * -0.36 * (s /= 100.0), -0.045 * s, 0.4);
            lv7 = lv7.rotateX(-MathHelper.lerp(g, lv.prevPitch, lv.pitch) * ((float)Math.PI / 180));
            lv7 = lv7.rotateY(-MathHelper.lerp(g, lv.prevYaw, lv.yaw) * ((float)Math.PI / 180));
            lv7 = lv7.rotateY(k * 0.5f);
            lv7 = lv7.rotateX(-k * 0.7f);
            t = MathHelper.lerp((double)g, lv.prevX, lv.getX()) + lv7.x;
            u = MathHelper.lerp((double)g, lv.prevY, lv.getY()) + lv7.y;
            v = MathHelper.lerp((double)g, lv.prevZ, lv.getZ()) + lv7.z;
            w = lv.getStandingEyeHeight();
        }
        double x = MathHelper.lerp((double)g, arg.prevX, arg.getX());
        double y = MathHelper.lerp((double)g, arg.prevY, arg.getY()) + 0.25;
        double z = MathHelper.lerp((double)g, arg.prevZ, arg.getZ());
        float aa = (float)(t - x);
        float ab = (float)(u - y) + w;
        float ac = (float)(v - z);
        VertexConsumer lv8 = arg3.getBuffer(RenderLayer.getLines());
        Matrix4f lv9 = arg2.peek().getModel();
        int ad = 16;
        for (int ae = 0; ae < 16; ++ae) {
            FishingBobberEntityRenderer.method_23172(aa, ab, ac, lv8, lv9, FishingBobberEntityRenderer.method_23954(ae, 16));
            FishingBobberEntityRenderer.method_23172(aa, ab, ac, lv8, lv9, FishingBobberEntityRenderer.method_23954(ae + 1, 16));
        }
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    private static float method_23954(int i, int j) {
        return (float)i / (float)j;
    }

    private static void method_23840(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, int i, float f, int j, int k, int l) {
        arg.vertex(arg2, f - 0.5f, (float)j - 0.5f, 0.0f).color(255, 255, 255, 255).texture(k, l).overlay(OverlayTexture.DEFAULT_UV).light(i).normal(arg3, 0.0f, 1.0f, 0.0f).next();
    }

    private static void method_23172(float f, float g, float h, VertexConsumer arg, Matrix4f arg2, float i) {
        arg.vertex(arg2, f * i, g * (i * i + i) * 0.5f + 0.25f, h * i).color(0, 0, 0, 255).next();
    }

    @Override
    public Identifier getTexture(FishingBobberEntity arg) {
        return TEXTURE;
    }
}

