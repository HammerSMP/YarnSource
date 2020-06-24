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
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CropBlock
extends PlantBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_7;
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected CropBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return AGE_TO_SHAPE[arg.get(this.getAgeProperty())];
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isOf(Blocks.FARMLAND);
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState arg) {
        return arg.get(this.getAgeProperty());
    }

    public BlockState withAge(int i) {
        return (BlockState)this.getDefaultState().with(this.getAgeProperty(), i);
    }

    public boolean isMature(BlockState arg) {
        return arg.get(this.getAgeProperty()) >= this.getMaxAge();
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return !this.isMature(arg);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        float f;
        int i;
        if (arg2.getBaseLightLevel(arg3, 0) >= 9 && (i = this.getAge(arg)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = CropBlock.getAvailableMoisture(this, arg2, arg3))) + 1) == 0) {
            arg2.setBlockState(arg3, this.withAge(i + 1), 2);
        }
    }

    public void applyGrowth(World arg, BlockPos arg2, BlockState arg3) {
        int j;
        int i = this.getAge(arg3) + this.getGrowthAmount(arg);
        if (i > (j = this.getMaxAge())) {
            i = j;
        }
        arg.setBlockState(arg2, this.withAge(i), 2);
    }

    protected int getGrowthAmount(World arg) {
        return MathHelper.nextInt(arg.random, 2, 5);
    }

    protected static float getAvailableMoisture(Block arg, BlockView arg2, BlockPos arg3) {
        boolean bl2;
        float f = 1.0f;
        BlockPos lv = arg3.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState lv2 = arg2.getBlockState(lv.add(i, 0, j));
                if (lv2.isOf(Blocks.FARMLAND)) {
                    g = 1.0f;
                    if (lv2.get(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos lv3 = arg3.north();
        BlockPos lv4 = arg3.south();
        BlockPos lv5 = arg3.west();
        BlockPos lv6 = arg3.east();
        boolean bl = arg == arg2.getBlockState(lv5).getBlock() || arg == arg2.getBlockState(lv6).getBlock();
        boolean bl3 = bl2 = arg == arg2.getBlockState(lv3).getBlock() || arg == arg2.getBlockState(lv4).getBlock();
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl32;
            boolean bl4 = bl32 = arg == arg2.getBlockState(lv5.north()).getBlock() || arg == arg2.getBlockState(lv6.north()).getBlock() || arg == arg2.getBlockState(lv6.south()).getBlock() || arg == arg2.getBlockState(lv5.south()).getBlock();
            if (bl32) {
                f /= 2.0f;
            }
        }
        return f;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return (arg2.getBaseLightLevel(arg3, 0) >= 8 || arg2.isSkyVisible(arg3)) && super.canPlaceAt(arg, arg2, arg3);
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg4 instanceof RavagerEntity && arg2.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            arg2.breakBlock(arg3, true, arg4);
        }
        super.onEntityCollision(arg, arg2, arg3, arg4);
    }

    @Environment(value=EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(this.getSeedsItem());
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        return !this.isMature(arg3);
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        this.applyGrowth(arg, arg2, arg3);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }
}

