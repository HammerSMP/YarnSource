/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomFeatureEntry<FC extends FeatureConfig> {
    public static final Codec<RandomFeatureEntry<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature").forGetter(arg -> arg.feature), (App)Codec.FLOAT.fieldOf("chance").forGetter(arg -> Float.valueOf(arg.chance))).apply((Applicative)instance, RandomFeatureEntry::new));
    public final ConfiguredFeature<FC, ?> feature;
    public final float chance;

    public RandomFeatureEntry(ConfiguredFeature<FC, ?> arg, float f) {
        this.feature = arg;
        this.chance = f;
    }

    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4) {
        return this.feature.generate(arg, arg2, arg3, random, arg4);
    }
}

