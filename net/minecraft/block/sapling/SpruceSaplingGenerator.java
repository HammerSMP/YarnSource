/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.class_5464;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class SpruceSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return class_5464.SPRUCE;
    }

    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
        return random.nextBoolean() ? class_5464.MEGA_SPRUCE : class_5464.MEGA_PINE;
    }
}

