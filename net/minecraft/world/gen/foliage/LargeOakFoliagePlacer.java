/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class LargeOakFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<LargeOakFoliagePlacer> field_24930 = RecordCodecBuilder.create(instance -> LargeOakFoliagePlacer.method_28838(instance).apply((Applicative)instance, LargeOakFoliagePlacer::new));

    public LargeOakFoliagePlacer(int i, int j, int k, int l, int m) {
        super(i, j, k, l, m, FoliagePlacerType.FANCY_FOLIAGE_PLACER);
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        for (int m = l; m >= l - j; --m) {
            int n = k + (m == l || m == l - j ? 0 : 1);
            this.generate(arg, random, arg2, arg3.getCenter(), n, set, m, arg3.isGiantTrunk());
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return MathHelper.square((float)i + 0.5f) + MathHelper.square((float)k + 0.5f) > (float)(l * l);
    }
}

