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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<PineFoliagePlacer> field_24935 = RecordCodecBuilder.create(instance -> PineFoliagePlacer.method_28846(instance).and(instance.group((App)Codec.INT.fieldOf("height").forGetter(arg -> arg.height), (App)Codec.INT.fieldOf("height_random").forGetter(arg -> arg.randomHeight))).apply((Applicative)instance, PineFoliagePlacer::new));
    private final int height;
    private final int randomHeight;

    public PineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l);
        this.height = m;
        this.randomHeight = n;
    }

    @Override
    protected FoliagePlacerType<?> method_28843() {
        return FoliagePlacerType.PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        int m = 0;
        for (int n = l; n >= l - j; --n) {
            this.generate(arg, random, arg2, arg3.getCenter(), m, set, n, arg3.isGiantTrunk());
            if (m >= 1 && n == l - j + 1) {
                --m;
                continue;
            }
            if (m >= k + arg3.getFoliageRadius()) continue;
            ++m;
        }
    }

    @Override
    public int getRadius(Random random, int i) {
        return super.getRadius(random, i) + random.nextInt(i + 1);
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return this.height + random.nextInt(this.randomHeight + 1);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }
}

