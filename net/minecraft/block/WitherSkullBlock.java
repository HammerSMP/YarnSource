/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkullBlock
extends SkullBlock {
    @Nullable
    private static BlockPattern witherBossPattern;
    @Nullable
    private static BlockPattern witherDispenserPattern;

    protected WitherSkullBlock(AbstractBlock.Settings arg) {
        super(SkullBlock.Type.WITHER_SKELETON, arg);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        super.onPlaced(arg, arg2, arg3, arg4, arg5);
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof SkullBlockEntity) {
            WitherSkullBlock.onPlaced(arg, arg2, (SkullBlockEntity)lv);
        }
    }

    public static void onPlaced(World arg, BlockPos arg2, SkullBlockEntity arg3) {
        boolean bl;
        if (arg.isClient) {
            return;
        }
        BlockState lv = arg3.getCachedState();
        boolean bl2 = bl = lv.isOf(Blocks.WITHER_SKELETON_SKULL) || lv.isOf(Blocks.WITHER_SKELETON_WALL_SKULL);
        if (!bl || arg2.getY() < 0 || arg.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        BlockPattern lv2 = WitherSkullBlock.getWitherBossPattern();
        BlockPattern.Result lv3 = lv2.searchAround(arg, arg2);
        if (lv3 == null) {
            return;
        }
        for (int i = 0; i < lv2.getWidth(); ++i) {
            for (int j = 0; j < lv2.getHeight(); ++j) {
                CachedBlockPosition lv4 = lv3.translate(i, j, 0);
                arg.setBlockState(lv4.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                arg.syncWorldEvent(2001, lv4.getBlockPos(), Block.getRawIdFromState(lv4.getBlockState()));
            }
        }
        WitherEntity lv5 = EntityType.WITHER.create(arg);
        BlockPos lv6 = lv3.translate(1, 2, 0).getBlockPos();
        lv5.refreshPositionAndAngles((double)lv6.getX() + 0.5, (double)lv6.getY() + 0.55, (double)lv6.getZ() + 0.5, lv3.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f, 0.0f);
        lv5.bodyYaw = lv3.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f;
        lv5.method_6885();
        for (ServerPlayerEntity lv7 : arg.getNonSpectatingEntities(ServerPlayerEntity.class, lv5.getBoundingBox().expand(50.0))) {
            Criteria.SUMMONED_ENTITY.trigger(lv7, lv5);
        }
        arg.spawnEntity(lv5);
        for (int k = 0; k < lv2.getWidth(); ++k) {
            for (int l = 0; l < lv2.getHeight(); ++l) {
                arg.updateNeighbors(lv3.translate(k, l, 0).getBlockPos(), Blocks.AIR);
            }
        }
    }

    public static boolean canDispense(World arg, BlockPos arg2, ItemStack arg3) {
        if (arg3.getItem() == Items.WITHER_SKELETON_SKULL && arg2.getY() >= 2 && arg.getDifficulty() != Difficulty.PEACEFUL && !arg.isClient) {
            return WitherSkullBlock.getWitherDispenserPattern().searchAround(arg, arg2) != null;
        }
        return false;
    }

    private static BlockPattern getWitherBossPattern() {
        if (witherBossPattern == null) {
            witherBossPattern = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', arg -> arg.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return witherBossPattern;
    }

    private static BlockPattern getWitherDispenserPattern() {
        if (witherDispenserPattern == null) {
            witherDispenserPattern = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', arg -> arg.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return witherDispenserPattern;
    }
}

