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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CryingObsidianBlock
extends Block {
    public CryingObsidianBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (random.nextInt(5) != 0) {
            return;
        }
        Direction lv = Direction.random(random);
        if (lv == Direction.UP) {
            return;
        }
        BlockPos lv2 = arg3.offset(lv);
        BlockState lv3 = arg2.getBlockState(lv2);
        if (arg.isOpaque() && lv3.isSideSolidFullSquare(arg2, lv2, lv.getOpposite())) {
            return;
        }
        double d = lv.getOffsetX() == 0 ? random.nextDouble() : 0.5 + (double)lv.getOffsetX() * 0.6;
        double e = lv.getOffsetY() == 0 ? random.nextDouble() : 0.5 + (double)lv.getOffsetY() * 0.6;
        double f = lv.getOffsetZ() == 0 ? random.nextDouble() : 0.5 + (double)lv.getOffsetZ() * 0.6;
        arg2.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)arg3.getX() + d, (double)arg3.getY() + e, (double)arg3.getZ() + f, 0.0, 0.0, 0.0);
    }
}

