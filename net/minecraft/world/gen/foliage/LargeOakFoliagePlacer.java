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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class LargeOakFoliagePlacer
extends BlobFoliagePlacer {
    public LargeOakFoliagePlacer(int i, int j, int k, int l, int m) {
        super(i, j, k, l, m, FoliagePlacerType.FANCY_FOLIAGE_PLACER);
    }

    public <T> LargeOakFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0), dynamic.get("height").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        for (int m = l; m >= l - j; --m) {
            int n = k + (m == l || m == l - j ? 0 : 1);
            this.generate(arg, random, arg2, arg3.getCenter(), n, set, m, arg3.isGiantTrunk());
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return MathHelper.square((float)i + 0.5f) + MathHelper.square((float)k + 0.5f) > (float)(l * l);
    }
}

