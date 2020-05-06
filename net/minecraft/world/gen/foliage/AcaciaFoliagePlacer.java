/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class AcaciaFoliagePlacer
extends FoliagePlacer {
    public AcaciaFoliagePlacer(int i, int j, int k, int l) {
        super(i, j, k, l, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
    }

    public <T> AcaciaFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0));
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

