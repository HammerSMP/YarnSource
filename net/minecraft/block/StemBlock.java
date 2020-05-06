/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StemBlock
extends PlantBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_7;
    protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
    private final GourdBlock gourdBlock;

    protected StemBlock(GourdBlock arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.gourdBlock = arg;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return AGE_TO_SHAPE[arg.get(AGE)];
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isOf(Blocks.FARMLAND);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.getBaseLightLevel(arg3, 0) < 9) {
            return;
        }
        float f = CropBlock.getAvailableMoisture(this, arg2, arg3);
        if (random.nextInt((int)(25.0f / f) + 1) == 0) {
            int i = arg.get(AGE);
            if (i < 7) {
                arg = (BlockState)arg.with(AGE, i + 1);
                arg2.setBlockState(arg3, arg, 2);
            } else {
                Direction lv = Direction.Type.HORIZONTAL.random(random);
                BlockPos lv2 = arg3.offset(lv);
                BlockState lv3 = arg2.getBlockState(lv2.down());
                if (arg2.getBlockState(lv2).isAir() && (lv3.isOf(Blocks.FARMLAND) || lv3.isOf(Blocks.DIRT) || lv3.isOf(Blocks.COARSE_DIRT) || lv3.isOf(Blocks.PODZOL) || lv3.isOf(Blocks.GRASS_BLOCK))) {
                    arg2.setBlockState(lv2, this.gourdBlock.getDefaultState());
                    arg2.setBlockState(arg3, (BlockState)this.gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, lv));
                }
            }
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    protected Item getPickItem() {
        if (this.gourdBlock == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        if (this.gourdBlock == Blocks.MELON) {
            return Items.MELON_SEEDS;
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        Item lv = this.getPickItem();
        return lv == null ? ItemStack.EMPTY : new ItemStack(lv);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return arg3.get(AGE) != 7;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        int i = Math.min(7, arg3.get(AGE) + MathHelper.nextInt(arg.random, 2, 5));
        BlockState lv = (BlockState)arg3.with(AGE, i);
        arg.setBlockState(arg2, lv, 2);
        if (i == 7) {
            lv.randomTick(arg, arg2, arg.random);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    public GourdBlock getGourdBlock() {
        return this.gourdBlock;
    }
}

