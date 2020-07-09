/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P2
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
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> CODEC = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
    protected final class_5428 radius;
    protected final class_5428 offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, class_5428, class_5428> method_30411(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)class_5428.method_30316(0, 8, 8).fieldOf("radius").forGetter(arg -> arg.radius), (App)class_5428.method_30316(0, 8, 8).fieldOf("offset").forGetter(arg -> arg.offset));
    }

    public FoliagePlacer(class_5428 arg, class_5428 arg2) {
        this.radius = arg;
        this.offset = arg2;
    }

    protected abstract FoliagePlacerType<?> getType();

    public void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, TreeNode arg3, int j, int k, Set<BlockPos> set, BlockBox arg4) {
        this.generate(arg, random, arg2, i, arg3, j, k, set, this.method_27386(random), arg4);
    }

    protected abstract void generate(ModifiableTestableWorld var1, Random var2, TreeFeatureConfig var3, int var4, TreeNode var5, int var6, int var7, Set<BlockPos> var8, int var9, BlockBox var10);

    public abstract int getHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRadius(Random random, int i) {
        return this.radius.method_30321(random);
    }

    private int method_27386(Random random) {
        return this.offset.method_30321(random);
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

    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, BlockPos arg3, int i, Set<BlockPos> set, int j, boolean bl, BlockBox arg4) {
        int k = bl ? 1 : 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = -i; l <= i + k; ++l) {
            for (int m = -i; m <= i + k; ++m) {
                if (this.method_27387(random, l, j, m, i, bl)) continue;
                lv.set(arg3, l, j, m);
                if (!TreeFeature.canReplace(arg, lv)) continue;
                arg.setBlockState(lv, arg2.leavesProvider.getBlockState(random, lv), 19);
                arg4.encompass(new BlockBox(lv, lv));
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

