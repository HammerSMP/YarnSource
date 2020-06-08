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
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DiskFeatureConfig
implements FeatureConfig {
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("state").forGetter(arg -> arg.state), (App)Codec.INT.fieldOf("radius").withDefault((Object)0).forGetter(arg -> arg.radius), (App)Codec.INT.fieldOf("y_size").withDefault((Object)0).forGetter(arg -> arg.ySize), (App)BlockState.CODEC.listOf().fieldOf("targets").forGetter(arg -> arg.targets)).apply((Applicative)instance, DiskFeatureConfig::new));
    public final BlockState state;
    public final int radius;
    public final int ySize;
    public final List<BlockState> targets;

    public DiskFeatureConfig(BlockState arg, int i, int j, List<BlockState> list) {
        this.state = arg;
        this.radius = i;
        this.ySize = j;
        this.targets = list;
    }
}

