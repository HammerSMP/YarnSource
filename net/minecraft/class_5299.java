/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class class_5299
extends DataFix {
    private static final ImmutableMap<String, class_5300> field_24647 = ImmutableMap.builder().put((Object)"minecraft:village", (Object)new class_5300(32, 8, 10387312)).put((Object)"minecraft:desert_pyramid", (Object)new class_5300(32, 8, 14357617)).put((Object)"minecraft:igloo", (Object)new class_5300(32, 8, 14357618)).put((Object)"minecraft:jungle_pyramid", (Object)new class_5300(32, 8, 14357619)).put((Object)"minecraft:swamp_hut", (Object)new class_5300(32, 8, 14357620)).put((Object)"minecraft:pillager_outpost", (Object)new class_5300(32, 8, 165745296)).put((Object)"minecraft:monument", (Object)new class_5300(32, 5, 10387313)).put((Object)"minecraft:endcity", (Object)new class_5300(20, 11, 10387313)).put((Object)"minecraft:mansion", (Object)new class_5300(80, 20, 10387319)).build();

    public class_5299(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(TypeReferences.CHUNK_GENERATOR_SETTINGS), typed -> typed.update(DSL.remainderFinder(), class_5299::method_28271));
    }

    private static <T> Dynamic<T> method_28268(long l, DynamicLike<T> dynamicLike, Dynamic<T> dynamic, Dynamic<T> dynamic2) {
        return dynamicLike.createMap((Map)ImmutableMap.of((Object)dynamicLike.createString("type"), (Object)dynamicLike.createString("minecraft:noise"), (Object)dynamicLike.createString("biome_source"), dynamic2, (Object)dynamicLike.createString("seed"), (Object)dynamicLike.createLong(l), (Object)dynamicLike.createString("settings"), dynamic));
    }

    private static <T> Dynamic<T> method_28272(Dynamic<T> dynamic, long l, boolean bl, boolean bl2) {
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:vanilla_layered")).put((Object)dynamic.createString("seed"), (Object)dynamic.createLong(l)).put((Object)dynamic.createString("large_biomes"), (Object)dynamic.createBoolean(bl2));
        if (bl) {
            builder.put((Object)dynamic.createString("legacy_biome_init_layer"), (Object)dynamic.createBoolean(bl));
        }
        return dynamic.createMap((Map)builder.build());
    }

    private static <T> Dynamic<T> method_28271(Dynamic<T> dynamic2) {
        Dynamic<T> dynamic13;
        DynamicOps dynamicOps = dynamic2.getOps();
        long l = dynamic2.get("RandomSeed").asLong(0L);
        Optional optional = dynamic2.get("generatorName").asString().map(string -> string.toLowerCase(Locale.ROOT)).result();
        Optional optional2 = dynamic2.get("legacy_custom_options").asString().result().map(Optional::of).orElseGet(() -> {
            if (optional.equals(Optional.of("customized"))) {
                return dynamic2.get("generatorOptions").asString().result();
            }
            return Optional.empty();
        });
        if (optional.equals(Optional.of("customized"))) {
            Dynamic<T> dynamic22 = class_5299.method_28268(l, dynamic2, dynamic2.createString("minecraft:overworld"), class_5299.method_28272(dynamic2, l, false, false));
        } else if (!optional.isPresent()) {
            Dynamic<T> dynamic3 = class_5299.method_28268(l, dynamic2, dynamic2.createString("minecraft:overworld"), class_5299.method_28272(dynamic2, l, false, false));
        } else {
            OptionalDynamic optionalDynamic = dynamic2.get("generatorOptions");
            switch ((String)optional.get()) {
                case "flat": {
                    Map<Dynamic<T>, Dynamic<T>> map = class_5299.method_28275(dynamicOps, optionalDynamic);
                    Dynamic dynamic4 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:flat"), (Object)dynamic2.createString("settings"), (Object)dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("structures"), (Object)dynamic2.createMap(map), (Object)dynamic2.createString("layers"), (Object)optionalDynamic.get("layers").orElseEmptyList(), (Object)dynamic2.createString("biome"), (Object)dynamic2.createString(optionalDynamic.get("biome").asString("plains"))))));
                    break;
                }
                case "debug_all_block_states": {
                    Dynamic dynamic5 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:debug")));
                    break;
                }
                case "buffet": {
                    Dynamic dynamic11;
                    Dynamic dynamic8;
                    OptionalDynamic optionalDynamic2 = optionalDynamic.get("chunk_generator");
                    Optional optional3 = optionalDynamic2.get("type").asString().result();
                    if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
                        Dynamic dynamic6 = dynamic2.createString("minecraft:caves");
                    } else if (Objects.equals(optional3, Optional.of("minecraft:floating_islands"))) {
                        Dynamic dynamic7 = dynamic2.createString("minecraft:floating_islands");
                    } else {
                        dynamic8 = dynamic2.createString("minecraft:overworld");
                    }
                    Dynamic dynamic9 = optionalDynamic.get("biome_source").result().orElseGet(() -> dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:fixed"))));
                    if (dynamic9.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                        String string2 = dynamic9.get("options").get("biomes").asStream().findFirst().flatMap(dynamic -> dynamic.asString().result()).orElse("minecraft:ocean");
                        Dynamic dynamic10 = dynamic9.remove("options").set("biome", dynamic2.createString(string2));
                    } else {
                        dynamic11 = dynamic9;
                    }
                    Dynamic<T> dynamic12 = class_5299.method_28268(l, dynamic2, dynamic8, dynamic11);
                    break;
                }
                default: {
                    boolean bl = ((String)optional.get()).equals("default");
                    boolean bl2 = ((String)optional.get()).equals("default_1_1") || bl && dynamic2.get("generatorVersion").asInt(0) == 0;
                    boolean bl3 = ((String)optional.get()).equals("amplified");
                    boolean bl4 = ((String)optional.get()).equals("largebiomes");
                    dynamic13 = class_5299.method_28268(l, dynamic2, dynamic2.createString(bl3 ? "minecraft:amplified" : "minecraft:overworld"), class_5299.method_28272(dynamic2, l, bl2, bl4));
                }
            }
        }
        boolean bl5 = dynamic2.get("MapFeatures").asBoolean(true);
        boolean bl6 = dynamic2.get("BonusChest").asBoolean(false);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
        builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl5));
        builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl6));
        builder.put(dynamicOps.createString("dimensions"), dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("minecraft:overworld"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:overworld"), (Object)dynamicOps.createString("generator"), (Object)dynamic13.getValue())), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createString("generator"), (Object)class_5299.method_28268(l, dynamic2, dynamic2.createString("minecraft:nether"), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:multi_noise"), (Object)dynamic2.createString("seed"), (Object)dynamic2.createLong(l), (Object)dynamic2.createString("preset"), (Object)dynamic2.createString("minecraft:nether")))).getValue())), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createString("generator"), (Object)class_5299.method_28268(l, dynamic2, dynamic2.createString("minecraft:end"), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:the_end"), (Object)dynamic2.createString("seed"), (Object)dynamic2.createLong(l)))).getValue())))));
        optional2.ifPresent(string -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(string)));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    private static <T> Map<Dynamic<T>, Dynamic<T>> method_28275(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
        MutableInt mutableInt = new MutableInt(32);
        MutableInt mutableInt2 = new MutableInt(3);
        MutableInt mutableInt3 = new MutableInt(128);
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        HashMap map = Maps.newHashMap();
        optionalDynamic.get("structures").flatMap(Dynamic::getMapValues).result().ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> dynamic2.getMapValues().result().ifPresent(map2 -> map2.forEach((dynamic2, dynamic3) -> {
            String string = dynamic.asString("");
            String string2 = dynamic2.asString("");
            String string3 = dynamic3.asString("");
            if ("stronghold".equals(string)) {
                mutableBoolean.setTrue();
                switch (string2) {
                    case "distance": {
                        mutableInt.setValue(class_5299.method_28280(string3, mutableInt.getValue(), 1));
                        return;
                    }
                    case "spread": {
                        mutableInt2.setValue(class_5299.method_28280(string3, mutableInt2.getValue(), 1));
                        return;
                    }
                    case "count": {
                        mutableInt3.setValue(class_5299.method_28280(string3, mutableInt3.getValue(), 1));
                        return;
                    }
                }
                return;
            }
            switch (string2) {
                case "distance": {
                    switch (string) {
                        case "village": {
                            class_5299.method_28281(map, "minecraft:village", string3, 9);
                            return;
                        }
                        case "biome_1": {
                            class_5299.method_28281(map, "minecraft:desert_pyramid", string3, 9);
                            class_5299.method_28281(map, "minecraft:igloo", string3, 9);
                            class_5299.method_28281(map, "minecraft:jungle_pyramid", string3, 9);
                            class_5299.method_28281(map, "minecraft:swamp_hut", string3, 9);
                            class_5299.method_28281(map, "minecraft:pillager_outpost", string3, 9);
                            return;
                        }
                        case "endcity": {
                            class_5299.method_28281(map, "minecraft:endcity", string3, 1);
                            return;
                        }
                        case "mansion": {
                            class_5299.method_28281(map, "minecraft:mansion", string3, 1);
                            return;
                        }
                    }
                    return;
                }
                case "separation": {
                    if ("oceanmonument".equals(string)) {
                        class_5300 lv = (class_5300)map.getOrDefault("minecraft:monument", field_24647.get((Object)"minecraft:monument"));
                        int i = class_5299.method_28280(string3, lv.field_24650, 1);
                        map.put("minecraft:monument", new class_5300(i, lv.field_24650, lv.field_24651));
                    }
                    return;
                }
                case "spacing": {
                    if ("oceanmonument".equals(string)) {
                        class_5299.method_28281(map, "minecraft:monument", string3, 1);
                    }
                    return;
                }
            }
        }))));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)optionalDynamic.createString("structures"), (Object)optionalDynamic.createMap(map.entrySet().stream().collect(Collectors.toMap(entry -> optionalDynamic.createString((String)entry.getKey()), entry -> ((class_5300)entry.getValue()).method_28288(dynamicOps)))));
        if (mutableBoolean.isTrue()) {
            builder.put((Object)optionalDynamic.createString("stronghold"), (Object)optionalDynamic.createMap((Map)ImmutableMap.of((Object)optionalDynamic.createString("distance"), (Object)optionalDynamic.createInt(mutableInt.getValue().intValue()), (Object)optionalDynamic.createString("spread"), (Object)optionalDynamic.createInt(mutableInt2.getValue().intValue()), (Object)optionalDynamic.createString("count"), (Object)optionalDynamic.createInt(mutableInt3.getValue().intValue()))));
        }
        return builder.build();
    }

    private static int method_28279(String string, int i) {
        return NumberUtils.toInt((String)string, (int)i);
    }

    private static int method_28280(String string, int i, int j) {
        return Math.max(j, class_5299.method_28279(string, i));
    }

    private static void method_28281(Map<String, class_5300> map, String string, String string2, int i) {
        class_5300 lv = map.getOrDefault(string, (class_5300)field_24647.get((Object)string));
        int j = class_5299.method_28280(string2, lv.field_24649, i);
        map.put(string, new class_5300(j, lv.field_24650, lv.field_24651));
    }

    static final class class_5300 {
        public static final Codec<class_5300> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("spacing").forGetter(arg -> arg.field_24649), (App)Codec.INT.fieldOf("separation").forGetter(arg -> arg.field_24650), (App)Codec.INT.fieldOf("salt").forGetter(arg -> arg.field_24651)).apply((Applicative)instance, class_5300::new));
        private final int field_24649;
        private final int field_24650;
        private final int field_24651;

        public class_5300(int i, int j, int k) {
            this.field_24649 = i;
            this.field_24650 = j;
            this.field_24651 = k;
        }

        public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
            return new Dynamic(dynamicOps, CODEC.encodeStart(dynamicOps, (Object)this).result().orElse(dynamicOps.emptyMap()));
        }
    }
}

