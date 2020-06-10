/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TurtleEggBlock
extends Block {
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntProperty HATCH = Properties.HATCH;
    public static final IntProperty EGGS = Properties.EGGS;

    public TurtleEggBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HATCH, 0)).with(EGGS, 1));
    }

    @Override
    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
        this.tryBreakEgg(arg, arg2, arg3, 100);
        super.onSteppedOn(arg, arg2, arg3);
    }

    @Override
    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        if (!(arg3 instanceof ZombieEntity)) {
            this.tryBreakEgg(arg, arg2, arg3, 3);
        }
        super.onLandedUpon(arg, arg2, arg3, f);
    }

    private void tryBreakEgg(World arg, BlockPos arg2, Entity arg3, int i) {
        if (!this.breaksEgg(arg, arg3)) {
            return;
        }
        if (!arg.isClient && arg.random.nextInt(i) == 0) {
            this.breakEgg(arg, arg2, arg.getBlockState(arg2));
        }
    }

    private void breakEgg(World arg, BlockPos arg2, BlockState arg3) {
        arg.playSound(null, arg2, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + arg.random.nextFloat() * 0.2f);
        int i = arg3.get(EGGS);
        if (i <= 1) {
            arg.breakBlock(arg2, false);
        } else {
            arg.setBlockState(arg2, (BlockState)arg3.with(EGGS, i - 1), 2);
            arg.syncWorldEvent(2001, arg2, Block.getRawIdFromState(arg3));
        }
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (this.shouldHatchProgress(arg2) && TurtleEggBlock.isSand(arg2, arg3)) {
            int i = arg.get(HATCH);
            if (i < 2) {
                arg2.playSound(null, arg3, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                arg2.setBlockState(arg3, (BlockState)arg.with(HATCH, i + 1), 2);
            } else {
                arg2.playSound(null, arg3, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                arg2.removeBlock(arg3, false);
                for (int j = 0; j < arg.get(EGGS); ++j) {
                    arg2.syncWorldEvent(2001, arg3, Block.getRawIdFromState(arg));
                    TurtleEntity lv = EntityType.TURTLE.create(arg2);
                    lv.setBreedingAge(-24000);
                    lv.setHomePos(arg3);
                    lv.refreshPositionAndAngles((double)arg3.getX() + 0.3 + (double)j * 0.2, arg3.getY(), (double)arg3.getZ() + 0.3, 0.0f, 0.0f);
                    arg2.spawnEntity(lv);
                }
            }
        }
    }

    public static boolean isSand(BlockView arg, BlockPos arg2) {
        return TurtleEggBlock.method_29952(arg, arg2.down());
    }

    public static boolean method_29952(BlockView arg, BlockPos arg2) {
        return arg.getBlockState(arg2.down()).isIn(BlockTags.SAND);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (TurtleEggBlock.isSand(arg2, arg3) && !arg2.isClient) {
            arg2.syncWorldEvent(2005, arg3, 0);
        }
    }

    private boolean shouldHatchProgress(World arg) {
        float f = arg.getSkyAngle(1.0f);
        if ((double)f < 0.69 && (double)f > 0.65) {
            return true;
        }
        return arg.random.nextInt(500) == 0;
    }

    @Override
    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        super.afterBreak(arg, arg2, arg3, arg4, arg5, arg6);
        this.breakEgg(arg, arg3, arg4);
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        if (arg2.getStack().getItem() == this.asItem() && arg.get(EGGS) < 4) {
            return true;
        }
        return super.canReplace(arg, arg2);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos());
        if (lv.isOf(this)) {
            return (BlockState)lv.with(EGGS, Math.min(4, lv.get(EGGS) + 1));
        }
        return super.getPlacementState(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(EGGS) > 1) {
            return LARGE_SHAPE;
        }
        return SMALL_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(HATCH, EGGS);
    }

    private boolean breaksEgg(World arg, Entity arg2) {
        if (arg2 instanceof TurtleEntity || arg2 instanceof BatEntity) {
            return false;
        }
        if (arg2 instanceof LivingEntity) {
            return arg2 instanceof PlayerEntity || arg.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        }
        return false;
    }
}

