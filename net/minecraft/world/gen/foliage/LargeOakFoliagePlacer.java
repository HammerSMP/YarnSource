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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class LargeOakFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<LargeOakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> LargeOakFoliagePlacer.method_28838(instance).apply((Applicative)instance, LargeOakFoliagePlacer::new));

    public LargeOakFoliagePlacer(UniformIntDistribution arg, UniformIntDistribution arg2, int i) {
        super(arg, arg2, i);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        for (int m = l; m >= l - foliageHeight; --m) {
            int n = radius + (m == l || m == l - foliageHeight ? 0 : 1);
            this.generate(world, random, config, arg3.getCenter(), n, leaves, m, arg3.isGiantTrunk(), arg4);
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        return MathHelper.square((float)baseHeight + 0.5f) + MathHelper.square((float)dy + 0.5f) > (float)(dz * dz);
    }
}

