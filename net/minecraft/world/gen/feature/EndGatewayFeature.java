/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndGatewayFeature
extends Feature<EndGatewayFeatureConfig> {
    public EndGatewayFeature(Codec<EndGatewayFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg42, EndGatewayFeatureConfig arg5) {
        for (BlockPos lv : BlockPos.iterate(arg42.add(-1, -2, -1), arg42.add(1, 2, 1))) {
            boolean bl4;
            boolean bl = lv.getX() == arg42.getX();
            boolean bl2 = lv.getY() == arg42.getY();
            boolean bl3 = lv.getZ() == arg42.getZ();
            boolean bl5 = bl4 = Math.abs(lv.getY() - arg42.getY()) == 2;
            if (bl && bl2 && bl3) {
                BlockPos lv2 = lv.toImmutable();
                this.setBlockState(arg, lv2, Blocks.END_GATEWAY.getDefaultState());
                arg5.getExitPos().ifPresent(arg4 -> {
                    BlockEntity lv = arg.getBlockEntity(lv2);
                    if (lv instanceof EndGatewayBlockEntity) {
                        EndGatewayBlockEntity lv2 = (EndGatewayBlockEntity)lv;
                        lv2.setExitPortalPos((BlockPos)arg4, arg5.isExact());
                        lv.markDirty();
                    }
                });
                continue;
            }
            if (bl2) {
                this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
                continue;
            }
            if (bl4 && bl && bl3) {
                this.setBlockState(arg, lv, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            if (!bl && !bl3 || bl4) {
                this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
                continue;
            }
            this.setBlockState(arg, lv, Blocks.BEDROCK.getDefaultState());
        }
        return true;
    }
}

