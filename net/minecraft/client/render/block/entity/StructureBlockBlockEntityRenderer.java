/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer
extends BlockEntityRenderer<StructureBlockBlockEntity> {
    public StructureBlockBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(StructureBlockBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        double af;
        double ae;
        double ad;
        double ac;
        double p;
        double o;
        if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
            return;
        }
        BlockPos lv = arg.getOffset();
        BlockPos lv2 = arg.getSize();
        if (lv2.getX() < 1 || lv2.getY() < 1 || lv2.getZ() < 1) {
            return;
        }
        if (arg.getMode() != StructureBlockMode.SAVE && arg.getMode() != StructureBlockMode.LOAD) {
            return;
        }
        double d = lv.getX();
        double e = lv.getZ();
        double g = lv.getY();
        double h = g + (double)lv2.getY();
        switch (arg.getMirror()) {
            case LEFT_RIGHT: {
                double k = lv2.getX();
                double l = -lv2.getZ();
                break;
            }
            case FRONT_BACK: {
                double m = -lv2.getX();
                double n = lv2.getZ();
                break;
            }
            default: {
                o = lv2.getX();
                p = lv2.getZ();
            }
        }
        switch (arg.getRotation()) {
            case CLOCKWISE_90: {
                double q = p < 0.0 ? d : d + 1.0;
                double r = o < 0.0 ? e + 1.0 : e;
                double s = q - p;
                double t = r + o;
                break;
            }
            case CLOCKWISE_180: {
                double u = o < 0.0 ? d : d + 1.0;
                double v = p < 0.0 ? e : e + 1.0;
                double w = u - o;
                double x = v - p;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                double y = p < 0.0 ? d + 1.0 : d;
                double z = o < 0.0 ? e : e + 1.0;
                double aa = y + p;
                double ab = z - o;
                break;
            }
            default: {
                ac = o < 0.0 ? d + 1.0 : d;
                ad = p < 0.0 ? e + 1.0 : e;
                ae = ac + o;
                af = ad + p;
            }
        }
        float ag = 1.0f;
        float ah = 0.9f;
        float ai = 0.5f;
        VertexConsumer lv3 = arg3.getBuffer(RenderLayer.getLines());
        if (arg.getMode() == StructureBlockMode.SAVE || arg.shouldShowBoundingBox()) {
            WorldRenderer.drawBox(arg2, lv3, ac, g, ad, ae, h, af, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        }
        if (arg.getMode() == StructureBlockMode.SAVE && arg.shouldShowAir()) {
            this.method_3585(arg, lv3, lv, true, arg2);
            this.method_3585(arg, lv3, lv, false, arg2);
        }
    }

    private void method_3585(StructureBlockBlockEntity arg, VertexConsumer arg2, BlockPos arg3, boolean bl, MatrixStack arg4) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getPos();
        BlockPos lv3 = lv2.add(arg3);
        for (BlockPos lv4 : BlockPos.iterate(lv3, lv3.add(arg.getSize()).add(-1, -1, -1))) {
            BlockState lv5 = lv.getBlockState(lv4);
            boolean bl2 = lv5.isAir();
            boolean bl3 = lv5.isOf(Blocks.STRUCTURE_VOID);
            if (!bl2 && !bl3) continue;
            float f = bl2 ? 0.05f : 0.0f;
            double d = (float)(lv4.getX() - lv2.getX()) + 0.45f - f;
            double e = (float)(lv4.getY() - lv2.getY()) + 0.45f - f;
            double g = (float)(lv4.getZ() - lv2.getZ()) + 0.45f - f;
            double h = (float)(lv4.getX() - lv2.getX()) + 0.55f + f;
            double i = (float)(lv4.getY() - lv2.getY()) + 0.55f + f;
            double j = (float)(lv4.getZ() - lv2.getZ()) + 0.55f + f;
            if (bl) {
                WorldRenderer.drawBox(arg4, arg2, d, e, g, h, i, j, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                continue;
            }
            if (bl2) {
                WorldRenderer.drawBox(arg4, arg2, d, e, g, h, i, j, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            WorldRenderer.drawBox(arg4, arg2, d, e, g, h, i, j, 1.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.25f, 0.25f);
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(StructureBlockBlockEntity arg) {
        return true;
    }
}

