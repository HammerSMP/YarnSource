/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5284;
import net.minecraft.class_5321;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5285 {
    public static final Codec<class_5285> field_24826 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(class_5285::method_28028), (App)Codec.BOOL.fieldOf("generate_features").withDefault((Object)true).stable().forGetter(class_5285::method_28029), (App)Codec.BOOL.fieldOf("bonus_chest").withDefault((Object)false).stable().forGetter(class_5285::method_28030), (App)Codec.unboundedMap((Codec)Identifier.field_25139.xmap(class_5321.method_29178(Registry.DIMENSION_TYPE_KEY), class_5321::method_29177), (Codec)Codec.mapPair((MapCodec)DimensionType.field_24756.fieldOf("type"), (MapCodec)ChunkGenerator.field_24746.fieldOf("generator")).codec()).xmap(DimensionType::method_28524, Function.identity()).fieldOf("dimensions").forGetter(class_5285::method_28609), (App)Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(arg -> arg.field_24532)).apply((Applicative)instance, instance.stable((Object)((Function5)class_5285::new)))).comapFlatMap(class_5285::method_28610, Function.identity());
    private static final Logger field_24525 = LogManager.getLogger();
    private static final int field_24524 = "North Carolina".hashCode();
    public static final class_5285 field_24520 = new class_5285(field_24524, true, true, class_5285.method_28608(DimensionType.method_28517(field_24524), class_5285.method_28604(field_24524)));
    public static final class_5285 field_24521 = new class_5285(0L, false, false, class_5285.method_28608(DimensionType.method_28517(0L), new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig())));
    private final long field_24526;
    private final boolean field_24527;
    private final boolean field_24528;
    private final LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> field_24827;
    private final Optional<String> field_24532;

    private DataResult<class_5285> method_28610() {
        if (this.method_28611()) {
            return DataResult.success((Object)this, (Lifecycle)Lifecycle.stable());
        }
        return DataResult.success((Object)this);
    }

    private boolean method_28611() {
        return DimensionType.method_28518(this.field_24526, this.field_24827);
    }

    public class_5285(long l, boolean bl, boolean bl2, LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap) {
        this(l, bl, bl2, linkedHashMap, Optional.empty());
    }

    private class_5285(long l, boolean bl, boolean bl2, LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap, Optional<String> optional) {
        this.field_24526 = l;
        this.field_24527 = bl;
        this.field_24528 = bl2;
        this.field_24827 = linkedHashMap;
        this.field_24532 = optional;
    }

    public static class_5285 method_28009() {
        long l = new Random().nextLong();
        return new class_5285(l, true, false, class_5285.method_28608(DimensionType.method_28517(l), class_5285.method_28604(l)));
    }

    public static SurfaceChunkGenerator method_28604(long l) {
        return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, class_5284.class_5307.OVERWORLD.method_28568());
    }

    public long method_28028() {
        return this.field_24526;
    }

    public boolean method_28029() {
        return this.field_24527;
    }

    public boolean method_28030() {
        return this.field_24528;
    }

    public static LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> method_28608(LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap, ChunkGenerator arg) {
        LinkedHashMap linkedHashMap2 = Maps.newLinkedHashMap();
        Pair<DimensionType, ChunkGenerator> pair = linkedHashMap.get(DimensionType.field_24753);
        DimensionType lv = pair == null ? DimensionType.method_28514() : (DimensionType)pair.getFirst();
        linkedHashMap2.put(DimensionType.field_24753, Pair.of((Object)lv, (Object)arg));
        for (Map.Entry<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry : linkedHashMap.entrySet()) {
            if (Objects.equals(entry.getKey(), DimensionType.field_24753)) continue;
            linkedHashMap2.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap2;
    }

    public LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> method_28609() {
        return this.field_24827;
    }

    public ChunkGenerator method_28032() {
        Pair<DimensionType, ChunkGenerator> pair = this.field_24827.get(DimensionType.field_24753);
        if (pair == null) {
            return class_5285.method_28604(new Random().nextLong());
        }
        return (ChunkGenerator)pair.getSecond();
    }

    public boolean method_28033() {
        return this.method_28032() instanceof DebugChunkGenerator;
    }

    public boolean method_28034() {
        return this.method_28032() instanceof FlatChunkGenerator;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_28035() {
        return this.field_24532.isPresent();
    }

    public class_5285 method_28036() {
        return new class_5285(this.field_24526, this.field_24527, true, this.field_24827, this.field_24532);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28037() {
        return new class_5285(this.field_24526, !this.field_24527, this.field_24528, this.field_24827);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28038() {
        return new class_5285(this.field_24526, this.field_24527, !this.field_24528, this.field_24827);
    }

    public static class_5285 method_28021(Properties properties) {
        String string2 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("generator-settings")), (Object)"");
        properties.put("generator-settings", string2);
        String string22 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("level-seed")), (Object)"");
        properties.put("level-seed", string22);
        String string3 = (String)properties.get("generate-structures");
        boolean bl = string3 == null || Boolean.parseBoolean(string3);
        properties.put("generate-structures", Objects.toString(bl));
        String string4 = (String)properties.get("level-type");
        String string5 = Optional.ofNullable(string4).map(string -> string.toLowerCase(Locale.ROOT)).orElse("default");
        properties.put("level-type", string5);
        long l = new Random().nextLong();
        if (!string22.isEmpty()) {
            try {
                long m = Long.parseLong(string22);
                if (m != 0L) {
                    l = m;
                }
            }
            catch (NumberFormatException numberFormatException) {
                l = string22.hashCode();
            }
        }
        LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap = DimensionType.method_28517(l);
        switch (string5) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? JsonHelper.deserialize(string2) : new JsonObject();
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject);
                return new class_5285(l, bl, false, class_5285.method_28608(linkedHashMap, new FlatChunkGenerator(FlatChunkGeneratorConfig.field_24975.parse(dynamic).resultOrPartial(((Logger)field_24525)::error).orElseGet(FlatChunkGeneratorConfig::getDefaultConfig))));
            }
            case "debug_all_block_states": {
                return new class_5285(l, bl, false, class_5285.method_28608(linkedHashMap, DebugChunkGenerator.generator));
            }
        }
        return new class_5285(l, bl, false, class_5285.method_28608(linkedHashMap, class_5285.method_28604(l)));
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28024(boolean bl, OptionalLong optionalLong) {
        class_5285 lv2;
        LinkedHashMap<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> linkedHashMap2;
        long l = optionalLong.orElse(this.field_24526);
        if (optionalLong.isPresent()) {
            LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
            long m = optionalLong.getAsLong();
            for (Map.Entry<class_5321<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry : this.field_24827.entrySet()) {
                linkedHashMap.put(entry.getKey(), Pair.of((Object)entry.getValue().getFirst(), (Object)((ChunkGenerator)entry.getValue().getSecond()).create(m)));
            }
        } else {
            linkedHashMap2 = this.field_24827;
        }
        if (this.method_28033()) {
            class_5285 lv = new class_5285(l, false, false, linkedHashMap2);
        } else {
            lv2 = new class_5285(l, this.method_28029(), this.method_28030() && !bl, linkedHashMap2);
        }
        return lv2;
    }
}

