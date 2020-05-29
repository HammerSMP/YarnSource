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

public class EmeraldOreFeatureConfig
implements FeatureConfig {
    public static final Codec<EmeraldOreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("target").forGetter(arg -> arg.target), (App)BlockState.field_24734.fieldOf("state").forGetter(arg -> arg.state)).apply((Applicative)instance, EmeraldOreFeatureConfig::new));
    public final BlockState target;
    public final BlockState state;

    public EmeraldOreFeatureConfig(BlockState arg, BlockState arg2) {
        this.target = arg;
        this.state = arg2;
    }
}

