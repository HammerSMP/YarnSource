/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity> {
    public FallingBlockEntityRenderer(EntityRenderDispatcher arg) {
        super(arg);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(FallingBlockEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        BlockState lv = arg.getBlockState();
        if (lv.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        World lv2 = arg.getWorldClient();
        if (lv == lv2.getBlockState(arg.getBlockPos()) || lv.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        arg2.push();
        BlockPos lv3 = new BlockPos(arg.getX(), arg.getBoundingBox().maxY, arg.getZ());
        arg2.translate(-0.5, 0.0, -0.5);
        BlockRenderManager lv4 = MinecraftClient.getInstance().getBlockRenderManager();
        lv4.getModelRenderer().render(lv2, lv4.getModel(lv), lv, lv3, arg2, arg3.getBuffer(RenderLayers.method_29359(lv)), false, new Random(), lv.getRenderingSeed(arg.getFallingBlockPos()), OverlayTexture.DEFAULT_UV);
        arg2.pop();
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    public Identifier getTexture(FallingBlockEntity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

