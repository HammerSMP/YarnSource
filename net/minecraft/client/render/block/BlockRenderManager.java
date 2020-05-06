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

    public BlockRenderManager(BlockModels arg, BlockColors arg2) {
        this.models = arg;
        this.blockColors = arg2;
        this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
        this.fluidRenderer = new FluidRenderer();
    }

    public BlockModels getModels() {
        return this.models;
    }

    public void renderDamage(BlockState arg, BlockPos arg2, BlockRenderView arg3, MatrixStack arg4, VertexConsumer arg5) {
        if (arg.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        BakedModel lv = this.models.getModel(arg);
        long l = arg.getRenderingSeed(arg2);
        this.blockModelRenderer.render(arg3, lv, arg, arg2, arg4, arg5, true, this.random, l, OverlayTexture.DEFAULT_UV);
    }

    public boolean renderBlock(BlockState arg, BlockPos arg2, BlockRenderView arg3, MatrixStack arg4, VertexConsumer arg5, boolean bl, Random random) {
        try {
            BlockRenderType lv = arg.getRenderType();
            if (lv != BlockRenderType.MODEL) {
                return false;
            }
            return this.blockModelRenderer.render(arg3, this.getModel(arg), arg, arg2, arg4, arg5, bl, random, arg.getRenderingSeed(arg2), OverlayTexture.DEFAULT_UV);
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Tesselating block in world");
            CrashReportSection lv3 = lv2.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(lv3, arg2, arg);
            throw new CrashException(lv2);
        }
    }

    public boolean renderFluid(BlockPos arg, BlockRenderView arg2, VertexConsumer arg3, FluidState arg4) {
        try {
            return this.fluidRenderer.render(arg2, arg, arg3, arg4);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Tesselating liquid in world");
            CrashReportSection lv2 = lv.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(lv2, arg, null);
            throw new CrashException(lv);
        }
    }

    public BlockModelRenderer getModelRenderer() {
        return this.blockModelRenderer;
    }

    public BakedModel getModel(BlockState arg) {
        return this.models.getModel(arg);
    }

    public void renderBlockAsEntity(BlockState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        BlockRenderType lv = arg.getRenderType();
        if (lv == BlockRenderType.INVISIBLE) {
            return;
        }
        switch (lv) {
            case MODEL: {
                BakedModel lv2 = this.getModel(arg);
                int k = this.blockColors.getColor(arg, null, null, 0);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                float h = (float)(k & 0xFF) / 255.0f;
                this.blockModelRenderer.render(arg2.peek(), arg3.getBuffer(RenderLayers.getEntityBlockLayer(arg)), arg, lv2, f, g, h, i, j);
                break;
            }
            case ENTITYBLOCK_ANIMATED: {
                BuiltinModelItemRenderer.INSTANCE.render(new ItemStack(arg.getBlock()), arg2, arg3, i, j);
            }
        }
    }

    @Override
    public void apply(ResourceManager arg) {
        this.fluidRenderer.onResourceReload();
    }
}

