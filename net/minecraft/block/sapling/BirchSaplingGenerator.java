/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.class_5464;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BirchSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return bl ? class_5464.BIRCH_BEES_005 : class_5464.BIRCH;
    }
}

