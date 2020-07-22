/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TargetBlock
extends Block {
    private static final IntProperty POWER = Properties.POWER;

    public TargetBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWER, 0));
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        int i = TargetBlock.trigger(world, state, hit, projectile);
        Entity lv = projectile.getOwner();
        if (lv instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv2 = (ServerPlayerEntity)lv;
            lv2.incrementStat(Stats.TARGET_HIT);
            Criteria.TARGET_HIT.trigger(lv2, projectile, hit.getPos(), i);
        }
    }

    private static int trigger(WorldAccess world, BlockState state, BlockHitResult arg3, Entity arg4) {
        int j;
        int i = TargetBlock.calculatePower(arg3, arg3.getPos());
        int n = j = arg4 instanceof PersistentProjectileEntity ? 20 : 8;
        if (!world.getBlockTickScheduler().isScheduled(arg3.getBlockPos(), state.getBlock())) {
            TargetBlock.setPower(world, state, i, arg3.getBlockPos(), j);
        }
        return i;
    }

    private static int calculatePower(BlockHitResult arg, Vec3d pos) {
        double i;
        Direction lv = arg.getSide();
        double d = Math.abs(MathHelper.fractionalPart(pos.x) - 0.5);
        double e = Math.abs(MathHelper.fractionalPart(pos.y) - 0.5);
        double f = Math.abs(MathHelper.fractionalPart(pos.z) - 0.5);
        Direction.Axis lv2 = lv.getAxis();
        if (lv2 == Direction.Axis.Y) {
            double g = Math.max(d, f);
        } else if (lv2 == Direction.Axis.Z) {
            double h = Math.max(d, e);
        } else {
            i = Math.max(e, f);
        }
        return Math.max(1, MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - i) / 0.5, 0.0, 1.0)));
    }

    private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlockState(pos, (BlockState)state.with(POWER, power), 3);
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWER) != 0) {
            world.setBlockState(pos, (BlockState)state.with(POWER, 0), 3);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient() || state.isOf(oldState.getBlock())) {
            return;
        }
        if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            world.setBlockState(pos, (BlockState)state.with(POWER, 0), 18);
        }
    }
}

