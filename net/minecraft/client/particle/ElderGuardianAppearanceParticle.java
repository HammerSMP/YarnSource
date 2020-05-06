/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianAppearanceParticle
extends Particle {
    private final Model model = new GuardianEntityModel();
    private final RenderLayer LAYER = RenderLayer.getEntityTranslucent(ElderGuardianEntityRenderer.TEXTURE);

    private ElderGuardianAppearanceParticle(ClientWorld arg, double d, double e, double f) {
        super(arg, d, e, f);
        this.gravityStrength = 0.0f;
        this.maxAge = 30;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer arg, Camera arg2, float f) {
        float g = ((float)this.age + f) / (float)this.maxAge;
        float h = 0.05f + 0.5f * MathHelper.sin(g * (float)Math.PI);
        MatrixStack lv = new MatrixStack();
        lv.multiply(arg2.getRotation());
        lv.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(150.0f * g - 60.0f));
        lv.scale(-1.0f, -1.0f, 1.0f);
        lv.translate(0.0, -1.101f, 1.5);
        VertexConsumerProvider.Immediate lv2 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lv3 = lv2.getBuffer(this.LAYER);
        this.model.render(lv, lv3, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, h);
        lv2.draw();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType arg, ClientWorld arg2, double d, double e, double f, double g, double h, double i) {
            return new ElderGuardianAppearanceParticle(arg2, d, e, f);
        }
    }
}

