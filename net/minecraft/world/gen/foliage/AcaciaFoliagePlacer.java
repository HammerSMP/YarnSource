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

public class AcaciaFoliagePlacer
extends FoliagePlacer {
    public static final Codec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> AcaciaFoliagePlacer.method_30411(instance).apply((Applicative)instance, AcaciaFoliagePlacer::new));

    public AcaciaFoliagePlacer(class_5428 arg, class_5428 arg2) {
        super(arg, arg2);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode arg3, int foliageHeight, int radius, Set<BlockPos> leaves, int l, BlockBox arg4) {
        boolean bl = arg3.isGiantTrunk();
        BlockPos lv = arg3.getCenter().up(l);
        this.generate(world, random, config, lv, radius + arg3.getFoliageRadius(), leaves, -1 - foliageHeight, bl, arg4);
        this.generate(world, random, config, lv, radius - 1, leaves, -foliageHeight, bl, arg4);
        this.generate(world, random, config, lv, radius + arg3.getFoliageRadius() - 1, leaves, 0, bl, arg4);
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        if (dx == 0) {
            return (baseHeight > 1 || dy > 1) && baseHeight != 0 && dy != 0;
        }
        return baseHeight == dz && dy == dz && dz > 0;
    }
}

