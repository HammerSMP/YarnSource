/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
    private final ModelPart aura = new ModelPart(64, 64, 0, 0);

    public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> arg) {
        super(arg);
        this.aura.addCuboid(-8.0f, -16.0f, -8.0f, 16.0f, 32.0f, 16.0f);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        if (!((LivingEntity)arg3).isUsingRiptide()) {
            return;
        }
        VertexConsumer lv = arg2.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        for (int m = 0; m < 3; ++m) {
            arg.push();
            float n = j * (float)(-(45 + m * 5));
            arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
            float o = 0.75f * (float)m;
            arg.scale(o, o, o);
            arg.translate(0.0, -0.2f + 0.6f * (float)m, 0.0);
            this.aura.render(arg, lv, i, OverlayTexture.DEFAULT_UV);
            arg.pop();
        }
    }
}

