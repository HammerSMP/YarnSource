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

    public void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, BlockBox arg4) {
        this.generate(world, random, config, trunkHeight, arg3, foliageHeight, radius, leaves, this.method_27386(random), arg4);
    }

    protected abstract void generate(ModifiableTestableWorld var1, Random var2, TreeFeatureConfig var3, int var4, TreeNode var5, int var6, int var7, Set<BlockPos> var8, int var9, BlockBox var10);

    public abstract int getHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRadius(Random random, int baseHeight) {
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

    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, BlockPos arg3, int baseHeight, Set<BlockPos> set, int j, boolean giantTrunk, BlockBox arg4) {
        int k = giantTrunk ? 1 : 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = -baseHeight; l <= baseHeight + k; ++l) {
            for (int m = -baseHeight; m <= baseHeight + k; ++m) {
                if (this.method_27387(random, l, j, m, baseHeight, giantTrunk)) continue;
                lv.set(arg3, l, j, m);
                if (!TreeFeature.canReplace(world, lv)) continue;
                world.setBlockState(lv, config.leavesProvider.getBlockState(random, lv), 19);
                arg4.encompass(new BlockBox(lv, lv));
                set.add(lv.toImmutable());
            }
        }
    }

    public static final class TreeNode {
        private final BlockPos center;
        private final int foliageRadius;
        private final boolean giantTrunk;

        public TreeNode(BlockPos center, int foliageRadius, boolean giantTrunk) {
            this.center = center;
            this.foliageRadius = foliageRadius;
            this.giantTrunk = giantTrunk;
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

