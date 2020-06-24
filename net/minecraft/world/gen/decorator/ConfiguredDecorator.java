/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
    public static final Codec<ConfiguredDecorator<?>> field_24981 = Registry.DECORATOR.dispatch("name", arg -> arg.decorator, Decorator::getCodec);
    public final Decorator<DC> decorator;
    public final DC config;

    public ConfiguredDecorator(Decorator<DC> arg, DC arg2) {
        this.decorator = arg;
        this.config = arg2;
    }

    public <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, ConfiguredFeature<FC, F> arg5) {
        return this.decorator.generate(arg, arg2, arg3, random, arg4, this.config, arg5);
    }
}

