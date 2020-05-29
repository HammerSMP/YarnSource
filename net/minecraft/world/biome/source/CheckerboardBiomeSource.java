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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5324;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class CheckerboardBiomeSource
extends BiomeSource {
    public static final Codec<CheckerboardBiomeSource> field_24715 = RecordCodecBuilder.create(instance -> instance.group((App)Registry.BIOME.listOf().fieldOf("biomes").forGetter(arg -> arg.biomeArray), (App)class_5324.method_29229(0, 62).fieldOf("scale").withDefault((Object)2).forGetter(arg -> arg.field_24716)).apply((Applicative)instance, CheckerboardBiomeSource::new));
    private final List<Biome> biomeArray;
    private final int gridSize;
    private final int field_24716;

    public CheckerboardBiomeSource(List<Biome> list, int i) {
        super((List<Biome>)ImmutableList.copyOf(list));
        this.biomeArray = list;
        this.gridSize = i + 2;
        this.field_24716 = i;
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24715;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long l) {
        return this;
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.biomeArray.get(Math.floorMod((i >> this.gridSize) + (k >> this.gridSize), this.biomeArray.size()));
    }
}

