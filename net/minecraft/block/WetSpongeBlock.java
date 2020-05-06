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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WetSpongeBlock
extends Block {
    protected WetSpongeBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg2.getDimension().doesWaterVaporize()) {
            arg2.setBlockState(arg3, Blocks.SPONGE.getDefaultState(), 3);
            arg2.syncWorldEvent(2009, arg3, 0);
            arg2.playSound(null, arg3, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, (1.0f + arg2.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        Direction lv = Direction.random(random);
        if (lv == Direction.UP) {
            return;
        }
        BlockPos lv2 = arg3.offset(lv);
        BlockState lv3 = arg2.getBlockState(lv2);
        if (arg.isOpaque() && lv3.isSideSolidFullSquare(arg2, lv2, lv.getOpposite())) {
            return;
        }
        double d = arg3.getX();
        double e = arg3.getY();
        double f = arg3.getZ();
        if (lv == Direction.DOWN) {
            e -= 0.05;
            d += random.nextDouble();
            f += random.nextDouble();
        } else {
            e += random.nextDouble() * 0.8;
            if (lv.getAxis() == Direction.Axis.X) {
                f += random.nextDouble();
                d = lv == Direction.EAST ? (d += 1.1) : (d += 0.05);
            } else {
                d += random.nextDouble();
                f = lv == Direction.SOUTH ? (f += 1.1) : (f += 0.05);
            }
        }
        arg2.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
    }
}

