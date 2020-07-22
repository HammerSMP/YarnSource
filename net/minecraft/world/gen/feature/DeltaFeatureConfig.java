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
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DeltaFeatureConfig
implements FeatureConfig {
    public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("contents").forGetter(arg -> arg.contents), (App)BlockState.CODEC.fieldOf("rim").forGetter(arg -> arg.rim), (App)UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("size").forGetter(arg -> arg.field_25843), (App)UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("rim_size").forGetter(arg -> arg.field_25844)).apply((Applicative)instance, DeltaFeatureConfig::new));
    private final BlockState contents;
    private final BlockState rim;
    private final UniformIntDistribution field_25843;
    private final UniformIntDistribution field_25844;

    public DeltaFeatureConfig(BlockState contents, BlockState rim, UniformIntDistribution arg3, UniformIntDistribution arg4) {
        this.contents = contents;
        this.rim = rim;
        this.field_25843 = arg3;
        this.field_25844 = arg4;
    }

    public BlockState method_30397() {
        return this.contents;
    }

    public BlockState method_30400() {
        return this.rim;
    }

    public UniformIntDistribution method_30402() {
        return this.field_25843;
    }

    public UniformIntDistribution method_30403() {
        return this.field_25844;
    }
}

