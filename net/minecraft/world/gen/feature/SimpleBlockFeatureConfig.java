/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
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

    public SimpleBlockFeatureConfig(BlockState arg, BlockState[] args, BlockState[] args2, BlockState[] args3) {
        this(arg, Lists.newArrayList((Object[])args), Lists.newArrayList((Object[])args2), Lists.newArrayList((Object[])args3));
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        Object object = BlockState.serialize(dynamicOps, this.toPlace).getValue();
        Object object2 = dynamicOps.createList(this.placeOn.stream().map(arg -> BlockState.serialize(dynamicOps, arg).getValue()));
        Object object3 = dynamicOps.createList(this.placeIn.stream().map(arg -> BlockState.serialize(dynamicOps, arg).getValue()));
        Object object4 = dynamicOps.createList(this.placeUnder.stream().map(arg -> BlockState.serialize(dynamicOps, arg).getValue()));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("to_place"), (Object)object, (Object)dynamicOps.createString("place_on"), (Object)object2, (Object)dynamicOps.createString("place_in"), (Object)object3, (Object)dynamicOps.createString("place_under"), (Object)object4)));
    }

    public static <T> SimpleBlockFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        List list = dynamic.get("place_on").asList(BlockState::deserialize);
        List list2 = dynamic.get("place_in").asList(BlockState::deserialize);
        List list3 = dynamic.get("place_under").asList(BlockState::deserialize);
        return new SimpleBlockFeatureConfig(lv, list, list2, list3);
    }
}

