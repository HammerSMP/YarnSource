/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
public class BlockRenderManager
implements SynchronousResourceReloadListener {
    private final BlockModels models;
    private final BlockModelRenderer blockModelRenderer;
    private final FluidRenderer fluidRenderer;
    private final Random random = new Random();
    private final BlockColors blockColors;

    public BlockRenderManager(BlockModels models, BlockColors blockColors) {
        this.models = models;
        this.blockColors = blockColors;
        this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
        this.fluidRenderer = new FluidRenderer();
    }

    public BlockModels getModels() {
        return this.models;
    }

    public void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer) {
        if (state.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        BakedModel lv = this.models.getModel(state);
        long l = state.getRenderingSeed(pos);
        this.blockModelRenderer.render(world, lv, state, pos, matrix, vertexConsumer, true, this.random, l, OverlayTexture.DEFAULT_UV);
    }

    public boolean renderBlock(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random) {
        try {
            BlockRenderType lv = state.getRenderType();
            if (lv != BlockRenderType.MODEL) {
                return false;
            }
            return this.blockModelRenderer.render(world, this.getModel(state), state, pos, matrix, vertexConsumer, cull, random, state.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Tesselating block in world");
            CrashReportSection lv3 = lv2.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(lv3, pos, state);
            throw new CrashException(lv2);
        }
    }

    public boolean renderFluid(BlockPos pos, BlockRenderView arg2, VertexConsumer arg3, FluidState arg4) {
        try {
            return this.fluidRenderer.render(arg2, pos, arg3, arg4);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Tesselating liquid in world");
            CrashReportSection lv2 = lv.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(lv2, pos, null);
            throw new CrashException(lv);
        }
    }

    public BlockModelRenderer getModelRenderer() {
        return this.blockModelRenderer;
    }

    public BakedModel getModel(BlockState state) {
        return this.models.getModel(state);
    }

    public void renderBlockAsEntity(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, int overlay) {
        BlockRenderType lv = state.getRenderType();
        if (lv == BlockRenderType.INVISIBLE) {
            return;
        }
        switch (lv) {
            case MODEL: {
                BakedModel lv2 = this.getModel(state);
                int k = this.blockColors.getColor(state, null, null, 0);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                float h = (float)(k & 0xFF) / 255.0f;
                this.blockModelRenderer.render(matrices.peek(), vertexConsumer.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, lv2, f, g, h, light, overlay);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                BuiltinModelItemRenderer.INSTANCE.render(new ItemStack(state.getBlock()), ModelTransformation.Mode.NONE, matrices, vertexConsumer, light, overlay);
            }
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        this.fluidRenderer.onResourceReload();
    }
}

