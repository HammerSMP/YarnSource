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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<NetherrackReplaceBlobsFeatureConfig> field_24905 = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("target").forGetter(arg -> arg.target), (App)BlockState.field_24734.fieldOf("state").forGetter(arg -> arg.state), (App)Vec3i.field_25123.fieldOf("minimum_reach").forGetter(arg -> arg.minReachPos), (App)Vec3i.field_25123.fieldOf("maximum_reach").forGetter(arg -> arg.maxReachPos)).apply((Applicative)instance, NetherrackReplaceBlobsFeatureConfig::new));
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

