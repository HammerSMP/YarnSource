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
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class RandomFeatureEntry {
    public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature").forGetter(arg -> arg.feature), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance").forGetter(arg -> Float.valueOf(arg.chance))).apply((Applicative)instance, RandomFeatureEntry::new));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final float chance;

    public RandomFeatureEntry(ConfiguredFeature<?, ?> arg, float f) {
        this(() -> arg, f);
    }

    private RandomFeatureEntry(Supplier<ConfiguredFeature<?, ?>> supplier, float f) {
        this.feature = supplier;
        this.chance = f;
    }

    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3) {
        return this.feature.get().generate(arg, arg2, random, arg3);
    }
}

