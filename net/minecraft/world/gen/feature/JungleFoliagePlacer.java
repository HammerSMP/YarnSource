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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class JungleFoliagePlacer
extends FoliagePlacer {
    public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> JungleFoliagePlacer.method_28846(instance).and((App)Codec.INT.fieldOf("height").forGetter(arg -> arg.height)).apply((Applicative)instance, JungleFoliagePlacer::new));
    protected final int height;

    public JungleFoliagePlacer(int i, int j, int k, int l, int m) {
        super(i, j, k, l);
        this.height = m;
    }

    @Override
    protected FoliagePlacerType<?> method_28843() {
        return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        int m = arg3.isGiantTrunk() ? j : 1 + random.nextInt(2);
        for (int n = l; n >= l - m; --n) {
            int o = k + arg3.getFoliageRadius() + 1 - n;
            this.generate(arg, random, arg2, arg3.getCenter(), o, set, n, arg3.isGiantTrunk(), arg4);
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (i + k >= 7) {
            return true;
        }
        return i * i + k * k > l * l;
    }
}

