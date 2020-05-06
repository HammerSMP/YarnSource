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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityRenderer
extends EntityRenderer<LlamaSpitEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/llama/spit.png");
    private final LlamaSpitEntityModel<LlamaSpitEntity> model = new LlamaSpitEntityModel();

    public LlamaSpitEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(LlamaSpitEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        arg2.push();
        arg2.translate(0.0, 0.15f, 0.0);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, arg.prevYaw, arg.yaw) - 90.0f));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, arg.prevPitch, arg.pitch)));
        this.model.setAngles(arg, g, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(LlamaSpitEntity arg) {
        return TEXTURE;
    }
}

