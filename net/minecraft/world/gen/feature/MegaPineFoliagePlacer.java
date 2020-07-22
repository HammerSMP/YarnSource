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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> MegaPineFoliagePlacer.method_30411(instance).and((App)UniformIntDistribution.createValidatedCodec(0, 16, 8).fieldOf("crown_height").forGetter(arg -> arg.crownHeight)).apply((Applicative)instance, MegaPineFoliagePlacer::new));
    private final UniformIntDistribution crownHeight;

    public MegaPineFoliagePlacer(UniformIntDistribution arg, UniformIntDistribution arg2, UniformIntDistribution arg3) {
        super(arg, arg2);
        this.crownHeight = arg3;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        BlockPos lv = arg3.getCenter();
        int m = 0;
        for (int n = lv.getY() - foliageHeight + l; n <= lv.getY() + l; ++n) {
            int r;
            int o = lv.getY() - n;
            int p = radius + arg3.getFoliageRadius() + MathHelper.floor((float)o / (float)foliageHeight * 3.5f);
            if (o > 0 && p == m && (n & 1) == 0) {
                int q = p + 1;
            } else {
                r = p;
            }
            this.generate(world, random, config, new BlockPos(lv.getX(), n, lv.getZ()), r, leaves, 0, arg3.isGiantTrunk(), arg4);
            m = p;
        }
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.crownHeight.getValue(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        if (baseHeight + dy >= 7) {
            return true;
        }
        return baseHeight * baseHeight + dy * dy > dz * dz;
    }
}

