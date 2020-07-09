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
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class BushFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> BushFoliagePlacer.method_28838(instance).apply((Applicative)instance, BushFoliagePlacer::new));

    public BushFoliagePlacer(class_5428 arg, class_5428 arg2, int i) {
        super(arg, arg2, i);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        for (int m = l; m >= l - j; --m) {
            int n = k + arg3.getFoliageRadius() - 1 - m;
            this.generate(arg, random, arg2, arg3.getCenter(), n, set, m, arg3.isGiantTrunk(), arg4);
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && random.nextInt(2) == 0;
    }
}

