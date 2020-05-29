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
import net.minecraft.world.gen.feature.FeatureConfig;

public class BoulderFeatureConfig
implements FeatureConfig {
    public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("state").forGetter(arg -> arg.state), (App)Codec.INT.fieldOf("start_radius").withDefault((Object)0).forGetter(arg -> arg.startRadius)).apply((Applicative)instance, BoulderFeatureConfig::new));
    public final BlockState state;
    public final int startRadius;

    public BoulderFeatureConfig(BlockState arg, int i) {
        this.state = arg;
        this.startRadius = i;
    }
}

