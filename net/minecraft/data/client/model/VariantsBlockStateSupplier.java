/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.data.client.model.PropertiesMap;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public class VariantsBlockStateSupplier
implements BlockStateSupplier {
    private final Block block;
    private final List<BlockStateVariant> variants;
    private final Set<Property<?>> definedProperties = Sets.newHashSet();
    private final List<BlockStateVariantMap> variantMaps = Lists.newArrayList();

    private VariantsBlockStateSupplier(Block block, List<BlockStateVariant> variants) {
        this.block = block;
        this.variants = variants;
    }

    public VariantsBlockStateSupplier coordinate(BlockStateVariantMap map) {
        map.getProperties().forEach(arg -> {
            if (this.block.getStateManager().getProperty(arg.getName()) != arg) {
                throw new IllegalStateException("Property " + arg + " is not defined for block " + this.block);
            }
            if (!this.definedProperties.add((Property<?>)arg)) {
                throw new IllegalStateException("Values of property " + arg + " already defined for block " + this.block);
            }
        });
        this.variantMaps.add(map);
        return this;
    }

    @Override
    public JsonElement get() {
        Stream<Object> stream = Stream.of(Pair.of((Object)PropertiesMap.empty(), this.variants));
        for (BlockStateVariantMap lv : this.variantMaps) {
            Map<PropertiesMap, List<BlockStateVariant>> map = lv.getVariants();
            stream = stream.flatMap(pair -> map.entrySet().stream().map(entry -> {
                PropertiesMap lv = ((PropertiesMap)pair.getFirst()).with((PropertiesMap)entry.getKey());
                List<BlockStateVariant> list = VariantsBlockStateSupplier.intersect((List)pair.getSecond(), (List)entry.getValue());
                return Pair.of((Object)lv, list);
            }));
        }
        TreeMap map2 = new TreeMap();
        stream.forEach(pair -> map2.put(((PropertiesMap)pair.getFirst()).asString(), BlockStateVariant.toJson((List)pair.getSecond())));
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("variants", (JsonElement)Util.make(new JsonObject(), jsonObject -> map2.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).add(arg_0, arg_1))));
        return jsonObject2;
    }

    private static List<BlockStateVariant> intersect(List<BlockStateVariant> list, List<BlockStateVariant> list2) {
        ImmutableList.Builder builder = ImmutableList.builder();
        list.forEach(arg -> list2.forEach(arg2 -> builder.add((Object)BlockStateVariant.union(arg, arg2))));
        return builder.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static VariantsBlockStateSupplier create(Block block) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.of((Object)BlockStateVariant.create()));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant variant) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.of((Object)variant));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant ... variants) {
        return new VariantsBlockStateSupplier(block, (List<BlockStateVariant>)ImmutableList.copyOf((Object[])variants));
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

