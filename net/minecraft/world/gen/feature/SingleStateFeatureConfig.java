/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SingleStateFeatureConfig
implements FeatureConfig {
    public static final Codec<SingleStateFeatureConfig> CODEC = BlockState.field_24734.fieldOf("state").xmap(SingleStateFeatureConfig::new, arg -> arg.state).codec();
    public final BlockState state;

    public SingleStateFeatureConfig(BlockState arg) {
        this.state = arg;
    }
}

