/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
    public static final ConfiguredFeature<?, ?> field_24832 = new ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
    public static final Codec<ConfiguredFeature<?, ?>> CODEC = Registry.FEATURE.dispatch("name", arg -> arg.feature, Feature::method_28627).withDefault(field_24832);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F arg, FC arg2) {
        this.feature = arg;
        this.config = arg2;
    }

    public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> arg) {
        Feature<DecoratedFeatureConfig> lv = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
        return lv.configure(new DecoratedFeatureConfig(this, arg));
    }

    public RandomFeatureEntry<FC> withChance(float f) {
        return new RandomFeatureEntry(this, f);
    }

    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4) {
        return ((Feature)this.feature).generate(arg, arg2, arg3, random, arg4, this.config);
    }
}

