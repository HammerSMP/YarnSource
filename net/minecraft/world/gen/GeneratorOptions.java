/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorOptions {
    public static final Codec<GeneratorOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorOptions::getSeed), (App)Codec.BOOL.fieldOf("generate_features").withDefault((Object)true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), (App)Codec.BOOL.fieldOf("bonus_chest").withDefault((Object)false).stable().forGetter(GeneratorOptions::hasBonusChest), (App)SimpleRegistry.createCodec(Registry.DIMENSION_OPTIONS, Lifecycle.stable(), DimensionOptions.CODEC).xmap(DimensionOptions::method_29569, Function.identity()).fieldOf("dimensions").forGetter(GeneratorOptions::getDimensionMap), (App)Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(arg -> arg.legacyCustomOptions)).apply((Applicative)instance, instance.stable((Object)((Function5)GeneratorOptions::new)))).comapFlatMap(GeneratorOptions::method_28610, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DEMO_SEED = "North Carolina".hashCode();
    public static final GeneratorOptions DEMO_CONFIG = new GeneratorOptions(DEMO_SEED, true, true, GeneratorOptions.method_28608(DimensionType.method_28517(DEMO_SEED), GeneratorOptions.createOverworldGenerator(DEMO_SEED)));
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final SimpleRegistry<DimensionOptions> field_24827;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorOptions> method_28610() {
        if (this.method_28611()) {
            return DataResult.success((Object)this, (Lifecycle)Lifecycle.stable());
        }
        return DataResult.success((Object)this);
    }

    private boolean method_28611() {
        return DimensionOptions.method_29567(this.seed, this.field_24827);
    }

    public GeneratorOptions(long l, boolean bl, boolean bl2, SimpleRegistry<DimensionOptions> arg) {
        this(l, bl, bl2, arg, Optional.empty());
    }

    private GeneratorOptions(long l, boolean bl, boolean bl2, SimpleRegistry<DimensionOptions> arg, Optional<String> optional) {
        this.seed = l;
        this.generateStructures = bl;
        this.bonusChest = bl2;
        this.field_24827 = arg;
        this.legacyCustomOptions = optional;
    }

    public static GeneratorOptions getDefaultOptions() {
        long l = new Random().nextLong();
        return new GeneratorOptions(l, true, false, GeneratorOptions.method_28608(DimensionType.method_28517(l), GeneratorOptions.createOverworldGenerator(l)));
    }

    public static SurfaceChunkGenerator createOverworldGenerator(long l) {
        return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public static SimpleRegistry<DimensionOptions> method_28608(SimpleRegistry<DimensionOptions> arg, ChunkGenerator arg2) {
        DimensionOptions lv = arg.get(DimensionOptions.OVERWORLD);
        Supplier<DimensionType> supplier = () -> lv == null ? DimensionType.getOverworldDimensionType() : lv.getDimensionType();
        return GeneratorOptions.method_29962(arg, supplier, arg2);
    }

    public static SimpleRegistry<DimensionOptions> method_29962(SimpleRegistry<DimensionOptions> arg, Supplier<DimensionType> supplier, ChunkGenerator arg2) {
        SimpleRegistry<DimensionOptions> lv = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        lv.add(DimensionOptions.OVERWORLD, new DimensionOptions(supplier, arg2));
        lv.markLoaded(DimensionOptions.OVERWORLD);
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : arg.getEntries()) {
            RegistryKey<DimensionOptions> lv2 = entry.getKey();
            if (lv2 == DimensionOptions.OVERWORLD) continue;
            lv.add(lv2, entry.getValue());
            if (!arg.isLoaded(lv2)) continue;
            lv.markLoaded(lv2);
        }
        return lv;
    }

    public SimpleRegistry<DimensionOptions> getDimensionMap() {
        return this.field_24827;
    }

    public ChunkGenerator getChunkGenerator() {
        DimensionOptions lv = this.field_24827.get(DimensionOptions.OVERWORLD);
        if (lv == null) {
            return GeneratorOptions.createOverworldGenerator(new Random().nextLong());
        }
        return lv.getChunkGenerator();
    }

    public ImmutableSet<RegistryKey<World>> method_29575() {
        return (ImmutableSet)this.getDimensionMap().getEntries().stream().map(entry -> RegistryKey.of(Registry.DIMENSION, ((RegistryKey)entry.getKey()).getValue())).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof DebugChunkGenerator;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof FlatChunkGenerator;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, true, this.field_24827, this.legacyCustomOptions);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleGenerateStructures() {
        return new GeneratorOptions(this.seed, !this.generateStructures, this.bonusChest, this.field_24827);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions toggleBonusChest() {
        return new GeneratorOptions(this.seed, this.generateStructures, !this.bonusChest, this.field_24827);
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions method_29573(SimpleRegistry<DimensionOptions> arg) {
        return new GeneratorOptions(this.seed, this.generateStructures, this.bonusChest, arg);
    }

    public static GeneratorOptions fromProperties(Properties properties) {
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
        SimpleRegistry<DimensionOptions> lv = DimensionType.method_28517(l);
        switch (string5) {
            case "flat": {
                JsonObject jsonObject = !string2.isEmpty() ? JsonHelper.deserialize(string2) : new JsonObject();
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject);
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(lv, new FlatChunkGenerator(FlatChunkGeneratorConfig.CODEC.parse(dynamic).resultOrPartial(((Logger)LOGGER)::error).orElseGet(FlatChunkGeneratorConfig::getDefaultConfig))));
            }
            case "debug_all_block_states": {
                return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(lv, DebugChunkGenerator.INSTANCE));
            }
        }
        return new GeneratorOptions(l, bl, false, GeneratorOptions.method_28608(lv, GeneratorOptions.createOverworldGenerator(l)));
    }

    @Environment(value=EnvType.CLIENT)
    public GeneratorOptions withHardcore(boolean bl, OptionalLong optionalLong) {
        GeneratorOptions lv5;
        SimpleRegistry<DimensionOptions> lv3;
        long l = optionalLong.orElse(this.seed);
        if (optionalLong.isPresent()) {
            SimpleRegistry<DimensionOptions> lv = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
            long m = optionalLong.getAsLong();
            for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : this.field_24827.getEntries()) {
                RegistryKey<DimensionOptions> lv2 = entry.getKey();
                lv.add(lv2, new DimensionOptions(entry.getValue().getDimensionTypeSupplier(), entry.getValue().getChunkGenerator().withSeed(m)));
                if (!this.field_24827.isLoaded(lv2)) continue;
                lv.markLoaded(lv2);
            }
        } else {
            lv3 = this.field_24827;
        }
        if (this.isDebugWorld()) {
            GeneratorOptions lv4 = new GeneratorOptions(l, false, false, lv3);
        } else {
            lv5 = new GeneratorOptions(l, this.shouldGenerateStructures(), this.hasBonusChest() && !bl, lv3);
        }
        return lv5;
    }
}

