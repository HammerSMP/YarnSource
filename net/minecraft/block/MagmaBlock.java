/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MagmaBlock
extends Block {
    public MagmaBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
        if (!arg3.isFireImmune() && arg3 instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)arg3)) {
            arg3.damage(DamageSource.HOT_FLOOR, 1.0f);
        }
        super.onSteppedOn(arg, arg2, arg3);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BubbleColumnBlock.update(arg2, arg3.up(), true);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.UP && arg3.isOf(Blocks.WATER)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 20);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockPos lv = arg3.up();
        if (arg2.getFluidState(arg3).matches(FluidTags.WATER)) {
            arg2.playSound(null, arg3, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (arg2.random.nextFloat() - arg2.random.nextFloat()) * 0.8f);
            arg2.spawnParticles(ParticleTypes.LARGE_SMOKE, (double)lv.getX() + 0.5, (double)lv.getY() + 0.25, (double)lv.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0);
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        arg2.getBlockTickScheduler().schedule(arg3, this, 20);
    }
}

