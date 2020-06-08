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
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RepeaterBlock
extends AbstractRedstoneGateBlock {
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final IntProperty DELAY = Properties.DELAY;

    protected RepeaterBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(DELAY, 1)).with(LOCKED, false)).with(POWERED, false));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (!arg4.abilities.allowModifyWorld) {
            return ActionResult.PASS;
        }
        arg2.setBlockState(arg3, (BlockState)arg.cycle(DELAY), 3);
        return ActionResult.success(arg2.isClient);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState arg) {
        return arg.get(DELAY) * 2;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = super.getPlacementState(arg);
        return (BlockState)lv.with(LOCKED, this.isLocked(arg.getWorld(), arg.getBlockPos(), lv));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg4.isClient() && arg2.getAxis() != arg.get(FACING).getAxis()) {
            return (BlockState)arg.with(LOCKED, this.isLocked(arg4, arg5, arg));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean isLocked(WorldView arg, BlockPos arg2, BlockState arg3) {
        return this.getMaxInputLevelSides(arg, arg2, arg3) > 0;
    }

    @Override
    protected boolean isValidInput(BlockState arg) {
        return RepeaterBlock.isRedstoneGate(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg.get(POWERED).booleanValue()) {
            return;
        }
        Direction lv = arg.get(FACING);
        double d = (double)arg3.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)arg3.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)arg3.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        float g = -5.0f;
        if (random.nextBoolean()) {
            g = arg.get(DELAY) * 2 - 1;
        }
        double h = (g /= 16.0f) * (float)lv.getOffsetX();
        double i = g * (float)lv.getOffsetZ();
        arg2.addParticle(DustParticleEffect.RED, d + h, e, f + i, 0.0, 0.0, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, DELAY, LOCKED, POWERED);
    }
}

