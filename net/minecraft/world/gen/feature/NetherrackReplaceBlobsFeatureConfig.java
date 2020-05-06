/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public final BlockState target;
    public final BlockState state;
    public final Vec3i minReachPos;
    public final Vec3i maxReachPos;

    public NetherrackReplaceBlobsFeatureConfig(BlockState arg, BlockState arg2, Vec3i arg3, Vec3i arg4) {
        this.target = arg;
        this.state = arg2;
        this.minReachPos = arg3;
        this.maxReachPos = arg4;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("target"), BlockState.serialize(dynamicOps, this.target).getValue());
        builder.put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue());
        builder.put(dynamicOps.createString("minimum_reach_x"), dynamicOps.createInt(this.minReachPos.getX()));
        builder.put(dynamicOps.createString("minimum_reach_y"), dynamicOps.createInt(this.minReachPos.getY()));
        builder.put(dynamicOps.createString("minimum_reach_z"), dynamicOps.createInt(this.minReachPos.getZ()));
        builder.put(dynamicOps.createString("maximum_reach_x"), dynamicOps.createInt(this.maxReachPos.getX()));
        builder.put(dynamicOps.createString("maximum_reach_y"), dynamicOps.createInt(this.maxReachPos.getY()));
        builder.put(dynamicOps.createString("maximum_reach_z"), dynamicOps.createInt(this.maxReachPos.getZ()));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    public static <T> NetherrackReplaceBlobsFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState lv2 = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int i = dynamic.get("minimum_reach_x").asInt(0);
        int j = dynamic.get("minimum_reach_y").asInt(0);
        int k = dynamic.get("minimum_reach_z").asInt(0);
        int l = dynamic.get("maximum_reach_x").asInt(0);
        int m = dynamic.get("maximum_reach_y").asInt(0);
        int n = dynamic.get("maximum_reach_z").asInt(0);
        return new NetherrackReplaceBlobsFeatureConfig(lv, lv2, new Vec3i(i, j, k), new Vec3i(l, m, n));
    }

    public static class Builder {
        private BlockState target = Blocks.AIR.getDefaultState();
        private BlockState state = Blocks.AIR.getDefaultState();
        private Vec3i minReachPos = Vec3i.ZERO;
        private Vec3i maxReachPos = Vec3i.ZERO;

        public Builder target(BlockState arg) {
            this.target = arg;
            return this;
        }

        public Builder state(BlockState arg) {
            this.state = arg;
            return this;
        }

        public Builder minReachPos(Vec3i arg) {
            this.minReachPos = arg;
            return this;
        }

        public Builder maxReachPos(Vec3i arg) {
            this.maxReachPos = arg;
            return this;
        }

        public NetherrackReplaceBlobsFeatureConfig build() {
            if (this.minReachPos.getX() < 0 || this.minReachPos.getY() < 0 || this.minReachPos.getZ() < 0) {
                throw new IllegalArgumentException("Minimum reach cannot be less than zero");
            }
            if (this.minReachPos.getX() > this.maxReachPos.getX() || this.minReachPos.getY() > this.maxReachPos.getY() || this.minReachPos.getZ() > this.maxReachPos.getZ()) {
                throw new IllegalArgumentException("Maximum reach must be greater than minimum reach for each axis");
            }
            return new NetherrackReplaceBlobsFeatureConfig(this.target, this.state, this.minReachPos, this.maxReachPos);
        }
    }
}

