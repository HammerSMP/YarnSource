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
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("minimum_reach").forGetter(arg -> arg.minReach), (App)Codec.INT.fieldOf("maximum_reach").forGetter(arg -> arg.maxReach), (App)Codec.INT.fieldOf("minimum_height").forGetter(arg -> arg.minHeight), (App)Codec.INT.fieldOf("maximum_height").forGetter(arg -> arg.maxHeight)).apply((Applicative)instance, BasaltColumnsFeatureConfig::new));
    public final int minReach;
    public final int maxReach;
    public final int minHeight;
    public final int maxHeight;

    public BasaltColumnsFeatureConfig(int i, int j, int k, int l) {
        this.minReach = i;
        this.maxReach = j;
        this.minHeight = k;
        this.maxHeight = l;
    }

    public static class Builder {
        private int minReach;
        private int maxReach;
        private int minHeight;
        private int maxHeight;

        public Builder reach(int i) {
            this.minReach = i;
            this.maxReach = i;
            return this;
        }

        public Builder reach(int i, int j) {
            this.minReach = i;
            this.maxReach = j;
            return this;
        }

        public Builder height(int i, int j) {
            this.minHeight = i;
            this.maxHeight = j;
            return this;
        }

        public BasaltColumnsFeatureConfig build() {
            if (this.minHeight < 1) {
                throw new IllegalArgumentException("Minimum height cannot be less than 1");
            }
            if (this.minReach < 0) {
                throw new IllegalArgumentException("Minimum reach cannot be negative");
            }
            if (this.minReach > this.maxReach || this.minHeight > this.maxHeight) {
                throw new IllegalArgumentException("Minimum reach/height cannot be greater than maximum width/height");
            }
            return new BasaltColumnsFeatureConfig(this.minReach, this.maxReach, this.minHeight, this.maxHeight);
        }
    }
}

