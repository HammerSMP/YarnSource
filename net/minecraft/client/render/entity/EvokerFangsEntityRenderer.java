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
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel();

    public EvokerFangsEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(EvokerFangsEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        float h = arg.getAnimationProgress(g);
        if (h == 0.0f) {
            return;
        }
        float j = 2.0f;
        if (h > 0.9f) {
            j = (float)((double)j * ((1.0 - (double)h) / (double)0.1f));
        }
        arg2.push();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f - arg.yaw));
        arg2.scale(-j, -j, j);
        float k = 0.03125f;
        arg2.translate(0.0, -0.626f, 0.0);
        arg2.scale(0.5f, 0.5f, 0.5f);
        this.model.setAngles(arg, h, 0.0f, 0.0f, arg.yaw, arg.pitch);
        VertexConsumer lv = arg3.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(arg2, lv, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(EvokerFangsEntity arg) {
        return TEXTURE;
    }
}

