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
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndGatewayFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, this.exitPos.map(arg -> dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("exit_x"), (Object)dynamicOps.createInt(arg.getX()), (Object)dynamicOps.createString("exit_y"), (Object)dynamicOps.createInt(arg.getY()), (Object)dynamicOps.createString("exit_z"), (Object)dynamicOps.createInt(arg.getZ()), (Object)dynamicOps.createString("exact"), (Object)dynamicOps.createBoolean(this.exact)))).orElse(dynamicOps.emptyMap()));
    }

    public static <T> EndGatewayFeatureConfig deserialize(Dynamic<T> dynamic) {
        Optional<BlockPos> optional = dynamic.get("exit_x").asNumber().flatMap(number -> dynamic.get("exit_y").asNumber().flatMap(number2 -> dynamic.get("exit_z").asNumber().map(number3 -> new BlockPos(number.intValue(), number2.intValue(), number3.intValue()))));
        boolean bl = dynamic.get("exact").asBoolean(false);
        return new EndGatewayFeatureConfig(optional, bl);
    }
}

