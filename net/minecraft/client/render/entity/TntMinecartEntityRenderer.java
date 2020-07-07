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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends MinecartEntityRenderer<TntMinecartEntity> {
    public TntMinecartEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    protected void renderBlock(TntMinecartEntity arg, float f, BlockState arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        int j = arg.getFuseTicks();
        if (j > -1 && (float)j - f + 1.0f < 10.0f) {
            float g = 1.0f - ((float)j - f + 1.0f) / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            arg3.scale(h, h, h);
        }
        TntMinecartEntityRenderer.renderFlashingBlock(arg2, arg3, arg4, i, j > -1 && j / 5 % 2 == 0);
    }

    public static void renderFlashingBlock(BlockState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i, boolean bl) {
        int k;
        if (bl) {
            int j = OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10);
        } else {
            k = OverlayTexture.DEFAULT_UV;
        }
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(arg, arg2, arg3, i, k);
    }
}

