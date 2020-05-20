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
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DeltaFeatureConfig
implements FeatureConfig {
    public static final Codec<DeltaFeatureConfig> field_24881 = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("contents").forGetter(arg -> arg.contents), (App)BlockState.field_24734.fieldOf("rim").forGetter(arg -> arg.rim), (App)Codec.INT.fieldOf("minimum_radius").forGetter(arg -> arg.minRadius), (App)Codec.INT.fieldOf("maximum_radius").forGetter(arg -> arg.maxRadius), (App)Codec.INT.fieldOf("maximum_rim").forGetter(arg -> arg.maxRim)).apply((Applicative)instance, DeltaFeatureConfig::new));
    public final BlockState contents;
    public final BlockState rim;
    public final int minRadius;
    public final int maxRadius;
    public final int maxRim;

    public DeltaFeatureConfig(BlockState arg, BlockState arg2, int i, int j, int k) {
        this.contents = arg;
        this.rim = arg2;
        this.minRadius = i;
        this.maxRadius = j;
        this.maxRim = k;
    }

    public static class Builder {
        Optional<BlockState> contents = Optional.empty();
        Optional<BlockState> rim = Optional.empty();
        int minRadius;
        int maxRadius;
        int maxRim;

        public Builder radius(int i, int j) {
            this.minRadius = i;
            this.maxRadius = j;
            return this;
        }

        public Builder contents(BlockState arg) {
            this.contents = Optional.of(arg);
            return this;
        }

        public Builder rim(BlockState arg, int i) {
            this.rim = Optional.of(arg);
            this.maxRim = i;
            return this;
        }

        public DeltaFeatureConfig build() {
            if (!this.contents.isPresent()) {
                throw new IllegalArgumentException("Missing contents");
            }
            if (!this.rim.isPresent()) {
                throw new IllegalArgumentException("Missing rim");
            }
            if (this.minRadius > this.maxRadius) {
                throw new IllegalArgumentException("Minimum radius cannot be greater than maximum radius");
            }
            return new DeltaFeatureConfig(this.contents.get(), this.rim.get(), this.minRadius, this.maxRadius, this.maxRim);
        }
    }
}

