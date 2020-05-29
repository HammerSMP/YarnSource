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

public class ThreeLayersFeatureSize
extends FeatureSize {
    public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("limit").withDefault((Object)1).forGetter(arg -> arg.limit), (App)Codec.INT.fieldOf("upper_limit").withDefault((Object)1).forGetter(arg -> arg.upperLimit), (App)Codec.INT.fieldOf("lower_size").withDefault((Object)0).forGetter(arg -> arg.lowerSize), (App)Codec.INT.fieldOf("middle_size").withDefault((Object)1).forGetter(arg -> arg.middleSize), (App)Codec.INT.fieldOf("upper_size").withDefault((Object)1).forGetter(arg -> arg.upperSize), ThreeLayersFeatureSize.method_28820()).apply((Applicative)instance, ThreeLayersFeatureSize::new));
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public ThreeLayersFeatureSize(int i, int j, int k, int l, int m, OptionalInt optionalInt) {
        super(optionalInt);
        this.limit = i;
        this.upperLimit = j;
        this.lowerSize = k;
        this.middleSize = l;
        this.upperSize = m;
    }

    @Override
    protected FeatureSizeType<?> method_28824() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int method_27378(int i, int j) {
        if (j < this.limit) {
            return this.lowerSize;
        }
        if (j >= i - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}

