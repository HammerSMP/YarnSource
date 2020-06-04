/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;

public class RuinedPortalFeatureConfig
implements FeatureConfig {
    public static final Codec<RuinedPortalFeatureConfig> CODEC = RuinedPortalFeature.Type.field_24840.fieldOf("portal_type").xmap(RuinedPortalFeatureConfig::new, arg -> arg.portalType).codec();
    public final RuinedPortalFeature.Type portalType;

    public RuinedPortalFeatureConfig(RuinedPortalFeature.Type arg) {
        this.portalType = arg;
    }
}

