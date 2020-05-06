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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TargetBlock
extends Block {
    private static final IntProperty POWER = Properties.POWER;

    public TargetBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWER, 0));
    }

    @Override
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
        int i = TargetBlock.trigger(arg, arg2, arg3, arg4);
        Entity lv = arg4.getOwner();
        if (lv instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv2 = (ServerPlayerEntity)lv;
            lv2.incrementStat(Stats.TARGET_HIT);
            Criteria.TARGET_HIT.trigger(lv2, arg4, arg3.getPos(), i);
        }
    }

    private static int trigger(IWorld arg, BlockState arg2, BlockHitResult arg3, Entity arg4) {
        int j;
        int i = TargetBlock.calculatePower(arg3, arg3.getPos());
        int n = j = arg4 instanceof PersistentProjectileEntity ? 20 : 8;
        if (!arg.getBlockTickScheduler().isScheduled(arg3.getBlockPos(), arg2.getBlock())) {
            TargetBlock.setPower(arg, arg2, i, arg3.getBlockPos(), j);
        }
        return i;
    }

    private static int calculatePower(BlockHitResult arg, Vec3d arg2) {
        double i;
        Direction lv = arg.getSide();
        double d = Math.abs(MathHelper.fractionalPart(arg2.x) - 0.5);
        double e = Math.abs(MathHelper.fractionalPart(arg2.y) - 0.5);
        double f = Math.abs(MathHelper.fractionalPart(arg2.z) - 0.5);
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

    private static void setPower(IWorld arg, BlockState arg2, int i, BlockPos arg3, int j) {
        arg.setBlockState(arg3, (BlockState)arg2.with(POWER, i), 3);
        arg.getBlockTickScheduler().schedule(arg3, arg2.getBlock(), j);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg.get(POWER) != 0) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWER, 0), 3);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.get(POWER);
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(POWER);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg2.isClient() || arg.isOf(arg4.getBlock())) {
            return;
        }
        if (arg.get(POWER) > 0 && !arg2.getBlockTickScheduler().isScheduled(arg3, this)) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWER, 0), 18);
        }
    }
}

