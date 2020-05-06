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
import net.minecraft.block.ChorusPlantBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ChorusFlowerBlock
extends Block {
    public static final IntProperty AGE = Properties.AGE_5;
    private final ChorusPlantBlock plantBlock;

    protected ChorusFlowerBlock(ChorusPlantBlock arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.plantBlock = arg;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(AGE) < 5;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockPos lv = arg3.up();
        if (!arg2.isAir(lv) || lv.getY() >= 256) {
            return;
        }
        int i = arg.get(AGE);
        if (i >= 5) {
            return;
        }
        boolean bl = false;
        boolean bl2 = false;
        BlockState lv2 = arg2.getBlockState(arg3.down());
        Block lv3 = lv2.getBlock();
        if (lv3 == Blocks.END_STONE) {
            bl = true;
        } else if (lv3 == this.plantBlock) {
            int j = 1;
            for (int k = 0; k < 4; ++k) {
                Block lv4 = arg2.getBlockState(arg3.down(j + 1)).getBlock();
                if (lv4 == this.plantBlock) {
                    ++j;
                    continue;
                }
                if (lv4 != Blocks.END_STONE) break;
                bl2 = true;
                break;
            }
            if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
                bl = true;
            }
        } else if (lv2.isAir()) {
            bl = true;
        }
        if (bl && ChorusFlowerBlock.isSurroundedByAir(arg2, lv, null) && arg2.isAir(arg3.up(2))) {
            arg2.setBlockState(arg3, this.plantBlock.withConnectionProperties(arg2, arg3), 2);
            this.grow(arg2, lv, i);
        } else if (i < 4) {
            int l = random.nextInt(4);
            if (bl2) {
                ++l;
            }
            boolean bl3 = false;
            for (int m = 0; m < l; ++m) {
                Direction lv5 = Direction.Type.HORIZONTAL.random(random);
                BlockPos lv6 = arg3.offset(lv5);
                if (!arg2.isAir(lv6) || !arg2.isAir(lv6.down()) || !ChorusFlowerBlock.isSurroundedByAir(arg2, lv6, lv5.getOpposite())) continue;
                this.grow(arg2, lv6, i + 1);
                bl3 = true;
            }
            if (bl3) {
                arg2.setBlockState(arg3, this.plantBlock.withConnectionProperties(arg2, arg3), 2);
            } else {
                this.die(arg2, arg3);
            }
        } else {
            this.die(arg2, arg3);
        }
    }

    private void grow(World arg, BlockPos arg2, int i) {
        arg.setBlockState(arg2, (BlockState)this.getDefaultState().with(AGE, i), 2);
        arg.syncWorldEvent(1033, arg2, 0);
    }

    private void die(World arg, BlockPos arg2) {
        arg.setBlockState(arg2, (BlockState)this.getDefaultState().with(AGE, 5), 2);
        arg.syncWorldEvent(1034, arg2, 0);
    }

    private static boolean isSurroundedByAir(WorldView arg, BlockPos arg2, @Nullable Direction arg3) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            if (lv == arg3 || arg.isAir(arg2.offset(lv))) continue;
            return false;
        }
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 != Direction.UP && !arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.down());
        if (lv.getBlock() == this.plantBlock || lv.isOf(Blocks.END_STONE)) {
            return true;
        }
        if (!lv.isAir()) {
            return false;
        }
        boolean bl = false;
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockState lv3 = arg2.getBlockState(arg3.offset(lv2));
            if (lv3.isOf(this.plantBlock)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (lv3.isAir()) continue;
            return false;
        }
        return bl;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    public static void generate(IWorld arg, BlockPos arg2, Random random, int i) {
        arg.setBlockState(arg2, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).withConnectionProperties(arg, arg2), 2);
        ChorusFlowerBlock.generate(arg, arg2, random, arg2, i, 0);
    }

    private static void generate(IWorld arg, BlockPos arg2, Random random, BlockPos arg3, int i, int j) {
        ChorusPlantBlock lv = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
        int k = random.nextInt(4) + 1;
        if (j == 0) {
            ++k;
        }
        for (int l = 0; l < k; ++l) {
            BlockPos lv2 = arg2.up(l + 1);
            if (!ChorusFlowerBlock.isSurroundedByAir(arg, lv2, null)) {
                return;
            }
            arg.setBlockState(lv2, lv.withConnectionProperties(arg, lv2), 2);
            arg.setBlockState(lv2.down(), lv.withConnectionProperties(arg, lv2.down()), 2);
        }
        boolean bl = false;
        if (j < 4) {
            int m = random.nextInt(4);
            if (j == 0) {
                ++m;
            }
            for (int n = 0; n < m; ++n) {
                Direction lv3 = Direction.Type.HORIZONTAL.random(random);
                BlockPos lv4 = arg2.up(k).offset(lv3);
                if (Math.abs(lv4.getX() - arg3.getX()) >= i || Math.abs(lv4.getZ() - arg3.getZ()) >= i || !arg.isAir(lv4) || !arg.isAir(lv4.down()) || !ChorusFlowerBlock.isSurroundedByAir(arg, lv4, lv3.getOpposite())) continue;
                bl = true;
                arg.setBlockState(lv4, lv.withConnectionProperties(arg, lv4), 2);
                arg.setBlockState(lv4.offset(lv3.getOpposite()), lv.withConnectionProperties(arg, lv4.offset(lv3.getOpposite())), 2);
                ChorusFlowerBlock.generate(arg, lv4, random, arg3, i, j + 1);
            }
        }
        if (!bl) {
            arg.setBlockState(arg2.up(k), (BlockState)Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, 5), 2);
        }
    }

    @Override
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
        if (arg4.getType().isIn(EntityTypeTags.IMPACT_PROJECTILES)) {
            BlockPos lv = arg3.getBlockPos();
            arg.breakBlock(lv, true, arg4);
        }
    }
}

