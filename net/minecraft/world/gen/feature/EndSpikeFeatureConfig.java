/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndSpikeFeatureConfig
implements FeatureConfig {
    public static final Codec<EndSpikeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("crystal_invulnerable").orElse((Object)false).forGetter(arg -> arg.crystalInvulnerable), (App)EndSpikeFeature.Spike.CODEC.listOf().fieldOf("spikes").forGetter(arg -> arg.spikes), (App)BlockPos.field_25064.optionalFieldOf("crystal_beam_target").forGetter(arg -> Optional.ofNullable(arg.crystalBeamTarget))).apply((Applicative)instance, EndSpikeFeatureConfig::new));
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.Spike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public EndSpikeFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, @Nullable BlockPos arg) {
        this(bl, list, Optional.ofNullable(arg));
    }

    private EndSpikeFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, Optional<BlockPos> optional) {
        this.crystalInvulnerable = bl;
        this.spikes = list;
        this.crystalBeamTarget = optional.orElse(null);
    }

    public boolean isCrystalInvulnerable() {
        return this.crystalInvulnerable;
    }

    public List<EndSpikeFeature.Spike> getSpikes() {
        return this.spikes;
    }

    @Nullable
    public BlockPos getPos() {
        return this.crystalBeamTarget;
    }
}

