/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public abstract class TrunkPlacer {
    private final int baseHeight;
    private final int firstRandomHeight;
    private final int secondRandomHeight;
    protected final TrunkPlacerType<?> type;

    public TrunkPlacer(int i, int j, int k, TrunkPlacerType<?> arg) {
        this.baseHeight = i;
        this.firstRandomHeight = j;
        this.secondRandomHeight = k;
        this.type = arg;
    }

    public abstract List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BlockBox var6, TreeFeatureConfig var7);

    public int getHeight(Random random) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    protected static void method_27404(ModifiableWorld arg, BlockPos arg2, BlockState arg3, BlockBox arg4) {
        AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, arg2, arg3);
        arg4.encompass(new BlockBox(arg2, arg2));
    }

    private static boolean method_27403(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Block lv = arg.getBlock();
            return Feature.isDirt(lv) && !arg.isOf(Blocks.GRASS_BLOCK) && !arg.isOf(Blocks.MYCELIUM);
        });
    }

    protected static void method_27400(ModifiableTestableWorld arg, BlockPos arg2) {
        if (!TrunkPlacer.method_27403(arg, arg2)) {
            AbstractTreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, arg2, Blocks.DIRT.getDefaultState());
        }
    }

    protected static boolean method_27402(ModifiableTestableWorld arg, Random random, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        if (AbstractTreeFeature.canReplace(arg, arg2)) {
            TrunkPlacer.method_27404(arg, arg2, arg4.trunkProvider.getBlockState(random, arg2), arg3);
            set.add(arg2.toImmutable());
            return true;
        }
        return false;
    }

    protected static void method_27401(ModifiableTestableWorld arg, Random random, BlockPos.Mutable arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        if (AbstractTreeFeature.canTreeReplace(arg, arg2)) {
            TrunkPlacer.method_27402(arg, random, arg2, set, arg3, arg4);
        }
    }

    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.TRUNK_PLACER_TYPE.getId(this.type).toString())).put(dynamicOps.createString("base_height"), dynamicOps.createInt(this.baseHeight)).put(dynamicOps.createString("height_rand_a"), dynamicOps.createInt(this.firstRandomHeight)).put(dynamicOps.createString("height_rand_b"), dynamicOps.createInt(this.secondRandomHeight));
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build())).getValue();
    }
}

