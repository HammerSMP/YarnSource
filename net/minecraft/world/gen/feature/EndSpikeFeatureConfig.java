/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndSpikeFeatureConfig
implements FeatureConfig {
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.Spike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public EndSpikeFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, @Nullable BlockPos arg) {
        this.crystalInvulnerable = bl;
        this.spikes = list;
        this.crystalBeamTarget = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("crystalInvulnerable"), (Object)dynamicOps.createBoolean(this.crystalInvulnerable), (Object)dynamicOps.createString("spikes"), (Object)dynamicOps.createList(this.spikes.stream().map(arg -> arg.serialize(dynamicOps).getValue())), (Object)dynamicOps.createString("crystalBeamTarget"), (Object)(this.crystalBeamTarget == null ? dynamicOps.createList(Stream.empty()) : dynamicOps.createList(IntStream.of(this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()).mapToObj(dynamicOps::createInt))))));
    }

    public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> dynamic2) {
        BlockPos lv2;
        List list = dynamic2.get("spikes").asList(EndSpikeFeature.Spike::deserialize);
        List list2 = dynamic2.get("crystalBeamTarget").asList(dynamic -> dynamic.asInt(0));
        if (list2.size() == 3) {
            BlockPos lv = new BlockPos((Integer)list2.get(0), (Integer)list2.get(1), (Integer)list2.get(2));
        } else {
            lv2 = null;
        }
        return new EndSpikeFeatureConfig(dynamic2.get("crystalInvulnerable").asBoolean(false), list, lv2);
    }

    public boolean isCrystalInvulerable() {
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

