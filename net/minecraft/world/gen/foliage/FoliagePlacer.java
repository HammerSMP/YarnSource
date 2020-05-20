/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> field_24931 = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::method_28843, FoliagePlacerType::method_28849);
    protected final int radius;
    protected final int randomRadius;
    protected final int offset;
    protected final int randomOffset;

    protected static <P extends FoliagePlacer> Products.P4<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer, Integer> method_28846(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.INT.fieldOf("radius").forGetter(arg -> arg.radius), (App)Codec.INT.fieldOf("radius_random").forGetter(arg -> arg.randomRadius), (App)Codec.INT.fieldOf("offset").forGetter(arg -> arg.offset), (App)Codec.INT.fieldOf("offset_random").forGetter(arg -> arg.randomOffset));
    }

    public FoliagePlacer(int i, int j, int k, int l) {
        this.radius = i;
        this.randomRadius = j;
        this.offset = k;
        this.randomOffset = l;
    }

    protected abstract FoliagePlacerType<?> method_28843();

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

