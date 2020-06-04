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
import net.minecraft.block.SpreadableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock
extends SpreadableBlock {
    public MyceliumBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        super.randomDisplayTick(arg, arg2, arg3, random);
        if (random.nextInt(10) == 0) {
            arg2.addParticle(ParticleTypes.MYCELIUM, (double)arg3.getX() + random.nextDouble(), (double)arg3.getY() + 1.1, (double)arg3.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }
}

