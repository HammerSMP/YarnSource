/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity> {
    public static final Identifier CRYSTAL_BEAM_TEXTURE = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEXTURE = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon.png");
    private static final Identifier EYE_TEXTURE = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderLayer DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private static final RenderLayer DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
    private static final RenderLayer DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
    private static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel model = new DragonEntityModel();

    public EnderDragonEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(EnderDragonEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        float h = (float)arg.getSegmentProperties(7, g)[0];
        float j = (float)(arg.getSegmentProperties(5, g)[1] - arg.getSegmentProperties(10, g)[1]);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-h));
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0f));
        arg2.translate(0.0, 0.0, 1.0);
        arg2.scale(-1.0f, -1.0f, 1.0f);
        arg2.translate(0.0, -1.501f, 0.0);
        boolean bl = arg.hurtTime > 0;
        this.model.animateModel(arg, 0.0f, 0.0f, g);
        if (arg.ticksSinceDeath > 0) {
            float k = (float)arg.ticksSinceDeath / 200.0f;
            VertexConsumer lv = arg3.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE, k));
            this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            VertexConsumer lv2 = arg3.getBuffer(DRAGON_DECAL);
            this.model.render(arg2, lv2, i, OverlayTexture.getUv(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            VertexConsumer lv3 = arg3.getBuffer(DRAGON_CUTOUT);
            this.model.render(arg2, lv3, i, OverlayTexture.getUv(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        VertexConsumer lv4 = arg3.getBuffer(DRAGON_EYES);
        this.model.render(arg2, lv4, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        if (arg.ticksSinceDeath > 0) {
            float l = ((float)arg.ticksSinceDeath + g) / 200.0f;
            float m = 0.0f;
            if (l > 0.8f) {
                m = (l - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            VertexConsumer lv5 = arg3.getBuffer(RenderLayer.getLightning());
            arg2.push();
            arg2.translate(0.0, -1.0, -2.0);
            int n = 0;
            while ((float)n < (l + l * l) / 2.0f * 60.0f) {
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0f));
                arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0f));
                arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0f));
                arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0f));
                arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0f));
                arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0f + l * 90.0f));
                float o = random.nextFloat() * 20.0f + 5.0f + m * 10.0f;
                float p = random.nextFloat() * 2.0f + 1.0f + m * 2.0f;
                Matrix4f lv6 = arg2.peek().getModel();
                int q = (int)(255.0f * (1.0f - m));
                EnderDragonEntityRenderer.method_23157(lv5, lv6, q);
                EnderDragonEntityRenderer.method_23156(lv5, lv6, o, p);
                EnderDragonEntityRenderer.method_23158(lv5, lv6, o, p);
                EnderDragonEntityRenderer.method_23157(lv5, lv6, q);
                EnderDragonEntityRenderer.method_23158(lv5, lv6, o, p);
                EnderDragonEntityRenderer.method_23159(lv5, lv6, o, p);
                EnderDragonEntityRenderer.method_23157(lv5, lv6, q);
                EnderDragonEntityRenderer.method_23159(lv5, lv6, o, p);
                EnderDragonEntityRenderer.method_23156(lv5, lv6, o, p);
                ++n;
            }
            arg2.pop();
        }
        arg2.pop();
        if (arg.connectedCrystal != null) {
            arg2.push();
            float r = (float)(arg.connectedCrystal.getX() - MathHelper.lerp((double)g, arg.prevX, arg.getX()));
            float s = (float)(arg.connectedCrystal.getY() - MathHelper.lerp((double)g, arg.prevY, arg.getY()));
            float t = (float)(arg.connectedCrystal.getZ() - MathHelper.lerp((double)g, arg.prevZ, arg.getZ()));
            EnderDragonEntityRenderer.renderCrystalBeam(r, s + EndCrystalEntityRenderer.getYOffset(arg.connectedCrystal, g), t, g, arg.age, arg2, arg3, i);
            arg2.pop();
        }
        super.render(arg, f, g, arg2, arg3, i);
    }

    private static void method_23157(VertexConsumer arg, Matrix4f arg2, int i) {
        arg.vertex(arg2, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
        arg.vertex(arg2, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
    }

    private static void method_23156(VertexConsumer arg, Matrix4f arg2, float f, float g) {
        arg.vertex(arg2, -HALF_SQRT_3 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23158(VertexConsumer arg, Matrix4f arg2, float f, float g) {
        arg.vertex(arg2, HALF_SQRT_3 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23159(VertexConsumer arg, Matrix4f arg2, float f, float g) {
        arg.vertex(arg2, 0.0f, f, 1.0f * g).color(255, 0, 255, 0).next();
    }

    public static void renderCrystalBeam(float f, float g, float h, float i, int j, MatrixStack arg, VertexConsumerProvider arg2, int k) {
        float l = MathHelper.sqrt(f * f + h * h);
        float m = MathHelper.sqrt(f * f + g * g + h * h);
        arg.push();
        arg.translate(0.0, 2.0, 0.0);
        arg.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.atan2(h, f)) - 1.5707964f));
        arg.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float)(-Math.atan2(l, g)) - 1.5707964f));
        VertexConsumer lv = arg2.getBuffer(CRYSTAL_BEAM_LAYER);
        float n = 0.0f - ((float)j + i) * 0.01f;
        float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0f - ((float)j + i) * 0.01f;
        int p = 8;
        float q = 0.0f;
        float r = 0.75f;
        float s = 0.0f;
        MatrixStack.Entry lv2 = arg.peek();
        Matrix4f lv3 = lv2.getModel();
        Matrix3f lv4 = lv2.getNormal();
        for (int t = 1; t <= 8; ++t) {
            float u = MathHelper.sin((float)t * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float v = MathHelper.cos((float)t * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float w = (float)t / 8.0f;
            lv.vertex(lv3, q * 0.2f, r * 0.2f, 0.0f).color(0, 0, 0, 255).texture(s, n).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(lv4, 0.0f, -1.0f, 0.0f).next();
            lv.vertex(lv3, q, r, m).color(255, 255, 255, 255).texture(s, o).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(lv4, 0.0f, -1.0f, 0.0f).next();
            lv.vertex(lv3, u, v, m).color(255, 255, 255, 255).texture(w, o).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(lv4, 0.0f, -1.0f, 0.0f).next();
            lv.vertex(lv3, u * 0.2f, v * 0.2f, 0.0f).color(0, 0, 0, 255).texture(w, n).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(lv4, 0.0f, -1.0f, 0.0f).next();
            q = u;
            r = v;
            s = w;
        }
        arg.pop();
    }

    @Override
    public Identifier getTexture(EnderDragonEntity arg) {
        return TEXTURE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DragonEntityModel
    extends EntityModel<EnderDragonEntity> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private ModelPart wing;
        private ModelPart field_21548;
        private ModelPart field_21549;
        private ModelPart field_21550;
        private ModelPart field_21551;
        private ModelPart field_21552;
        private ModelPart field_21553;
        private ModelPart field_21554;
        private ModelPart field_21555;
        private ModelPart wingTip;
        private ModelPart frontLeg;
        private ModelPart frontLegTip;
        private ModelPart frontFoot;
        private ModelPart rearLeg;
        private ModelPart rearLegTip;
        private ModelPart rearFoot;
        @Nullable
        private EnderDragonEntity dragon;
        private float tickDelta;

        public DragonEntityModel() {
            this.textureWidth = 256;
            this.textureHeight = 256;
            float f = -16.0f;
            this.head = new ModelPart(this);
            this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 0.0f, 176, 44);
            this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 0.0f, 112, 30);
            this.head.mirror = true;
            this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.head.mirror = false;
            this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.jaw = new ModelPart(this);
            this.jaw.setPivot(0.0f, 4.0f, -8.0f);
            this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 0.0f, 176, 65);
            this.head.addChild(this.jaw);
            this.neck = new ModelPart(this);
            this.neck.addCuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 0.0f, 192, 104);
            this.neck.addCuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 0.0f, 48, 0);
            this.body = new ModelPart(this);
            this.body.setPivot(0.0f, 4.0f, 8.0f);
            this.body.addCuboid("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, 0.0f, 0, 0);
            this.body.addCuboid("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, 0.0f, 220, 53);
            this.wing = new ModelPart(this);
            this.wing.mirror = true;
            this.wing.setPivot(12.0f, 5.0f, 2.0f);
            this.wing.addCuboid("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.wing.addCuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.field_21548 = new ModelPart(this);
            this.field_21548.mirror = true;
            this.field_21548.setPivot(56.0f, 0.0f, 0.0f);
            this.field_21548.addCuboid("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.field_21548.addCuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.wing.addChild(this.field_21548);
            this.field_21549 = new ModelPart(this);
            this.field_21549.setPivot(12.0f, 20.0f, 2.0f);
            this.field_21549.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.field_21550 = new ModelPart(this);
            this.field_21550.setPivot(0.0f, 20.0f, -1.0f);
            this.field_21550.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.field_21549.addChild(this.field_21550);
            this.field_21551 = new ModelPart(this);
            this.field_21551.setPivot(0.0f, 23.0f, 0.0f);
            this.field_21551.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.field_21550.addChild(this.field_21551);
            this.field_21552 = new ModelPart(this);
            this.field_21552.setPivot(16.0f, 16.0f, 42.0f);
            this.field_21552.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.field_21553 = new ModelPart(this);
            this.field_21553.setPivot(0.0f, 32.0f, -4.0f);
            this.field_21553.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.field_21552.addChild(this.field_21553);
            this.field_21554 = new ModelPart(this);
            this.field_21554.setPivot(0.0f, 31.0f, 4.0f);
            this.field_21554.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.field_21553.addChild(this.field_21554);
            this.field_21555 = new ModelPart(this);
            this.field_21555.setPivot(-12.0f, 5.0f, 2.0f);
            this.field_21555.addCuboid("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.field_21555.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.wingTip = new ModelPart(this);
            this.wingTip.setPivot(-56.0f, 0.0f, 0.0f);
            this.wingTip.addCuboid("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.wingTip.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.field_21555.addChild(this.wingTip);
            this.frontLeg = new ModelPart(this);
            this.frontLeg.setPivot(-12.0f, 20.0f, 2.0f);
            this.frontLeg.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.frontLegTip = new ModelPart(this);
            this.frontLegTip.setPivot(0.0f, 20.0f, -1.0f);
            this.frontLegTip.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.frontLeg.addChild(this.frontLegTip);
            this.frontFoot = new ModelPart(this);
            this.frontFoot.setPivot(0.0f, 23.0f, 0.0f);
            this.frontFoot.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.frontLegTip.addChild(this.frontFoot);
            this.rearLeg = new ModelPart(this);
            this.rearLeg.setPivot(-16.0f, 16.0f, 42.0f);
            this.rearLeg.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.rearLegTip = new ModelPart(this);
            this.rearLegTip.setPivot(0.0f, 32.0f, -4.0f);
            this.rearLegTip.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.rearLeg.addChild(this.rearLegTip);
            this.rearFoot = new ModelPart(this);
            this.rearFoot.setPivot(0.0f, 31.0f, 4.0f);
            this.rearFoot.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.rearLegTip.addChild(this.rearFoot);
        }

        @Override
        public void animateModel(EnderDragonEntity arg, float f, float g, float h) {
            this.dragon = arg;
            this.tickDelta = h;
        }

        @Override
        public void setAngles(EnderDragonEntity arg, float f, float g, float h, float i, float j) {
        }

        @Override
        public void render(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
            arg.push();
            float l = MathHelper.lerp(this.tickDelta, this.dragon.prevWingPosition, this.dragon.wingPosition);
            this.jaw.pitch = (float)(Math.sin(l * ((float)Math.PI * 2)) + 1.0) * 0.2f;
            float m = (float)(Math.sin(l * ((float)Math.PI * 2) - 1.0f) + 1.0);
            m = (m * m + m * 2.0f) * 0.05f;
            arg.translate(0.0, m - 2.0f, -3.0);
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(m * 2.0f));
            float n = 0.0f;
            float o = 20.0f;
            float p = -12.0f;
            float q = 1.5f;
            double[] ds = this.dragon.getSegmentProperties(6, this.tickDelta);
            float r = MathHelper.fwrapDegrees(this.dragon.getSegmentProperties(5, this.tickDelta)[0] - this.dragon.getSegmentProperties(10, this.tickDelta)[0]);
            float s = MathHelper.fwrapDegrees(this.dragon.getSegmentProperties(5, this.tickDelta)[0] + (double)(r / 2.0f));
            float t = l * ((float)Math.PI * 2);
            for (int u = 0; u < 5; ++u) {
                double[] es = this.dragon.getSegmentProperties(5 - u, this.tickDelta);
                float v = (float)Math.cos((float)u * 0.45f + t) * 0.15f;
                this.neck.yaw = MathHelper.fwrapDegrees(es[0] - ds[0]) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pitch = v + this.dragon.method_6823(u, ds, es) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = -MathHelper.fwrapDegrees(es[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = o;
                this.neck.pivotZ = p;
                this.neck.pivotX = n;
                o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
                p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(arg, arg2, i, j);
            }
            this.head.pivotY = o;
            this.head.pivotZ = p;
            this.head.pivotX = n;
            double[] fs = this.dragon.getSegmentProperties(0, this.tickDelta);
            this.head.yaw = MathHelper.fwrapDegrees(fs[0] - ds[0]) * ((float)Math.PI / 180);
            this.head.pitch = MathHelper.fwrapDegrees(this.dragon.method_6823(6, ds, fs)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.roll = -MathHelper.fwrapDegrees(fs[0] - (double)s) * ((float)Math.PI / 180);
            this.head.render(arg, arg2, i, j);
            arg.push();
            arg.translate(0.0, 1.0, 0.0);
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-r * 1.5f));
            arg.translate(0.0, -1.0, 0.0);
            this.body.roll = 0.0f;
            this.body.render(arg, arg2, i, j);
            float w = l * ((float)Math.PI * 2);
            this.wing.pitch = 0.125f - (float)Math.cos(w) * 0.2f;
            this.wing.yaw = -0.25f;
            this.wing.roll = -((float)(Math.sin(w) + 0.125)) * 0.8f;
            this.field_21548.roll = (float)(Math.sin(w + 2.0f) + 0.5) * 0.75f;
            this.field_21555.pitch = this.wing.pitch;
            this.field_21555.yaw = -this.wing.yaw;
            this.field_21555.roll = -this.wing.roll;
            this.wingTip.roll = -this.field_21548.roll;
            this.method_23838(arg, arg2, i, j, m, this.wing, this.field_21549, this.field_21550, this.field_21551, this.field_21552, this.field_21553, this.field_21554);
            this.method_23838(arg, arg2, i, j, m, this.field_21555, this.frontLeg, this.frontLegTip, this.frontFoot, this.rearLeg, this.rearLegTip, this.rearFoot);
            arg.pop();
            float x = -((float)Math.sin(l * ((float)Math.PI * 2))) * 0.0f;
            t = l * ((float)Math.PI * 2);
            o = 10.0f;
            p = 60.0f;
            n = 0.0f;
            ds = this.dragon.getSegmentProperties(11, this.tickDelta);
            for (int y = 0; y < 12; ++y) {
                fs = this.dragon.getSegmentProperties(12 + y, this.tickDelta);
                x = (float)((double)x + Math.sin((float)y * 0.45f + t) * (double)0.05f);
                this.neck.yaw = (MathHelper.fwrapDegrees(fs[0] - ds[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.neck.pitch = x + (float)(fs[1] - ds[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = MathHelper.fwrapDegrees(fs[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = o;
                this.neck.pivotZ = p;
                this.neck.pivotX = n;
                o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
                p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(arg, arg2, i, j);
            }
            arg.pop();
        }

        private void method_23838(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, ModelPart arg3, ModelPart arg4, ModelPart arg5, ModelPart arg6, ModelPart arg7, ModelPart arg8, ModelPart arg9) {
            arg7.pitch = 1.0f + f * 0.1f;
            arg8.pitch = 0.5f + f * 0.1f;
            arg9.pitch = 0.75f + f * 0.1f;
            arg4.pitch = 1.3f + f * 0.1f;
            arg5.pitch = -0.5f - f * 0.1f;
            arg6.pitch = 0.75f + f * 0.1f;
            arg3.render(arg, arg2, i, j);
            arg4.render(arg, arg2, i, j);
            arg7.render(arg, arg2, i, j);
        }
    }
}

