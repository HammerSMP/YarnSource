/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("minimum_reach"), (Object)dynamicOps.createInt(this.minReach), (Object)dynamicOps.createString("maximum_reach"), (Object)dynamicOps.createInt(this.maxReach), (Object)dynamicOps.createString("minimum_height"), (Object)dynamicOps.createInt(this.minHeight), (Object)dynamicOps.createString("maximum_height"), (Object)dynamicOps.createInt(this.maxHeight))));
    }

    public static <T> BasaltColumnsFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("minimum_reach").asInt(0);
        int j = dynamic.get("maximum_reach").asInt(0);
        int k = dynamic.get("minimum_height").asInt(1);
        int l = dynamic.get("maximum_height").asInt(1);
        return new BasaltColumnsFeatureConfig(i, j, k, l);
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

