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

public class SpruceFoliagePlacer
extends FoliagePlacer {
    public static final Codec<SpruceFoliagePlacer> field_24936 = RecordCodecBuilder.create(instance -> SpruceFoliagePlacer.method_30411(instance).and((App)class_5428.method_30316(0, 16, 8).fieldOf("trunk_height").forGetter(arg -> arg.trunkHeight)).apply((Applicative)instance, SpruceFoliagePlacer::new));
    private final class_5428 trunkHeight;

    public SpruceFoliagePlacer(class_5428 arg, class_5428 arg2, class_5428 arg3) {
        super(arg, arg2);
        this.trunkHeight = arg3;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        BlockPos lv = arg3.getCenter();
        int m = random.nextInt(2);
        int n = 1;
        int o = 0;
        for (int p = l; p >= -j; --p) {
            this.generate(arg, random, arg2, lv, m, set, p, arg3.isGiantTrunk(), arg4);
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
        return Math.max(4, i - this.trunkHeight.method_30321(random));
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }
}

