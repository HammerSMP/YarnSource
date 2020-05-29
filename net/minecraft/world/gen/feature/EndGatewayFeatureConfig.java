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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndGatewayFeatureConfig
implements FeatureConfig {
    public static final Codec<EndGatewayFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.field_25064.optionalFieldOf("exit").forGetter(arg -> arg.exitPos), (App)Codec.BOOL.fieldOf("exact").forGetter(arg -> arg.exact)).apply((Applicative)instance, EndGatewayFeatureConfig::new));
    private final Optional<BlockPos> exitPos;
    private final boolean exact;

    private EndGatewayFeatureConfig(Optional<BlockPos> optional, boolean bl) {
        this.exitPos = optional;
        this.exact = bl;
    }

    public static EndGatewayFeatureConfig createConfig(BlockPos arg, boolean bl) {
        return new EndGatewayFeatureConfig(Optional.of(arg), bl);
    }

    public static EndGatewayFeatureConfig createConfig() {
        return new EndGatewayFeatureConfig(Optional.empty(), false);
    }

    public Optional<BlockPos> getExitPos() {
        return this.exitPos;
    }

    public boolean isExact() {
        return this.exact;
    }
}

