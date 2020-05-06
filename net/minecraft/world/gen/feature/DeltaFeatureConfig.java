/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DeltaFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)new ImmutableMap.Builder().put(dynamicOps.createString("contents"), BlockState.serialize(dynamicOps, this.contents).getValue()).put(dynamicOps.createString("rim"), BlockState.serialize(dynamicOps, this.rim).getValue()).put(dynamicOps.createString("minimum_radius"), dynamicOps.createInt(this.minRadius)).put(dynamicOps.createString("maximum_radius"), dynamicOps.createInt(this.maxRadius)).put(dynamicOps.createString("maximum_rim"), dynamicOps.createInt(this.maxRim)).build()));
    }

    public static <T> DeltaFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("contents").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv2 = dynamic.get("rim").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int i = dynamic.get("minimum_radius").asInt(0);
        int j = dynamic.get("maximum_radius").asInt(0);
        int k = dynamic.get("maximum_rim").asInt(0);
        return new DeltaFeatureConfig(lv, lv2, i, j, k);
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

