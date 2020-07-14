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
import net.minecraft.class_5428;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DiskFeatureConfig
implements FeatureConfig {
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("state").forGetter(arg -> arg.state), (App)class_5428.method_30316(0, 4, 4).fieldOf("radius").forGetter(arg -> arg.radius), (App)Codec.intRange((int)0, (int)4).fieldOf("half_height").forGetter(arg -> arg.ySize), (App)BlockState.CODEC.listOf().fieldOf("targets").forGetter(arg -> arg.targets)).apply((Applicative)instance, DiskFeatureConfig::new));
    public final BlockState state;
    public final class_5428 radius;
    public final int ySize;
    public final List<BlockState> targets;

    public DiskFeatureConfig(BlockState state, class_5428 arg2, int ySize, List<BlockState> targets) {
        this.state = state;
        this.radius = arg2;
        this.ySize = ySize;
        this.targets = targets;
    }
}

