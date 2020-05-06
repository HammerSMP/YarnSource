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
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SmokerBlock
extends AbstractFurnaceBlock {
    protected SmokerBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new SmokerBlockEntity();
    }

    @Override
    protected void openScreen(World arg, BlockPos arg2, PlayerEntity arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof SmokerBlockEntity) {
            arg3.openHandledScreen((NamedScreenHandlerFactory)((Object)lv));
            arg3.incrementStat(Stats.INTERACT_WITH_SMOKER);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg.get(LIT).booleanValue()) {
            return;
        }
        double d = (double)arg3.getX() + 0.5;
        double e = arg3.getY();
        double f = (double)arg3.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            arg2.playSound(d, e, f, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
        arg2.addParticle(ParticleTypes.SMOKE, d, e + 1.1, f, 0.0, 0.0, 0.0);
    }
}

