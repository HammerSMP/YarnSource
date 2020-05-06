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
import net.minecraft.world.gen.feature.RuinedPortalFeature;

public class RuinedPortalFeatureConfig
implements FeatureConfig {
    public final RuinedPortalFeature.Type portalType;

    public RuinedPortalFeatureConfig() {
        this(RuinedPortalFeature.Type.STANDARD);
    }

    public RuinedPortalFeatureConfig(RuinedPortalFeature.Type arg) {
        this.portalType = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("portal_type"), (Object)dynamicOps.createString(this.portalType.getName()))));
    }

    public static <T> RuinedPortalFeatureConfig deserialize(Dynamic<T> dynamic) {
        RuinedPortalFeature.Type lv = RuinedPortalFeature.Type.byName(dynamic.get("portal_type").asString(""));
        return new RuinedPortalFeatureConfig(lv);
    }
}

