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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        if (((PassiveEntity)arg3).isBaby() || ((Entity)arg3).isInvisible()) {
            return;
        }
        BlockRenderManager lv = MinecraftClient.getInstance().getBlockRenderManager();
        BlockState lv2 = ((MooshroomEntity)arg3).getMooshroomType().getMushroomState();
        int m = LivingEntityRenderer.getOverlay(arg3, 0.0f);
        arg.push();
        arg.translate(0.2f, -0.35f, 0.5);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-48.0f));
        arg.scale(-1.0f, -1.0f, 1.0f);
        arg.translate(-0.5, -0.5, -0.5);
        lv.renderBlockAsEntity(lv2, arg, arg2, i, m);
        arg.pop();
        arg.push();
        arg.translate(0.2f, -0.35f, 0.5);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(42.0f));
        arg.translate(0.1f, 0.0, -0.6f);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-48.0f));
        arg.scale(-1.0f, -1.0f, 1.0f);
        arg.translate(-0.5, -0.5, -0.5);
        lv.renderBlockAsEntity(lv2, arg, arg2, i, m);
        arg.pop();
        arg.push();
        ((CowEntityModel)this.getContextModel()).getHead().rotate(arg);
        arg.translate(0.0, -0.7f, -0.2f);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-78.0f));
        arg.scale(-1.0f, -1.0f, 1.0f);
        arg.translate(-0.5, -0.5, -0.5);
        lv.renderBlockAsEntity(lv2, arg, arg2, i, m);
        arg.pop();
    }
}

