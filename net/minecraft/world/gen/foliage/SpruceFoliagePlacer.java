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

public class SpruceFoliagePlacer
extends FoliagePlacer {
    public static final Codec<SpruceFoliagePlacer> field_24936 = RecordCodecBuilder.create(instance -> SpruceFoliagePlacer.method_28846(instance).and(instance.group((App)Codec.INT.fieldOf("trunk_height").forGetter(arg -> arg.trunkHeight), (App)Codec.INT.fieldOf("trunk_height_random").forGetter(arg -> arg.randomTrunkHeight))).apply((Applicative)instance, SpruceFoliagePlacer::new));
    private final int trunkHeight;
    private final int randomTrunkHeight;

    public SpruceFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l);
        this.trunkHeight = m;
        this.randomTrunkHeight = n;
    }

    @Override
    protected FoliagePlacerType<?> method_28843() {
        return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        BlockPos lv = arg3.getCenter();
        int m = random.nextInt(2);
        int n = 1;
        int o = 0;
        for (int p = l; p >= -j; --p) {
            this.generate(arg, random, arg2, lv, m, set, p, arg3.isGiantTrunk());
            if (m >= n) {
                m = o;
                o = 1;
                n = Math.min(n + 1, k + arg3.getFoliageRadius());
                continue;
            }
            ++m;
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return i - this.trunkHeight - random.nextInt(this.randomTrunkHeight + 1);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }
}

