/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public static final Codec<SpringFeatureConfig> field_24912 = RecordCodecBuilder.create(instance -> instance.group((App)FluidState.field_25018.fieldOf("state").forGetter(arg -> arg.state), (App)Codec.BOOL.fieldOf("requires_block_below").withDefault((Object)true).forGetter(arg -> arg.requiresBlockBelow), (App)Codec.INT.fieldOf("rock_count").withDefault((Object)4).forGetter(arg -> arg.rockCount), (App)Codec.INT.fieldOf("hole_count").withDefault((Object)1).forGetter(arg -> arg.holeCount), (App)Registry.BLOCK.listOf().fieldOf("valid_blocks").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter(arg -> arg.validBlocks)).apply((Applicative)instance, SpringFeatureConfig::new));
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringFeatureConfig(FluidState arg, boolean bl, int i, int j, Set<Block> set) {
        this.state = arg;
        this.requiresBlockBelow = bl;
        this.rockCount = i;
        this.holeCount = j;
        this.validBlocks = set;
    }
}

