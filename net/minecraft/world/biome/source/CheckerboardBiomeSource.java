/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class CheckerboardBiomeSource
extends BiomeSource {
    public static final Codec<CheckerboardBiomeSource> field_24715 = RecordCodecBuilder.create(instance -> instance.group((App)Biome.field_24677.listOf().fieldOf("biomes").forGetter(arg -> arg.biomeArray), (App)Codec.intRange((int)0, (int)62).fieldOf("scale").orElse((Object)2).forGetter(arg -> arg.field_24716)).apply((Applicative)instance, CheckerboardBiomeSource::new));
    private final List<Supplier<Biome>> biomeArray;
    private final int gridSize;
    private final int field_24716;

    public CheckerboardBiomeSource(List<Supplier<Biome>> list, int size) {
        super((List)list.stream().map(Supplier::get).collect(ImmutableList.toImmutableList()));
        this.biomeArray = list;
        this.gridSize = size + 2;
        this.field_24716 = size;
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24715;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeArray.get(Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.size())).get();
    }
}

