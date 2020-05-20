/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer
implements DynamicSerializable {
    private final int radius;
    private final int randomRadius;
    private final int offset;
    private final int randomOffset;
    private final FoliagePlacerType<?> type;

    public FoliagePlacer(int i, int j, int k, int l, FoliagePlacerType<?> arg) {
        this.radius = i;
        this.randomRadius = j;
        this.offset = k;
        this.randomOffset = l;
        this.type = arg;
    }

    public void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, TreeNode arg3, int j, int k, Set<BlockPos> set) {
        this.generate(arg, random, arg2, i, arg3, j, k, set, this.method_27386(random));
    }

    protected abstract void generate(ModifiableTestableWorld var1, Random var2, TreeFeatureConfig var3, int var4, TreeNode var5, int var6, int var7, Set<BlockPos> var8, int var9);

    public abstract int getHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRadius(Random random, int i) {
        return this.radius + random.nextInt(this.randomRadius + 1);
    }

    private int method_27386(Random random) {
        return this.offset + random.nextInt(this.randomOffset + 1);
    }

    protected abstract boolean isInvalidForLeaves(Random var1, int var2, int var3, int var4, int var5, boolean var6);

    protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
        int p;
        int o;
        if (bl) {
            int m = Math.min(Math.abs(i), Math.abs(i - 1));
            int n = Math.min(Math.abs(k), Math.abs(k - 1));
        } else {
            o = Math.abs(i);
            p = Math.abs(k);
        }
        return this.isInvalidForLeaves(random, o, j, p, l, bl);
    }

    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, BlockPos arg3, int i, Set<BlockPos> set, int j, boolean bl) {
        int k = bl ? 1 : 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = -i; l <= i + k; ++l) {
            for (int m = -i; m <= i + k; ++m) {
                if (this.method_27387(random, l, j, m, i, bl)) continue;
                lv.set(arg3, l, j, m);
                if (!TreeFeature.canReplace(arg, lv)) continue;
                arg.setBlockState(lv, arg2.leavesProvider.getBlockState(random, lv), 19);
                set.add(lv.toImmutable());
            }
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.type).toString())).put(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius)).put(dynamicOps.createString("radius_random"), dynamicOps.createInt(this.randomRadius)).put(dynamicOps.createString("offset"), dynamicOps.createInt(this.offset)).put(dynamicOps.createString("offset_random"), dynamicOps.createInt(this.randomOffset));
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build())).getValue();
    }

    public static final class TreeNode {
        private final BlockPos center;
        private final int foliageRadius;
        private final boolean giantTrunk;

        public TreeNode(BlockPos arg, int i, boolean bl) {
            this.center = arg;
            this.foliageRadius = i;
            this.giantTrunk = bl;
        }

        public BlockPos getCenter() {
            return this.center;
        }

        public int getFoliageRadius() {
            return this.foliageRadius;
        }

        public boolean isGiantTrunk() {
            return this.giantTrunk;
        }
    }
}

