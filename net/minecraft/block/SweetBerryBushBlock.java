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
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SweetBerryBushBlock
extends PlantBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_3;
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public SweetBerryBushBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(Items.SWEET_BERRIES);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(AGE) == 0) {
            return SMALL_SHAPE;
        }
        if (arg.get(AGE) < 3) {
            return LARGE_SHAPE;
        }
        return super.getOutlineShape(arg, arg2, arg3, arg4);
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        int i = arg.get(AGE);
        if (i < 3 && random.nextInt(5) == 0 && arg2.getBaseLightLevel(arg3.up(), 0) >= 9) {
            arg2.setBlockState(arg3, (BlockState)arg.with(AGE, i + 1), 2);
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (!(arg4 instanceof LivingEntity) || arg4.getType() == EntityType.FOX || arg4.getType() == EntityType.BEE) {
            return;
        }
        arg4.slowMovement(arg, new Vec3d(0.8f, 0.75, 0.8f));
        if (!(arg2.isClient || arg.get(AGE) <= 0 || arg4.lastRenderX == arg4.getX() && arg4.lastRenderZ == arg4.getZ())) {
            double d = Math.abs(arg4.getX() - arg4.lastRenderX);
            double e = Math.abs(arg4.getZ() - arg4.lastRenderZ);
            if (d >= (double)0.003f || e >= (double)0.003f) {
                arg4.damage(DamageSource.SWEET_BERRY_BUSH, 1.0f);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        boolean bl;
        int i = arg.get(AGE);
        boolean bl2 = bl = i == 3;
        if (!bl && arg4.getStackInHand(arg5).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        }
        if (i > 1) {
            int j = 1 + arg2.random.nextInt(2);
            SweetBerryBushBlock.dropStack(arg2, arg3, new ItemStack(Items.SWEET_BERRIES, j + (bl ? 1 : 0)));
            arg2.playSound(null, arg3, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0f, 0.8f + arg2.random.nextFloat() * 0.4f);
            arg2.setBlockState(arg3, (BlockState)arg.with(AGE, 1), 2);
            return ActionResult.SUCCESS;
        }
        return super.onUse(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return arg3.get(AGE) < 3;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        int i = Math.min(3, arg3.get(AGE) + 1);
        arg.setBlockState(arg2, (BlockState)arg3.with(AGE, i), 2);
    }
}

