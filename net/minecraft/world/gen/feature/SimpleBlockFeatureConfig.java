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

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.field_24734.fieldOf("to_place").forGetter(arg -> arg.toPlace), (App)BlockState.field_24734.listOf().fieldOf("place_on").forGetter(arg -> arg.placeOn), (App)BlockState.field_24734.listOf().fieldOf("place_in").forGetter(arg -> arg.placeIn), (App)BlockState.field_24734.listOf().fieldOf("place_under").forGetter(arg -> arg.placeUnder)).apply((Applicative)instance, SimpleBlockFeatureConfig::new));
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockState arg, List<BlockState> list, List<BlockState> list2, List<BlockState> list3) {
        this.toPlace = arg;
        this.placeOn = list;
        this.placeIn = list2;
        this.placeUnder = list3;
    }
}

