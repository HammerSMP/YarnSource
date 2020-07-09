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
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> PineFoliagePlacer.method_30411(instance).and((App)class_5428.method_30316(0, 16, 8).fieldOf("height").forGetter(arg -> arg.height)).apply((Applicative)instance, PineFoliagePlacer::new));
    private final class_5428 height;

    public PineFoliagePlacer(class_5428 arg, class_5428 arg2, class_5428 arg3) {
        super(arg, arg2);
        this.height = arg3;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        int m = 0;
        for (int n = l; n >= l - j; --n) {
            this.generate(arg, random, arg2, arg3.getCenter(), m, set, n, arg3.isGiantTrunk(), arg4);
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
        return this.height.method_30321(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }
}

