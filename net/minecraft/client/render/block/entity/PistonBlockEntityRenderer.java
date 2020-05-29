/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderer
extends BlockEntityRenderer<PistonBlockEntity> {
    private final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();

    public PistonBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(PistonBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        World lv = arg.getWorld();
        if (lv == null) {
            return;
        }
        BlockPos lv2 = arg.getPos().offset(arg.getMovementDirection().getOpposite());
        BlockState lv3 = arg.getPushedBlock();
        if (lv3.isAir() || arg.getProgress(f) >= 1.0f) {
            return;
        }
        BlockModelRenderer.enableBrightnessCache();
        arg2.push();
        arg2.translate(arg.getRenderOffsetX(f), arg.getRenderOffsetY(f), arg.getRenderOffsetZ(f));
        if (lv3.isOf(Blocks.PISTON_HEAD) && arg.getProgress(f) <= 4.0f) {
            lv3 = (BlockState)lv3.with(PistonHeadBlock.SHORT, arg.getProgress(f) <= 0.5f);
            this.method_3575(lv2, lv3, arg2, arg3, lv, false, j);
        } else if (arg.isSource() && !arg.isExtending()) {
            PistonType lv4 = lv3.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState lv5 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, lv4)).with(PistonHeadBlock.FACING, lv3.get(PistonBlock.FACING));
            lv5 = (BlockState)lv5.with(PistonHeadBlock.SHORT, arg.getProgress(f) >= 0.5f);
            this.method_3575(lv2, lv5, arg2, arg3, lv, false, j);
            BlockPos lv6 = lv2.offset(arg.getMovementDirection());
            arg2.pop();
            arg2.push();
            lv3 = (BlockState)lv3.with(PistonBlock.EXTENDED, true);
            this.method_3575(lv6, lv3, arg2, arg3, lv, true, j);
        } else {
            this.method_3575(lv2, lv3, arg2, arg3, lv, false, j);
        }
        arg2.pop();
        BlockModelRenderer.disableBrightnessCache();
    }

    private void method_3575(BlockPos arg, BlockState arg2, MatrixStack arg3, VertexConsumerProvider arg4, World arg5, boolean bl, int i) {
        RenderLayer lv = RenderLayers.method_29359(arg2);
        VertexConsumer lv2 = arg4.getBuffer(lv);
        this.manager.getModelRenderer().render(arg5, this.manager.getModel(arg2), arg2, arg, arg3, lv2, bl, new Random(), arg2.getRenderingSeed(arg), i);
    }
}

