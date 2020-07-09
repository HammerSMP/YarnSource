/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class BlobFoliagePlacer
extends FoliagePlacer {
    public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> BlobFoliagePlacer.method_28838(instance).apply((Applicative)instance, BlobFoliagePlacer::new));
    protected final int height;

    protected static <P extends BlobFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, class_5428, class_5428, Integer> method_28838(RecordCodecBuilder.Instance<P> instance) {
        return BlobFoliagePlacer.method_30411(instance).and((App)Codec.intRange((int)0, (int)16).fieldOf("height").forGetter(arg -> arg.height));
    }

    public BlobFoliagePlacer(class_5428 arg, class_5428 arg2, int i) {
        super(arg, arg2);
        this.height = i;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        for (int m = l; m >= l - j; --m) {
            int n = Math.max(k + arg3.getFoliageRadius() - 1 - m / 2, 0);
            this.generate(arg, random, arg2, arg3.getCenter(), n, set, m, arg3.isGiantTrunk(), arg4);
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && (random.nextInt(2) == 0 || j == 0);
    }
}

