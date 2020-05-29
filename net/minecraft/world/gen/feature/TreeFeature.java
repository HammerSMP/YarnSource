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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class TreeFeature
extends Feature<TreeFeatureConfig> {
    public TreeFeature(Codec<TreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean canTreeReplace(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Block lv = arg.getBlock();
            return arg.isAir() || arg.isIn(BlockTags.LEAVES) || TreeFeature.isDirt(lv) || arg.isIn(BlockTags.LOGS) || arg.isIn(BlockTags.SAPLINGS) || arg.isOf(Blocks.VINE) || arg.isOf(Blocks.WATER);
        });
    }

    private static boolean isVine(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> arg.isOf(Blocks.VINE));
    }

    private static boolean isWater(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> arg.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> arg.isAir() || arg.isIn(BlockTags.LEAVES));
    }

    private static boolean isDirtOrGrass(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Block lv = arg.getBlock();
            return TreeFeature.isDirt(lv) || lv == Blocks.FARMLAND;
        });
    }

    private static boolean isReplaceablePlant(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Material lv = arg.getMaterial();
            return lv == Material.REPLACEABLE_PLANT;
        });
    }

    public static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld arg, BlockPos arg2, BlockState arg3) {
        arg.setBlockState(arg2, arg3, 19);
    }

    public static boolean canReplace(ModifiableTestableWorld arg, BlockPos arg2) {
        return TreeFeature.isAirOrLeaves(arg, arg2) || TreeFeature.isReplaceablePlant(arg, arg2) || TreeFeature.isWater(arg, arg2);
    }

    private boolean generate(ModifiableTestableWorld arg, Random random, BlockPos arg2, Set<BlockPos> set, Set<BlockPos> set2, BlockBox arg3, TreeFeatureConfig arg42) {
        BlockPos lv2;
        int i = arg42.trunkPlacer.getHeight(random);
        int j = arg42.foliagePlacer.getHeight(random, i, arg42);
        int k = i - j;
        int l = arg42.foliagePlacer.getRadius(random, k);
        if (!arg42.skipFluidCheck) {
            int q;
            int m = arg.getTopPosition(Heightmap.Type.OCEAN_FLOOR, arg2).getY();
            int n = arg.getTopPosition(Heightmap.Type.WORLD_SURFACE, arg2).getY();
            if (n - m > arg42.baseHeight) {
                return false;
            }
            if (arg42.heightmap == Heightmap.Type.OCEAN_FLOOR) {
                int o = m;
            } else if (arg42.heightmap == Heightmap.Type.WORLD_SURFACE) {
                int p = n;
            } else {
                q = arg.getTopPosition(arg42.heightmap, arg2).getY();
            }
            BlockPos lv = new BlockPos(arg2.getX(), q, arg2.getZ());
        } else {
            lv2 = arg2;
        }
        if (lv2.getY() < 1 || lv2.getY() + i + 1 > 256) {
            return false;
        }
        if (!TreeFeature.isDirtOrGrass(arg, lv2.down())) {
            return false;
        }
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        OptionalInt optionalInt = arg42.featureSize.getMinClippedHeight();
        int r = i;
        for (int s = 0; s <= i + 1; ++s) {
            int t = arg42.featureSize.method_27378(i, s);
            block1: for (int u = -t; u <= t; ++u) {
                for (int v = -t; v <= t; ++v) {
                    lv3.set(lv2, u, s, v);
                    if (TreeFeature.canTreeReplace(arg, lv3) && (arg42.ignoreVines || !TreeFeature.isVine(arg, lv3))) continue;
                    if (optionalInt.isPresent() && s - 1 >= optionalInt.getAsInt() + 1) {
                        r = s - 2;
                        continue block1;
                    }
                    return false;
                }
            }
        }
        int w = r;
        List<FoliagePlacer.TreeNode> list = arg42.trunkPlacer.generate(arg, random, w, lv2, set, arg3, arg42);
        list.forEach(arg4 -> arg.foliagePlacer.generate(arg, random, arg42, w, (FoliagePlacer.TreeNode)arg4, j, l, set2, arg3));
        return true;
    }

    @Override
    protected void setBlockState(ModifiableWorld arg, BlockPos arg2, BlockState arg3) {
        TreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, arg2, arg3);
    }

    @Override
    public final boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg32, Random random, BlockPos arg4, TreeFeatureConfig arg5) {
        HashSet set = Sets.newHashSet();
        HashSet set2 = Sets.newHashSet();
        HashSet set3 = Sets.newHashSet();
        BlockBox lv = BlockBox.empty();
        boolean bl = this.generate(arg, random, arg4, set, set2, lv, arg5);
        if (lv.minX > lv.maxX || !bl || set.isEmpty()) {
            return false;
        }
        if (!arg5.decorators.isEmpty()) {
            ArrayList list = Lists.newArrayList((Iterable)set);
            ArrayList list2 = Lists.newArrayList((Iterable)set2);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list2.sort(Comparator.comparingInt(Vec3i::getY));
            arg5.decorators.forEach(arg3 -> arg3.generate(arg, random, list, list2, set3, lv));
        }
        VoxelSet lv2 = this.placeLogsAndLeaves(arg, lv, set, set3);
        Structure.updateCorner(arg, 3, lv2, lv.minX, lv.minY, lv.minZ);
        return true;
    }

    private VoxelSet placeLogsAndLeaves(WorldAccess arg, BlockBox arg2, Set<BlockPos> set, Set<BlockPos> set2) {
        ArrayList list = Lists.newArrayList();
        BitSetVoxelSet lv = new BitSetVoxelSet(arg2.getBlockCountX(), arg2.getBlockCountY(), arg2.getBlockCountZ());
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (BlockPos lv3 : Lists.newArrayList(set2)) {
            if (!arg2.contains(lv3)) continue;
            ((VoxelSet)lv).set(lv3.getX() - arg2.minX, lv3.getY() - arg2.minY, lv3.getZ() - arg2.minZ, true, true);
        }
        for (BlockPos lv4 : Lists.newArrayList(set)) {
            if (arg2.contains(lv4)) {
                ((VoxelSet)lv).set(lv4.getX() - arg2.minX, lv4.getY() - arg2.minY, lv4.getZ() - arg2.minZ, true, true);
            }
            for (Direction lv5 : Direction.values()) {
                BlockState lv6;
                lv2.set(lv4, lv5);
                if (set.contains(lv2) || !(lv6 = arg.getBlockState(lv2)).contains(Properties.DISTANCE_1_7)) continue;
                ((Set)list.get(0)).add(lv2.toImmutable());
                TreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, lv2, (BlockState)lv6.with(Properties.DISTANCE_1_7, 1));
                if (!arg2.contains(lv2)) continue;
                ((VoxelSet)lv).set(lv2.getX() - arg2.minX, lv2.getY() - arg2.minY, lv2.getZ() - arg2.minZ, true, true);
            }
        }
        for (int k = 1; k < 6; ++k) {
            Set set3 = (Set)list.get(k - 1);
            Set set4 = (Set)list.get(k);
            for (BlockPos lv7 : set3) {
                if (arg2.contains(lv7)) {
                    ((VoxelSet)lv).set(lv7.getX() - arg2.minX, lv7.getY() - arg2.minY, lv7.getZ() - arg2.minZ, true, true);
                }
                for (Direction lv8 : Direction.values()) {
                    int l;
                    BlockState lv9;
                    lv2.set(lv7, lv8);
                    if (set3.contains(lv2) || set4.contains(lv2) || !(lv9 = arg.getBlockState(lv2)).contains(Properties.DISTANCE_1_7) || (l = lv9.get(Properties.DISTANCE_1_7).intValue()) <= k + 1) continue;
                    BlockState lv10 = (BlockState)lv9.with(Properties.DISTANCE_1_7, k + 1);
                    TreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, lv2, lv10);
                    if (arg2.contains(lv2)) {
                        ((VoxelSet)lv).set(lv2.getX() - arg2.minX, lv2.getY() - arg2.minY, lv2.getZ() - arg2.minZ, true, true);
                    }
                    set4.add(lv2.toImmutable());
                }
            }
        }
        return lv;
    }
}

