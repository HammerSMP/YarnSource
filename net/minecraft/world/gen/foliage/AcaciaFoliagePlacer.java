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
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class AcaciaFoliagePlacer
extends FoliagePlacer {
    public static final Codec<AcaciaFoliagePlacer> field_24926 = RecordCodecBuilder.create(instance -> AcaciaFoliagePlacer.method_28846(instance).apply((Applicative)instance, AcaciaFoliagePlacer::new));

    public AcaciaFoliagePlacer(int i, int j, int k, int l) {
        super(i, j, k, l);
    }

    @Override
    protected FoliagePlacerType<?> method_28843() {
        return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        boolean bl = arg3.isGiantTrunk();
        BlockPos lv = arg3.getCenter().up(l);
        this.generate(arg, random, arg2, lv, k + arg3.getFoliageRadius(), set, -1 - j, bl);
        this.generate(arg, random, arg2, lv, k - 1, set, -j, bl);
        this.generate(arg, random, arg2, lv, k + arg3.getFoliageRadius() - 1, set, 0, bl);
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (j == 0) {
            return (i > 1 || k > 1) && i != 0 && k != 0;
        }
        return i == l && k == l && l > 0;
    }
}

