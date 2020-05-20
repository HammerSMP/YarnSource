/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.Identifier;
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
    public static final Logger log = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F arg, FC arg2) {
        this.feature = arg;
        this.config = arg2;
    }

    public ConfiguredFeature(F arg, Dynamic<?> dynamic) {
        this(arg, ((Feature)arg).deserializeConfig(dynamic));
    }

    public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> arg) {
        Feature<DecoratedFeatureConfig> lv = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
        return lv.configure(new DecoratedFeatureConfig(this, arg));
    }

    public RandomFeatureEntry<FC> withChance(float f) {
        return new RandomFeatureEntry(this, f);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("name"), (Object)dynamicOps.createString(Registry.FEATURE.getId((Feature<?>)this.feature).toString()), (Object)dynamicOps.createString("config"), (Object)this.config.serialize(dynamicOps).getValue())));
    }

    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4) {
        return ((Feature)this.feature).generate(arg, arg2, arg3, random, arg4, this.config);
    }

    public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("name").asString("");
        Feature<?> lv = Registry.FEATURE.get(new Identifier(string));
        try {
            return new ConfiguredFeature(lv, dynamic.get("config").orElseEmptyMap());
        }
        catch (RuntimeException runtimeException) {
            log.warn("Error while deserializing {}", (Object)string);
            return new ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
        }
    }
}

