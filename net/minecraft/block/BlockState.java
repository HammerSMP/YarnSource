/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockState
extends AbstractBlock.AbstractBlockState {
    public BlockState(Block arg, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(arg, immutableMap);
    }

    @Override
    protected BlockState asBlockState() {
        return this;
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps, BlockState arg) {
        Object object2;
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = arg.getEntries();
        if (immutableMap.isEmpty()) {
            Object object = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Name"), (Object)dynamicOps.createString(Registry.BLOCK.getId(arg.getBlock()).toString())));
        } else {
            object2 = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Name"), (Object)dynamicOps.createString(Registry.BLOCK.getId(arg.getBlock()).toString()), (Object)dynamicOps.createString("Properties"), (Object)dynamicOps.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of((Object)dynamicOps.createString(((Property)entry.getKey()).getName()), (Object)dynamicOps.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return new Dynamic(dynamicOps, object2);
    }

    public static <T> BlockState deserialize(Dynamic<T> dynamic2) {
        Block lv = Registry.BLOCK.get(new Identifier(dynamic2.getElement("Name").flatMap(((DynamicOps)dynamic2.getOps())::getStringValue).orElse("minecraft:air")));
        Map map = dynamic2.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        BlockState lv2 = lv.getDefaultState();
        StateManager<Block, BlockState> lv3 = lv.getStateManager();
        for (Map.Entry entry : map.entrySet()) {
            String string = (String)entry.getKey();
            Property<?> lv4 = lv3.getProperty(string);
            if (lv4 == null) continue;
            lv2 = State.tryRead(lv2, lv4, string, dynamic2.toString(), (String)entry.getValue());
        }
        return lv2;
    }
}

