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
import net.minecraft.block.Blocks;
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
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        this.tryBreakEgg(world, pos, entity, 100);
        super.onSteppedOn(world, pos, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, pos, entity, 3);
        }
        super.onLandedUpon(world, pos, entity, distance);
    }

    private void tryBreakEgg(World world, BlockPos arg2, Entity entity, int inverseChance) {
        BlockState lv;
        if (!this.breaksEgg(world, entity)) {
            return;
        }
        if (!world.isClient && world.random.nextInt(inverseChance) == 0 && (lv = world.getBlockState(arg2)).isOf(Blocks.TURTLE_EGG)) {
            this.breakEgg(world, arg2, lv);
        }
    }

    private void breakEgg(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        int i = state.get(EGGS);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, (BlockState)state.with(EGGS, i - 1), 2);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress(world) && TurtleEggBlock.isSand(world, pos)) {
            int i = state.get(HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.setBlockState(pos, (BlockState)state.with(HATCH, i + 1), 2);
            } else {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.removeBlock(pos, false);
                for (int j = 0; j < state.get(EGGS); ++j) {
                    world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
                    TurtleEntity lv = EntityType.TURTLE.create(world);
                    lv.setBreedingAge(-24000);
                    lv.setHomePos(pos);
                    lv.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, pos.getY(), (double)pos.getZ() + 0.3, 0.0f, 0.0f);
                    world.spawnEntity(lv);
                }
            }
        }
    }

    public static boolean isSand(BlockView arg, BlockPos arg2) {
        return TurtleEggBlock.method_29952(arg, arg2.down());
    }

    public static boolean method_29952(BlockView arg, BlockPos arg2) {
        return arg.getBlockState(arg2).isIn(BlockTags.SAND);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (TurtleEggBlock.isSand(world, pos) && !world.isClient) {
            world.syncWorldEvent(2005, pos, 0);
        }
    }

    private boolean shouldHatchProgress(World world) {
        float f = world.method_30274(1.0f);
        if ((double)f < 0.69 && (double)f > 0.65) {
            return true;
        }
        return world.random.nextInt(500) == 0;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        this.breakEgg(world, pos, state);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getStack().getItem() == this.asItem() && state.get(EGGS) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState lv = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (lv.isOf(this)) {
            return (BlockState)lv.with(EGGS, Math.min(4, lv.get(EGGS) + 1));
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(EGGS) > 1) {
            return LARGE_SHAPE;
        }
        return SMALL_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean breaksEgg(World arg, Entity arg2) {
        if (arg2 instanceof TurtleEntity || arg2 instanceof BatEntity) {
            return false;
        }
        if (arg2 instanceof LivingEntity) {
            return arg2 instanceof PlayerEntity || arg.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        }
        return false;
    }
}

