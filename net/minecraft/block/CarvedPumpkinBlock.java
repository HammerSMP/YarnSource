/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Wearable;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CarvedPumpkinBlock
extends HorizontalFacingBlock
implements Wearable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    @Nullable
    private BlockPattern snowGolemDispenserPattern;
    @Nullable
    private BlockPattern snowGolemPattern;
    @Nullable
    private BlockPattern ironGolemDispenserPattern;
    @Nullable
    private BlockPattern ironGolemPattern;
    private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE = arg -> arg != null && (arg.isOf(Blocks.CARVED_PUMPKIN) || arg.isOf(Blocks.JACK_O_LANTERN));

    protected CarvedPumpkinBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        this.trySpawnEntity(arg2, arg3);
    }

    public boolean canDispense(WorldView arg, BlockPos arg2) {
        return this.getSnowGolemDispenserPattern().searchAround(arg, arg2) != null || this.getIronGolemDispenserPattern().searchAround(arg, arg2) != null;
    }

    private void trySpawnEntity(World arg, BlockPos arg2) {
        block9: {
            BlockPattern.Result lv;
            block8: {
                lv = this.getSnowGolemPattern().searchAround(arg, arg2);
                if (lv == null) break block8;
                for (int i = 0; i < this.getSnowGolemPattern().getHeight(); ++i) {
                    CachedBlockPosition lv2 = lv.translate(0, i, 0);
                    arg.setBlockState(lv2.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                    arg.syncWorldEvent(2001, lv2.getBlockPos(), Block.getRawIdFromState(lv2.getBlockState()));
                }
                SnowGolemEntity lv3 = EntityType.SNOW_GOLEM.create(arg);
                BlockPos lv4 = lv.translate(0, 2, 0).getBlockPos();
                lv3.refreshPositionAndAngles((double)lv4.getX() + 0.5, (double)lv4.getY() + 0.05, (double)lv4.getZ() + 0.5, 0.0f, 0.0f);
                arg.spawnEntity(lv3);
                for (ServerPlayerEntity lv5 : arg.getNonSpectatingEntities(ServerPlayerEntity.class, lv3.getBoundingBox().expand(5.0))) {
                    Criteria.SUMMONED_ENTITY.trigger(lv5, lv3);
                }
                for (int j = 0; j < this.getSnowGolemPattern().getHeight(); ++j) {
                    CachedBlockPosition lv6 = lv.translate(0, j, 0);
                    arg.updateNeighbors(lv6.getBlockPos(), Blocks.AIR);
                }
                break block9;
            }
            lv = this.getIronGolemPattern().searchAround(arg, arg2);
            if (lv == null) break block9;
            for (int k = 0; k < this.getIronGolemPattern().getWidth(); ++k) {
                for (int l = 0; l < this.getIronGolemPattern().getHeight(); ++l) {
                    CachedBlockPosition lv7 = lv.translate(k, l, 0);
                    arg.setBlockState(lv7.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                    arg.syncWorldEvent(2001, lv7.getBlockPos(), Block.getRawIdFromState(lv7.getBlockState()));
                }
            }
            BlockPos lv8 = lv.translate(1, 2, 0).getBlockPos();
            IronGolemEntity lv9 = EntityType.IRON_GOLEM.create(arg);
            lv9.setPlayerCreated(true);
            lv9.refreshPositionAndAngles((double)lv8.getX() + 0.5, (double)lv8.getY() + 0.05, (double)lv8.getZ() + 0.5, 0.0f, 0.0f);
            arg.spawnEntity(lv9);
            for (ServerPlayerEntity lv10 : arg.getNonSpectatingEntities(ServerPlayerEntity.class, lv9.getBoundingBox().expand(5.0))) {
                Criteria.SUMMONED_ENTITY.trigger(lv10, lv9);
            }
            for (int m = 0; m < this.getIronGolemPattern().getWidth(); ++m) {
                for (int n = 0; n < this.getIronGolemPattern().getHeight(); ++n) {
                    CachedBlockPosition lv11 = lv.translate(m, n, 0);
                    arg.updateNeighbors(lv11.getBlockPos(), Blocks.AIR);
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING);
    }

    private BlockPattern getSnowGolemDispenserPattern() {
        if (this.snowGolemDispenserPattern == null) {
            this.snowGolemDispenserPattern = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemDispenserPattern;
    }

    private BlockPattern getSnowGolemPattern() {
        if (this.snowGolemPattern == null) {
            this.snowGolemPattern = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemPattern;
    }

    private BlockPattern getIronGolemDispenserPattern() {
        if (this.ironGolemDispenserPattern == null) {
            this.ironGolemDispenserPattern = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.ironGolemDispenserPattern;
    }

    private BlockPattern getIronGolemPattern() {
        if (this.ironGolemPattern == null) {
            this.ironGolemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.ironGolemPattern;
    }
}

