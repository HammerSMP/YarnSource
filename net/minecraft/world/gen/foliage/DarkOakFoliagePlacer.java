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
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class DarkOakFoliagePlacer
extends FoliagePlacer {
    public static final Codec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> DarkOakFoliagePlacer.method_30411(instance).apply((Applicative)instance, DarkOakFoliagePlacer::new));

    public DarkOakFoliagePlacer(class_5428 arg, class_5428 arg2) {
        super(arg, arg2);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        BlockPos lv = arg3.getCenter().up(l);
        boolean bl = arg3.isGiantTrunk();
        if (bl) {
            this.generate(world, random, config, lv, radius + 2, leaves, -1, bl, arg4);
            this.generate(world, random, config, lv, radius + 3, leaves, 0, bl, arg4);
            this.generate(world, random, config, lv, radius + 2, leaves, 1, bl, arg4);
            if (random.nextBoolean()) {
                this.generate(world, random, config, lv, radius, leaves, 2, bl, arg4);
            }
        } else {
            this.generate(world, random, config, lv, radius + 2, leaves, -1, bl, arg4);
            this.generate(world, random, config, lv, radius + 1, leaves, 0, bl, arg4);
        }
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
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
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        if (dx == -1 && !bl) {
            return baseHeight == dz && dy == dz;
        }
        if (dx == 1) {
            return baseHeight + dy > dz * 2 - 2;
        }
        return false;
    }
}

