/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ShipwreckFeatureConfig
implements FeatureConfig {
    public static final Codec<ShipwreckFeatureConfig> field_24908 = Codec.BOOL.fieldOf("is_beached").withDefault((Object)false).xmap(ShipwreckFeatureConfig::new, arg -> arg.isBeached).codec();
    public final boolean isBeached;

    public ShipwreckFeatureConfig(boolean bl) {
        this.isBeached = bl;
    }
}

