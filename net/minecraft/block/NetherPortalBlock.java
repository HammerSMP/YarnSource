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
import net.minecraft.AreaHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class NetherPortalBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public NetherPortalBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        switch (arg.get(AXIS)) {
            case Z: {
                return Z_SHAPE;
            }
        }
        return X_SHAPE;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.getDimension().isNatural() && arg2.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < arg2.getDifficulty().getId()) {
            ZombifiedPiglinEntity lv;
            while (arg2.getBlockState(arg3).isOf(this)) {
                arg3 = arg3.down();
            }
            if (arg2.getBlockState(arg3).allowsSpawning(arg2, arg3, EntityType.ZOMBIFIED_PIGLIN) && (lv = EntityType.ZOMBIFIED_PIGLIN.spawn(arg2, null, null, null, arg3.up(), SpawnReason.STRUCTURE, false, false)) != null) {
                lv.method_30229();
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        boolean bl;
        Direction.Axis lv = arg2.getAxis();
        Direction.Axis lv2 = arg.get(AXIS);
        boolean bl2 = bl = lv2 != lv && lv.isHorizontal();
        if (bl || arg3.isOf(this) || new AreaHelper(arg4, arg5, lv2).wasAlreadyValid()) {
            return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (!arg4.hasVehicle() && !arg4.hasPassengers() && arg4.canUsePortals()) {
            arg4.setInNetherPortal(arg3);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (random.nextInt(100) == 0) {
            arg2.playSound((double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)arg3.getX() + random.nextDouble();
            double e = (double)arg3.getY() + random.nextDouble();
            double f = (double)arg3.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j = ((double)random.nextFloat() - 0.5) * 0.5;
            int k = random.nextInt(2) * 2 - 1;
            if (arg2.getBlockState(arg3.west()).isOf(this) || arg2.getBlockState(arg3.east()).isOf(this)) {
                f = (double)arg3.getZ() + 0.5 + 0.25 * (double)k;
                j = random.nextFloat() * 2.0f * (float)k;
            } else {
                d = (double)arg3.getX() + 0.5 + 0.25 * (double)k;
                g = random.nextFloat() * 2.0f * (float)k;
            }
            arg2.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (arg.get(AXIS)) {
                    case X: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.X);
                    }
                }
                return arg;
            }
        }
        return arg;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AXIS);
    }
}

