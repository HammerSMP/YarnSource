/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.DynamicLike
 *  com.mojang.datafixers.OptionalDynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.types.JsonOps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DynamicLike;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5284;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5285 {
    private static final Dynamic<?> field_24522 = new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)new CompoundTag());
    private static final ChunkGenerator field_24523 = new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
    private static final int field_24524 = "North Carolina".hashCode();
    public static final class_5285 field_24520 = new class_5285(field_24524, true, true, class_5287.DEFAULT, field_24522, new OverworldChunkGenerator(new VanillaLayeredBiomeSource(field_24524, false, 4), field_24524, new OverworldChunkGeneratorConfig()));
    public static final class_5285 field_24521 = new class_5285(0L, false, false, class_5287.FLAT, field_24522, field_24523);
    private static final Logger field_24525 = LogManager.getLogger();
    private final long field_24526;
    private final boolean field_24527;
    private final boolean field_24528;
    private final class_5287 field_24529;
    private final Dynamic<?> field_24530;
    private final ChunkGenerator field_24531;
    @Nullable
    private final String field_24532;
    private final boolean field_24533;
    private static final Map<class_5287, class_5288> field_24534 = Maps.newHashMap();

    public class_5285(long l, boolean bl, boolean bl2, class_5287 arg, Dynamic<?> dynamic, ChunkGenerator arg2) {
        this(l, bl, bl2, arg, dynamic, arg2, null, false);
    }

    private class_5285(long l, boolean bl, boolean bl2, class_5287 arg, Dynamic<?> dynamic, ChunkGenerator arg2, @Nullable String string, boolean bl3) {
        this.field_24526 = l;
        this.field_24527 = bl;
        this.field_24528 = bl2;
        this.field_24532 = string;
        this.field_24533 = bl3;
        this.field_24529 = arg;
        this.field_24530 = dynamic;
        this.field_24531 = arg2;
    }

    public static class_5285 method_28023(CompoundTag arg, DataFixer dataFixer, int i) {
        boolean bl2;
        class_5287 lv5;
        OverworldChunkGenerator lv4;
        Dynamic<?> dynamic4;
        long l = arg.getLong("RandomSeed");
        String string = null;
        if (arg.contains("generatorName", 8)) {
            String string2 = arg.getString("generatorName");
            class_5287 lv = class_5287.method_28048(string2);
            if (lv == null) {
                lv = class_5287.DEFAULT;
            } else if (lv == class_5287.CUSTOMIZED) {
                string = arg.getString("generatorOptions");
            } else if (lv == class_5287.DEFAULT) {
                int j = 0;
                if (arg.contains("generatorVersion", 99)) {
                    j = arg.getInt("generatorVersion");
                }
                if (j == 0) {
                    lv = class_5287.DEFAULT_1_1;
                }
            }
            CompoundTag lv2 = arg.getCompound("generatorOptions");
            Dynamic dynamic = new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv2);
            int k = Math.max(i, 2501);
            Dynamic dynamic2 = dynamic.merge(dynamic.createString("levelType"), dynamic.createString(lv.field_24551));
            Dynamic dynamic3 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, k, SharedConstants.getGameVersion().getWorldVersion()).remove("levelType");
            ChunkGenerator lv3 = class_5285.method_28013(lv, dynamic3, l);
        } else {
            dynamic4 = field_24522;
            lv4 = new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, false, 4), l, new OverworldChunkGeneratorConfig());
            lv5 = class_5287.DEFAULT;
        }
        if (arg.contains("legacy_custom_options", 8)) {
            string = arg.getString("legacy_custom_options");
        }
        if (arg.contains("MapFeatures", 99)) {
            boolean bl = arg.getBoolean("MapFeatures");
        } else {
            bl2 = true;
        }
        boolean bl3 = arg.getBoolean("BonusChest");
        boolean bl4 = lv5 == class_5287.CUSTOMIZED && i < 1466;
        return new class_5285(l, bl2, bl3, lv5, dynamic4, lv4, string, bl4);
    }

    private static ChunkGenerator method_28011(long l) {
        TheEndBiomeSource lv = new TheEndBiomeSource(l);
        class_5284 lv2 = new class_5284(new ChunkGeneratorConfig());
        lv2.setDefaultBlock(Blocks.END_STONE.getDefaultState());
        lv2.setDefaultFluid(Blocks.AIR.getDefaultState());
        return new FloatingIslandsChunkGenerator(lv, l, lv2);
    }

    private static ChunkGenerator method_28026(long l) {
        ImmutableList immutableList = ImmutableList.of((Object)Biomes.NETHER_WASTES, (Object)Biomes.SOUL_SAND_VALLEY, (Object)Biomes.CRIMSON_FOREST, (Object)Biomes.WARPED_FOREST, (Object)Biomes.BASALT_DELTAS);
        MultiNoiseBiomeSource lv = MultiNoiseBiomeSource.fromBiomes(l, (List<Biome>)immutableList);
        CavesChunkGeneratorConfig lv2 = new CavesChunkGeneratorConfig(new ChunkGeneratorConfig());
        lv2.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
        lv2.setDefaultFluid(Blocks.LAVA.getDefaultState());
        return new CavesChunkGenerator(lv, l, lv2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5285 method_28009() {
        long l = new Random().nextLong();
        return new class_5285(l, true, false, class_5287.DEFAULT, field_24522, new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, false, 4), l, new OverworldChunkGeneratorConfig()));
    }

    public CompoundTag method_28025() {
        CompoundTag lv = new CompoundTag();
        lv.putLong("RandomSeed", this.method_28028());
        class_5287 lv2 = this.field_24529 == class_5287.CUSTOMIZED ? class_5287.DEFAULT : this.field_24529;
        lv.putString("generatorName", lv2.field_24551);
        lv.putInt("generatorVersion", this.field_24529 == class_5287.DEFAULT ? 1 : 0);
        CompoundTag lv3 = (CompoundTag)this.field_24530.convert((DynamicOps)NbtOps.INSTANCE).getValue();
        if (!lv3.isEmpty()) {
            lv.put("generatorOptions", lv3);
        }
        if (this.field_24532 != null) {
            lv.putString("legacy_custom_options", this.field_24532);
        }
        lv.putBoolean("MapFeatures", this.method_28029());
        lv.putBoolean("BonusChest", this.method_28030());
        return lv;
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

    public Map<DimensionType, ChunkGenerator> method_28031() {
        return ImmutableMap.of((Object)DimensionType.OVERWORLD, (Object)this.field_24531, (Object)DimensionType.THE_NETHER, (Object)class_5285.method_28026(this.field_24526), (Object)DimensionType.THE_END, (Object)class_5285.method_28011(this.field_24526));
    }

    public ChunkGenerator method_28032() {
        return this.field_24531;
    }

    public boolean method_28033() {
        return this.field_24529 == class_5287.DEBUG_ALL_BLOCK_STATES;
    }

    public boolean method_28034() {
        return this.field_24529 == class_5287.FLAT;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_28035() {
        return this.field_24533;
    }

    public class_5285 method_28036() {
        return new class_5285(this.field_24526, this.field_24527, true, this.field_24529, this.field_24530, this.field_24531, this.field_24532, this.field_24533);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28037() {
        return new class_5285(this.field_24526, !this.field_24527, this.field_24528, this.field_24529, this.field_24530, this.field_24531);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28038() {
        return new class_5285(this.field_24526, this.field_24527, !this.field_24528, this.field_24529, this.field_24530, this.field_24531);
    }

    public static class_5285 method_28021(Properties properties) {
        class_5287 lv2;
        String string = (String)MoreObjects.firstNonNull((Object)((String)properties.get("generator-settings")), (Object)"");
        properties.put("generator-settings", string);
        String string2 = (String)MoreObjects.firstNonNull((Object)((String)properties.get("level-seed")), (Object)"");
        properties.put("level-seed", string2);
        String string3 = (String)properties.get("generate-structures");
        boolean bl = string3 == null || Boolean.parseBoolean(string3);
        properties.put("generate-structures", Objects.toString(bl));
        String string4 = (String)properties.get("level-type");
        if (string4 != null) {
            class_5287 lv = (class_5287)MoreObjects.firstNonNull((Object)class_5287.method_28048(string4), (Object)class_5287.DEFAULT);
        } else {
            lv2 = class_5287.DEFAULT;
        }
        properties.put("level-type", lv2.field_24551);
        JsonObject jsonObject = !string.isEmpty() ? JsonHelper.deserialize(string) : new JsonObject();
        long l = new Random().nextLong();
        if (!string2.isEmpty()) {
            try {
                long m = Long.parseLong(string2);
                if (m != 0L) {
                    l = m;
                }
            }
            catch (NumberFormatException numberFormatException) {
                l = string2.hashCode();
            }
        }
        Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject);
        return new class_5285(l, bl, false, lv2, dynamic, class_5285.method_28013(lv2, dynamic, l));
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28015(class_5288 arg) {
        return this.method_28014(arg.field_24557, field_24522, class_5285.method_28013(arg.field_24557, field_24522, this.field_24526));
    }

    @Environment(value=EnvType.CLIENT)
    private class_5285 method_28014(class_5287 arg, Dynamic<?> dynamic, ChunkGenerator arg2) {
        return new class_5285(this.field_24526, this.field_24527, this.field_24528, arg, dynamic, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28019(FlatChunkGeneratorConfig arg) {
        return this.method_28014(class_5287.FLAT, arg.toDynamic(NbtOps.INSTANCE), new FlatChunkGenerator(arg));
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28012(class_5286 arg, Set<Biome> set) {
        Dynamic<?> dynamic = class_5285.method_28027(arg, set);
        return this.method_28014(class_5287.BUFFET, dynamic, class_5285.method_28013(class_5287.BUFFET, dynamic, this.field_24526));
    }

    @Environment(value=EnvType.CLIENT)
    public class_5288 method_28039() {
        if (this.field_24529 == class_5287.CUSTOMIZED) {
            return class_5288.field_24552;
        }
        return field_24534.getOrDefault(this.field_24529, class_5288.field_24552);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5285 method_28024(boolean bl, OptionalLong optionalLong) {
        class_5285 lv3;
        ChunkGenerator lv;
        long l = optionalLong.orElse(this.field_24526);
        ChunkGenerator chunkGenerator = lv = optionalLong.isPresent() ? this.field_24531.create(optionalLong.getAsLong()) : this.field_24531;
        if (this.method_28033()) {
            class_5285 lv2 = new class_5285(l, false, false, this.field_24529, this.field_24530, lv);
        } else {
            lv3 = new class_5285(l, this.method_28029(), this.method_28030() && !bl, this.field_24529, this.field_24530, lv);
        }
        return lv3;
    }

    private static ChunkGenerator method_28013(class_5287 arg, Dynamic<?> dynamic, long l) {
        if (arg == class_5287.BUFFET) {
            BiomeSource lv = class_5285.method_28017(dynamic.get("biome_source"), l);
            OptionalDynamic dynamicLike = dynamic.get("chunk_generator");
            class_5286 lv2 = (class_5286)((Object)DataFixUtils.orElse(dynamicLike.get("type").asString().flatMap(string -> Optional.ofNullable(class_5286.method_28045(string))), (Object)((Object)class_5286.SURFACE)));
            OptionalDynamic dynamicLike2 = dynamicLike.get("options");
            BlockState lv3 = class_5285.method_28018(dynamicLike2.get("default_block"), Registry.BLOCK, Blocks.STONE).getDefaultState();
            BlockState lv4 = class_5285.method_28018(dynamicLike2.get("default_fluid"), Registry.BLOCK, Blocks.WATER).getDefaultState();
            switch (lv2) {
                case CAVES: {
                    CavesChunkGeneratorConfig lv5 = new CavesChunkGeneratorConfig(new ChunkGeneratorConfig());
                    lv5.setDefaultBlock(lv3);
                    lv5.setDefaultFluid(lv4);
                    return new CavesChunkGenerator(lv, l, lv5);
                }
                case FLOATING_ISLANDS: {
                    class_5284 lv6 = new class_5284(new ChunkGeneratorConfig());
                    lv6.setDefaultBlock(lv3);
                    lv6.setDefaultFluid(lv4);
                    return new FloatingIslandsChunkGenerator(lv, l, lv6);
                }
            }
            OverworldChunkGeneratorConfig lv7 = new OverworldChunkGeneratorConfig();
            lv7.setDefaultBlock(lv3);
            lv7.setDefaultFluid(lv4);
            return new OverworldChunkGenerator(lv, l, lv7);
        }
        if (arg == class_5287.FLAT) {
            FlatChunkGeneratorConfig lv8 = FlatChunkGeneratorConfig.fromDynamic(dynamic);
            return new FlatChunkGenerator(lv8);
        }
        if (arg == class_5287.DEBUG_ALL_BLOCK_STATES) {
            return DebugChunkGenerator.generator;
        }
        boolean bl = arg == class_5287.DEFAULT_1_1;
        int i = arg == class_5287.LARGE_BIOMES ? 6 : 4;
        boolean bl2 = arg == class_5287.AMPLIFIED;
        OverworldChunkGeneratorConfig lv9 = new OverworldChunkGeneratorConfig(new ChunkGeneratorConfig(), bl2);
        return new OverworldChunkGenerator(new VanillaLayeredBiomeSource(l, bl, i), l, lv9);
    }

    private static <T> T method_28018(DynamicLike<?> dynamicLike, Registry<T> arg, T object) {
        return (T)dynamicLike.asString().map(Identifier::new).flatMap(arg::getOrEmpty).orElse(object);
    }

    private static BiomeSource method_28017(DynamicLike<?> dynamicLike, long l) {
        BiomeSourceType lv = class_5285.method_28018(dynamicLike.get("type"), Registry.BIOME_SOURCE_TYPE, BiomeSourceType.FIXED);
        OptionalDynamic dynamicLike2 = dynamicLike.get("options");
        Stream stream2 = dynamicLike2.get("biomes").asStreamOpt().map(stream -> stream.map(dynamic -> class_5285.method_28018(dynamic, Registry.BIOME, Biomes.OCEAN))).orElseGet(Stream::empty);
        if (BiomeSourceType.CHECKERBOARD == lv) {
            Biome[] arrbiome;
            int i = dynamicLike2.get("size").asInt(2);
            Biome[] lvs = (Biome[])stream2.toArray(Biome[]::new);
            if (lvs.length > 0) {
                arrbiome = lvs;
            } else {
                Biome[] arrbiome2 = new Biome[1];
                arrbiome = arrbiome2;
                arrbiome2[0] = Biomes.OCEAN;
            }
            Biome[] lvs2 = arrbiome;
            return new CheckerboardBiomeSource(lvs2, i);
        }
        if (BiomeSourceType.VANILLA_LAYERED == lv) {
            return new VanillaLayeredBiomeSource(l, false, 4);
        }
        Biome lv2 = stream2.findFirst().orElse(Biomes.OCEAN);
        return new FixedBiomeSource(lv2);
    }

    @Environment(value=EnvType.CLIENT)
    private static Dynamic<?> method_28027(class_5286 arg, Set<Biome> set) {
        CompoundTag lv = new CompoundTag();
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
        CompoundTag lv3 = new CompoundTag();
        ListTag lv4 = new ListTag();
        for (Biome lv5 : set) {
            lv4.add(StringTag.of(Registry.BIOME.getId(lv5).toString()));
        }
        lv3.put("biomes", lv4);
        lv2.put("options", lv3);
        CompoundTag lv6 = new CompoundTag();
        CompoundTag lv7 = new CompoundTag();
        lv6.putString("type", arg.method_28046());
        lv7.putString("default_block", "minecraft:stone");
        lv7.putString("default_fluid", "minecraft:water");
        lv6.put("options", lv7);
        lv.put("biome_source", lv2);
        lv.put("chunk_generator", lv6);
        return new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv);
    }

    @Environment(value=EnvType.CLIENT)
    public FlatChunkGeneratorConfig method_28040() {
        return this.field_24529 == class_5287.FLAT ? FlatChunkGeneratorConfig.fromDynamic(this.field_24530) : FlatChunkGeneratorConfig.getDefaultConfig();
    }

    @Environment(value=EnvType.CLIENT)
    public Pair<class_5286, Set<Biome>> method_28041() {
        if (this.field_24529 != class_5287.BUFFET) {
            return Pair.of((Object)((Object)class_5286.SURFACE), (Object)ImmutableSet.of());
        }
        class_5286 lv = class_5286.SURFACE;
        HashSet set = Sets.newHashSet();
        CompoundTag lv2 = (CompoundTag)this.field_24530.convert((DynamicOps)NbtOps.INSTANCE).getValue();
        if (lv2.contains("chunk_generator", 10) && lv2.getCompound("chunk_generator").contains("type", 8)) {
            String string = lv2.getCompound("chunk_generator").getString("type");
            lv = class_5286.method_28045(string);
        }
        if (lv2.contains("biome_source", 10) && lv2.getCompound("biome_source").contains("biomes", 9)) {
            ListTag lv3 = lv2.getCompound("biome_source").getList("biomes", 8);
            for (int i = 0; i < lv3.size(); ++i) {
                Identifier lv4 = new Identifier(lv3.getString(i));
                Biome lv5 = Registry.BIOME.get(lv4);
                set.add(lv5);
            }
        }
        return Pair.of((Object)((Object)lv), (Object)set);
    }

    public static enum class_5286 {
        SURFACE("minecraft:surface"),
        CAVES("minecraft:caves"),
        FLOATING_ISLANDS("minecraft:floating_islands");

        private static final Map<String, class_5286> field_24539;
        private final String field_24540;

        private class_5286(String string2) {
            this.field_24540 = string2;
        }

        @Environment(value=EnvType.CLIENT)
        public Text method_28043() {
            return new TranslatableText("createWorld.customize.buffet.generatortype").append(" ").append(new TranslatableText(Util.createTranslationKey("generator", new Identifier(this.field_24540))));
        }

        private String method_28046() {
            return this.field_24540;
        }

        @Nullable
        public static class_5286 method_28045(String string) {
            return field_24539.get(string);
        }

        static {
            field_24539 = Arrays.stream(class_5286.values()).collect(Collectors.toMap(class_5286::method_28046, Function.identity()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_5288 {
        public static final class_5288 field_24552 = new class_5288(class_5287.DEFAULT);
        public static final class_5288 field_24553 = new class_5288(class_5287.FLAT);
        public static final class_5288 field_24554 = new class_5288(class_5287.AMPLIFIED);
        public static final class_5288 field_24555 = new class_5288(class_5287.BUFFET);
        public static final List<class_5288> field_24556 = Lists.newArrayList((Object[])new class_5288[]{field_24552, field_24553, new class_5288(class_5287.LARGE_BIOMES), field_24554, field_24555, new class_5288(class_5287.DEBUG_ALL_BLOCK_STATES)});
        private final class_5287 field_24557;
        private final Text field_24558;

        private class_5288(class_5287 arg) {
            this.field_24557 = arg;
            field_24534.put(arg, this);
            this.field_24558 = new TranslatableText("generator." + arg.field_24551);
        }

        public Text method_28049() {
            return this.field_24558;
        }
    }

    static class class_5287 {
        private static final Set<class_5287> field_24550 = Sets.newHashSet();
        public static final class_5287 DEFAULT = new class_5287("default");
        public static final class_5287 FLAT = new class_5287("flat");
        public static final class_5287 LARGE_BIOMES = new class_5287("largeBiomes");
        public static final class_5287 AMPLIFIED = new class_5287("amplified");
        public static final class_5287 BUFFET = new class_5287("buffet");
        public static final class_5287 DEBUG_ALL_BLOCK_STATES = new class_5287("debug_all_block_states");
        public static final class_5287 CUSTOMIZED = new class_5287("customized");
        public static final class_5287 DEFAULT_1_1 = new class_5287("default_1_1");
        private final String field_24551;

        private class_5287(String string) {
            this.field_24551 = string;
            field_24550.add(this);
        }

        @Nullable
        public static class_5287 method_28048(String string) {
            for (class_5287 lv : field_24550) {
                if (!lv.field_24551.equalsIgnoreCase(string)) continue;
                return lv;
            }
            return null;
        }
    }
}

