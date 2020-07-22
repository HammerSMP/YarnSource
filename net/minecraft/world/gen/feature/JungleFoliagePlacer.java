/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class JungleFoliagePlacer
extends FoliagePlacer {
    public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> JungleFoliagePlacer.method_30411(instance).and((App)Codec.intRange((int)0, (int)16).fieldOf("height").forGetter(arg -> arg.height)).apply((Applicative)instance, JungleFoliagePlacer::new));
    protected final int height;

    public JungleFoliagePlacer(UniformIntDistribution arg, UniformIntDistribution arg2, int i) {
        super(arg, arg2);
        this.height = i;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        int m = arg3.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);
        for (int n = l; n >= l - m; --n) {
            int o = radius + arg3.getFoliageRadius() + 1 - n;
            this.generate(world, random, config, arg3.getCenter(), o, leaves, n, arg3.isGiantTrunk(), arg4);
        }
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        if (baseHeight + dy >= 7) {
            return true;
        }
        return baseHeight * baseHeight + dy * dy > dz * dz;
    }
}

