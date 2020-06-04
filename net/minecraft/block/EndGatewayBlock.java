/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndGatewayBlock
extends BlockWithEntity {
    protected EndGatewayBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new EndGatewayBlockEntity();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (!(lv instanceof EndGatewayBlockEntity)) {
            return;
        }
        int i = ((EndGatewayBlockEntity)lv).getDrawnSidesCount();
        for (int j = 0; j < i; ++j) {
            double d = (double)arg3.getX() + random.nextDouble();
            double e = (double)arg3.getY() + random.nextDouble();
            double f = (double)arg3.getZ() + random.nextDouble();
            double g = (random.nextDouble() - 0.5) * 0.5;
            double h = (random.nextDouble() - 0.5) * 0.5;
            double k = (random.nextDouble() - 0.5) * 0.5;
            int l = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
                f = (double)arg3.getZ() + 0.5 + 0.25 * (double)l;
                k = random.nextFloat() * 2.0f * (float)l;
            } else {
                d = (double)arg3.getX() + 0.5 + 0.25 * (double)l;
                g = random.nextFloat() * 2.0f * (float)l;
            }
            arg2.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, k);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBucketPlace(BlockState arg, Fluid arg2) {
        return false;
    }
}

