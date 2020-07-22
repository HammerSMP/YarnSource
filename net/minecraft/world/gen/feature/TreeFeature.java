/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class TreeFeature
extends Feature<TreeFeatureConfig> {
    public TreeFeature(Codec<TreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return TreeFeature.canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
    }

    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
    }

    private static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Block lv = state.getBlock();
            return TreeFeature.isSoil(lv) || lv == Blocks.FARMLAND;
        });
    }

    private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Material lv = state.getMaterial();
            return lv == Material.REPLACEABLE_PLANT;
        });
    }

    public static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 19);
    }

    public static boolean canReplace(TestableWorld arg, BlockPos pos) {
        return TreeFeature.isAirOrLeaves(arg, pos) || TreeFeature.isReplaceablePlant(arg, pos) || TreeFeature.isWater(arg, pos);
    }

    private boolean generate(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config) {
        BlockPos lv2;
        int i = config.trunkPlacer.getHeight(random);
        int j = config.foliagePlacer.getHeight(random, i, config);
        int k = i - j;
        int l = config.foliagePlacer.getRadius(random, k);
        if (!config.skipFluidCheck) {
            int q;
            int m = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            int n = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            if (n - m > config.maxWaterDepth) {
                return false;
            }
            if (config.heightmap == Heightmap.Type.OCEAN_FLOOR) {
                int o = m;
            } else if (config.heightmap == Heightmap.Type.WORLD_SURFACE) {
                int p = n;
            } else {
                q = world.getTopPosition(config.heightmap, pos).getY();
            }
            BlockPos lv = new BlockPos(pos.getX(), q, pos.getZ());
        } else {
            lv2 = pos;
        }
        if (lv2.getY() < 1 || lv2.getY() + i + 1 > 256) {
            return false;
        }
        if (!TreeFeature.isDirtOrGrass(world, lv2.down())) {
            return false;
        }
        OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
        int r = this.method_29963(world, i, lv2, config);
        if (!(r >= i || optionalInt.isPresent() && r >= optionalInt.getAsInt())) {
            return false;
        }
        List<FoliagePlacer.TreeNode> list = config.trunkPlacer.generate(world, random, r, lv2, logPositions, box, config);
        list.forEach(arg4 -> arg.foliagePlacer.generate(world, random, config, r, (FoliagePlacer.TreeNode)arg4, j, l, leavesPositions, box));
        return true;
    }

    private int method_29963(TestableWorld arg, int i, BlockPos arg2, TreeFeatureConfig arg3) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int j = 0; j <= i + 1; ++j) {
            int k = arg3.minimumSize.method_27378(i, j);
            for (int l = -k; l <= k; ++l) {
                for (int m = -k; m <= k; ++m) {
                    lv.set(arg2, l, j, m);
                    if (TreeFeature.canTreeReplace(arg, lv) && (arg3.ignoreVines || !TreeFeature.isVine(arg, lv))) continue;
                    return j - 2;
                }
            }
        }
        return i;
    }

    @Override
    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    @Override
    public final boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, TreeFeatureConfig arg4) {
        HashSet set = Sets.newHashSet();
        HashSet set2 = Sets.newHashSet();
        HashSet set3 = Sets.newHashSet();
        BlockBox lv = BlockBox.empty();
        boolean bl = this.generate(arg, random, arg3, set, set2, lv, arg4);
        if (lv.minX > lv.maxX || !bl || set.isEmpty()) {
            return false;
        }
        if (!arg4.decorators.isEmpty()) {
            ArrayList list = Lists.newArrayList((Iterable)set);
            ArrayList list2 = Lists.newArrayList((Iterable)set2);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list2.sort(Comparator.comparingInt(Vec3i::getY));
            arg4.decorators.forEach(decorator -> decorator.generate(arg, random, list, list2, set3, lv));
        }
        VoxelSet lv2 = this.placeLogsAndLeaves(arg, lv, set, set3);
        Structure.updateCorner(arg, 3, lv2, lv.minX, lv.minY, lv.minZ);
        return true;
    }

    private VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> logs, Set<BlockPos> leaves) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet lv = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (BlockPos lv3 : Lists.newArrayList(leaves)) {
            if (!box.contains(lv3)) continue;
            ((VoxelSet)lv).set(lv3.getX() - box.minX, lv3.getY() - box.minY, lv3.getZ() - box.minZ, true, true);
        }
        for (BlockPos lv4 : Lists.newArrayList(logs)) {
            if (box.contains(lv4)) {
                ((VoxelSet)lv).set(lv4.getX() - box.minX, lv4.getY() - box.minY, lv4.getZ() - box.minZ, true, true);
            }
            for (Direction lv5 : Direction.values()) {
                BlockState lv6;
                lv2.set(lv4, lv5);
                if (logs.contains(lv2) || !(lv6 = world.getBlockState(lv2)).contains(Properties.DISTANCE_1_7)) continue;
                ((Set)list.get(0)).add(lv2.toImmutable());
                TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, lv2, (BlockState)lv6.with(Properties.DISTANCE_1_7, 1));
                if (!box.contains(lv2)) continue;
                ((VoxelSet)lv).set(lv2.getX() - box.minX, lv2.getY() - box.minY, lv2.getZ() - box.minZ, true, true);
            }
        }
        for (int k = 1; k < 6; ++k) {
            Set set3 = (Set)list.get(k - 1);
            Set set4 = (Set)list.get(k);
            for (BlockPos lv7 : set3) {
                if (box.contains(lv7)) {
                    ((VoxelSet)lv).set(lv7.getX() - box.minX, lv7.getY() - box.minY, lv7.getZ() - box.minZ, true, true);
                }
                for (Direction lv8 : Direction.values()) {
                    int l;
                    BlockState lv9;
                    lv2.set(lv7, lv8);
                    if (set3.contains(lv2) || set4.contains(lv2) || !(lv9 = world.getBlockState(lv2)).contains(Properties.DISTANCE_1_7) || (l = lv9.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                    BlockState lv10 = (BlockState)lv9.with(Properties.DISTANCE_1_7, k + 1);
                    TreeFeature.setBlockStateWithoutUpdatingNeighbors(world, lv2, lv10);
                    if (box.contains(lv2)) {
                        ((VoxelSet)lv).set(lv2.getX() - box.minX, lv2.getY() - box.minY, lv2.getZ() - box.minZ, true, true);
                    }
                    set4.add(lv2.toImmutable());
                }
            }
        }
        return lv;
    }
}

