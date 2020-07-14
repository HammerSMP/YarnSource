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
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        int m = 0;
        for (int n = l; n >= l - foliageHeight; --n) {
            this.generate(world, random, config, arg3.getCenter(), m, leaves, n, arg3.isGiantTrunk(), arg4);
            if (m >= 1 && n == l - foliageHeight + 1) {
                --m;
                continue;
            }
            if (m >= radius + arg3.getFoliageRadius()) continue;
            ++m;
        }
    }

    @Override
    public int getRadius(Random random, int baseHeight) {
        return super.getRadius(random, baseHeight) + random.nextInt(baseHeight + 1);
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height.method_30321(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        return baseHeight == dz && dy == dz && dz > 0;
    }
}

