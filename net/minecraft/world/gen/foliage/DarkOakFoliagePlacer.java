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

public class DarkOakFoliagePlacer
extends FoliagePlacer {
    public static final Codec<DarkOakFoliagePlacer> field_24929 = RecordCodecBuilder.create(instance -> DarkOakFoliagePlacer.method_28846(instance).apply((Applicative)instance, DarkOakFoliagePlacer::new));

    public DarkOakFoliagePlacer(int i, int j, int k, int l) {
        super(i, j, k, l);
    }

    @Override
    protected FoliagePlacerType<?> method_28843() {
        return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        BlockPos lv = arg3.getCenter().up(l);
        boolean bl = arg3.isGiantTrunk();
        if (bl) {
            this.generate(arg, random, arg2, lv, k + 2, set, -1, bl);
            this.generate(arg, random, arg2, lv, k + 3, set, 0, bl);
            this.generate(arg, random, arg2, lv, k + 2, set, 1, bl);
            if (random.nextBoolean()) {
                this.generate(arg, random, arg2, lv, k, set, 2, bl);
            }
        } else {
            this.generate(arg, random, arg2, lv, k + 2, set, -1, bl);
            this.generate(arg, random, arg2, lv, k + 1, set, 0, bl);
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return 4;
    }

    @Override
    protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
        if (!(j != 0 || !bl || i != -l && i < l || k != -l && k < l)) {
            return true;
        }
        return super.method_27387(random, i, j, k, l, bl);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (j == -1 && !bl) {
            return i == l && k == l;
        }
        if (j == 1) {
            return i + k > l * 2 - 2;
        }
        return false;
    }
}

