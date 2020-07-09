/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public class TwoLayersFeatureSize
extends FeatureSize {
    public static final Codec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)81).fieldOf("limit").orElse((Object)1).forGetter(arg -> arg.limit), (App)Codec.intRange((int)0, (int)16).fieldOf("lower_size").orElse((Object)0).forGetter(arg -> arg.lowerSize), (App)Codec.intRange((int)0, (int)16).fieldOf("upper_size").orElse((Object)1).forGetter(arg -> arg.upperSize), TwoLayersFeatureSize.createCodecBuilder()).apply((Applicative)instance, TwoLayersFeatureSize::new));
    private final int limit;
    private final int lowerSize;
    private final int upperSize;

    public TwoLayersFeatureSize(int i, int j, int k) {
        this(i, j, k, OptionalInt.empty());
    }

    public TwoLayersFeatureSize(int i, int j, int k, OptionalInt optionalInt) {
        super(optionalInt);
        this.limit = i;
        this.lowerSize = j;
        this.upperSize = k;
    }

    @Override
    protected FeatureSizeType<?> getType() {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int method_27378(int i, int j) {
        return j < this.limit ? this.lowerSize : this.upperSize;
    }
}

