/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.foliage;

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

public class SpruceFoliagePlacer
extends FoliagePlacer {
    public static final Codec<SpruceFoliagePlacer> field_24936 = RecordCodecBuilder.create(instance -> SpruceFoliagePlacer.method_30411(instance).and((App)UniformIntDistribution.createValidatedCodec(0, 16, 8).fieldOf("trunk_height").forGetter(arg -> arg.trunkHeight)).apply((Applicative)instance, SpruceFoliagePlacer::new));
    private final UniformIntDistribution trunkHeight;

    public SpruceFoliagePlacer(UniformIntDistribution arg, UniformIntDistribution arg2, UniformIntDistribution arg3) {
        super(arg, arg2);
        this.trunkHeight = arg3;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        BlockPos lv = arg3.getCenter();
        int m = random.nextInt(2);
        int n = 1;
        int o = 0;
        for (int p = l; p >= -foliageHeight; --p) {
            this.generate(world, random, config, lv, m, leaves, p, arg3.isGiantTrunk(), arg4);
            if (m >= n) {
                m = o;
                o = 1;
                n = Math.min(n + 1, radius + arg3.getFoliageRadius());
                continue;
            }
            ++m;
        }
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return Math.max(4, trunkHeight - this.trunkHeight.getValue(random));
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        return baseHeight == dz && dy == dz && dz > 0;
    }
}

