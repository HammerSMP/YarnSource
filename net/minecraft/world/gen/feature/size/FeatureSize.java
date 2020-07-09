/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public abstract class FeatureSize {
    public static final Codec<FeatureSize> CODEC = Registry.FEATURE_SIZE_TYPE.dispatch(FeatureSize::getType, FeatureSizeType::getCodec);
    protected final OptionalInt minClippedHeight;

    protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> createCodecBuilder() {
        return Codec.intRange((int)0, (int)80).optionalFieldOf("min_clipped_height").xmap(optional -> optional.map(OptionalInt::of).orElse(OptionalInt.empty()), optionalInt -> optionalInt.isPresent() ? Optional.of(optionalInt.getAsInt()) : Optional.empty()).forGetter(arg -> arg.minClippedHeight);
    }

    public FeatureSize(OptionalInt optionalInt) {
        this.minClippedHeight = optionalInt;
    }

    protected abstract FeatureSizeType<?> getType();

    public abstract int method_27378(int var1, int var2);

    public OptionalInt getMinClippedHeight() {
        return this.minClippedHeight;
    }
}

