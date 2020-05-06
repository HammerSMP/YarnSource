/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
    public final Decorator<DC> decorator;
    public final DC config;

    public ConfiguredDecorator(Decorator<DC> arg, Dynamic<?> dynamic) {
        this(arg, arg.deserialize(dynamic));
    }

    public ConfiguredDecorator(Decorator<DC> arg, DC arg2) {
        this.decorator = arg;
        this.config = arg2;
    }

    public <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, ConfiguredFeature<FC, F> arg5) {
        return this.decorator.generate(arg, arg2, arg3, random, arg4, this.config, arg5);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("name"), (Object)dynamicOps.createString(Registry.DECORATOR.getId(this.decorator).toString()), (Object)dynamicOps.createString("config"), (Object)this.config.serialize(dynamicOps).getValue())));
    }

    public static <T> ConfiguredDecorator<?> deserialize(Dynamic<T> dynamic) {
        Decorator<?> lv = Registry.DECORATOR.get(new Identifier(dynamic.get("name").asString("")));
        return new ConfiguredDecorator<Dynamic>((Decorator<Dynamic>)lv, dynamic.get("config").orElseEmptyMap());
    }
}

