/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.class_5321;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T>
implements Codec<T>,
Keyable,
IndexedIterable<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final Identifier field_25100 = new Identifier("root");
    protected static final MutableRegistry<MutableRegistry<?>> field_25101 = new SimpleRegistry(Registry.method_29106("root"), Lifecycle.experimental());
    public static final Registry<? extends Registry<?>> REGISTRIES = field_25101;
    public static final class_5321<Registry<SoundEvent>> SOUND_EVENT_KEY = Registry.method_29106("sound_event");
    public static final class_5321<Registry<Fluid>> FLUID_KEY = Registry.method_29106("fluid");
    public static final class_5321<Registry<StatusEffect>> MOB_EFFECT_KEY = Registry.method_29106("mob_effect");
    public static final class_5321<Registry<Block>> BLOCK_KEY = Registry.method_29106("block");
    public static final class_5321<Registry<Enchantment>> ENCHANTMENT_KEY = Registry.method_29106("enchantment");
    public static final class_5321<Registry<EntityType<?>>> ENTITY_TYPE_KEY = Registry.method_29106("entity_type");
    public static final class_5321<Registry<Item>> ITEM_KEY = Registry.method_29106("item");
    public static final class_5321<Registry<Potion>> POTION_KEY = Registry.method_29106("potion");
    public static final class_5321<Registry<Carver<?>>> CARVER_KEY = Registry.method_29106("carver");
    public static final class_5321<Registry<SurfaceBuilder<?>>> SURFACE_BUILD_KEY = Registry.method_29106("surface_builder");
    public static final class_5321<Registry<Feature<?>>> FEATURE_KEY = Registry.method_29106("feature");
    public static final class_5321<Registry<Decorator<?>>> DECORATOR_KEY = Registry.method_29106("decorator");
    public static final class_5321<Registry<Biome>> BIOME_KEY = Registry.method_29106("biome");
    public static final class_5321<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_KEY = Registry.method_29106("block_state_provider_type");
    public static final class_5321<Registry<BlockPlacerType<?>>> BLOCK_PLACER_TYPE_KEY = Registry.method_29106("block_placer_type");
    public static final class_5321<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_KEY = Registry.method_29106("foliage_placer_type");
    public static final class_5321<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_KEY = Registry.method_29106("trunk_placer_type");
    public static final class_5321<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_KEY = Registry.method_29106("tree_decorator_type");
    public static final class_5321<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_KEY = Registry.method_29106("feature_size_type");
    public static final class_5321<Registry<ParticleType<?>>> PARTICLE_TYPE_KEY = Registry.method_29106("particle_type");
    public static final class_5321<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_KEY = Registry.method_29106("biome_source");
    public static final class_5321<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_KEY = Registry.method_29106("chunk_generator");
    public static final class_5321<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_KEY = Registry.method_29106("block_entity_type");
    public static final class_5321<Registry<PaintingMotive>> MOTIVE_KEY = Registry.method_29106("motive");
    public static final class_5321<Registry<Identifier>> CUSTOM_STAT_KEY = Registry.method_29106("custom_stat");
    public static final class_5321<Registry<ChunkStatus>> CHUNK_STATUS_KEY = Registry.method_29106("chunk_status");
    public static final class_5321<Registry<StructureFeature<?>>> STRUCTURE_FEATURE_KEY = Registry.method_29106("structure_feature");
    public static final class_5321<Registry<StructurePieceType>> STRUCTURE_PIECE_KEY = Registry.method_29106("structure_piece");
    public static final class_5321<Registry<RuleTestType<?>>> RULE_TEST_KEY = Registry.method_29106("rule_test");
    public static final class_5321<Registry<PosRuleTestType<?>>> POS_RULE_TEST_KEY = Registry.method_29106("pos_rule_test");
    public static final class_5321<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_KEY = Registry.method_29106("structure_processor");
    public static final class_5321<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_KEY = Registry.method_29106("structure_pool_element");
    public static final class_5321<Registry<ScreenHandlerType<?>>> MENU_KEY = Registry.method_29106("menu");
    public static final class_5321<Registry<RecipeType<?>>> RECIPE_TYPE_KEY = Registry.method_29106("recipe_type");
    public static final class_5321<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER_KEY = Registry.method_29106("recipe_serializer");
    public static final class_5321<Registry<EntityAttribute>> ATTRIBUTES_KEY = Registry.method_29106("attributes");
    public static final class_5321<Registry<StatType<?>>> STAT_TYPE_KEY = Registry.method_29106("stat_type");
    public static final class_5321<Registry<VillagerType>> VILLAGER_TYPE_KEY = Registry.method_29106("villager_type");
    public static final class_5321<Registry<VillagerProfession>> VILLAGER_PROFESSION_KEY = Registry.method_29106("villager_profession");
    public static final class_5321<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE_KEY = Registry.method_29106("point_of_interest_type");
    public static final class_5321<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_KEY = Registry.method_29106("memory_module_type");
    public static final class_5321<Registry<SensorType<?>>> SENSOR_TYPE_KEY = Registry.method_29106("sensor_type");
    public static final class_5321<Registry<Schedule>> SCHEDULE_KEY = Registry.method_29106("schedule");
    public static final class_5321<Registry<Activity>> ACTIVITY_KEY = Registry.method_29106("activity");
    public static final class_5321<Registry<DimensionType>> DIMENSION_TYPE_KEY = Registry.method_29106("dimension_type");
    public static final Registry<SoundEvent> SOUND_EVENT = Registry.create(SOUND_EVENT_KEY, () -> SoundEvents.ENTITY_ITEM_PICKUP);
    public static final DefaultedRegistry<Fluid> FLUID = Registry.create(FLUID_KEY, "empty", () -> Fluids.EMPTY);
    public static final Registry<StatusEffect> STATUS_EFFECT = Registry.create(MOB_EFFECT_KEY, () -> StatusEffects.LUCK);
    public static final DefaultedRegistry<Block> BLOCK = Registry.create(BLOCK_KEY, "air", () -> Blocks.AIR);
    public static final Registry<Enchantment> ENCHANTMENT = Registry.create(ENCHANTMENT_KEY, () -> Enchantments.FORTUNE);
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = Registry.create(ENTITY_TYPE_KEY, "pig", () -> EntityType.PIG);
    public static final DefaultedRegistry<Item> ITEM = Registry.create(ITEM_KEY, "air", () -> Items.AIR);
    public static final DefaultedRegistry<Potion> POTION = Registry.create(POTION_KEY, "empty", () -> Potions.EMPTY);
    public static final Registry<Carver<?>> CARVER = Registry.create(CARVER_KEY, () -> Carver.CAVE);
    public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = Registry.create(SURFACE_BUILD_KEY, () -> SurfaceBuilder.DEFAULT);
    public static final Registry<Feature<?>> FEATURE = Registry.create(FEATURE_KEY, () -> Feature.ORE);
    public static final Registry<Decorator<?>> DECORATOR = Registry.create(DECORATOR_KEY, () -> Decorator.NOPE);
    public static final Registry<Biome> BIOME = Registry.create(BIOME_KEY, () -> Biomes.DEFAULT);
    public static final Registry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = Registry.create(BLOCK_STATE_PROVIDER_TYPE_KEY, () -> BlockStateProviderType.SIMPLE_STATE_PROVIDER);
    public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPE = Registry.create(BLOCK_PLACER_TYPE_KEY, () -> BlockPlacerType.SIMPLE_BLOCK_PLACER);
    public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = Registry.create(FOLIAGE_PLACER_TYPE_KEY, () -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = Registry.create(TRUNK_PLACER_TYPE_KEY, () -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
    public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = Registry.create(TREE_DECORATOR_TYPE_KEY, () -> TreeDecoratorType.LEAVE_VINE);
    public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = Registry.create(FEATURE_SIZE_TYPE_KEY, () -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = Registry.create(PARTICLE_TYPE_KEY, () -> ParticleTypes.BLOCK);
    public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = Registry.method_29108(BIOME_SOURCE_KEY, Lifecycle.stable(), () -> BiomeSource.field_24713);
    public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = Registry.method_29108(CHUNK_GENERATOR_KEY, Lifecycle.stable(), () -> ChunkGenerator.field_24746);
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = Registry.create(BLOCK_ENTITY_TYPE_KEY, () -> BlockEntityType.FURNACE);
    public static final DefaultedRegistry<PaintingMotive> PAINTING_MOTIVE = Registry.create(MOTIVE_KEY, "kebab", () -> PaintingMotive.KEBAB);
    public static final Registry<Identifier> CUSTOM_STAT = Registry.create(CUSTOM_STAT_KEY, () -> Stats.JUMP);
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = Registry.create(CHUNK_STATUS_KEY, "empty", () -> ChunkStatus.EMPTY);
    public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = Registry.create(STRUCTURE_FEATURE_KEY, () -> StructureFeature.MINESHAFT);
    public static final Registry<StructurePieceType> STRUCTURE_PIECE = Registry.create(STRUCTURE_PIECE_KEY, () -> StructurePieceType.MINESHAFT_ROOM);
    public static final Registry<RuleTestType<?>> RULE_TEST = Registry.create(RULE_TEST_KEY, () -> RuleTestType.ALWAYS_TRUE);
    public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = Registry.create(POS_RULE_TEST_KEY, () -> PosRuleTestType.ALWAYS_TRUE);
    public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = Registry.create(STRUCTURE_PROCESSOR_KEY, () -> StructureProcessorType.BLOCK_IGNORE);
    public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = Registry.create(STRUCTURE_POOL_ELEMENT_KEY, () -> StructurePoolElementType.EMPTY_POOL_ELEMENT);
    public static final Registry<ScreenHandlerType<?>> SCREEN_HANDLER = Registry.create(MENU_KEY, () -> ScreenHandlerType.ANVIL);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = Registry.create(RECIPE_TYPE_KEY, () -> RecipeType.CRAFTING);
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = Registry.create(RECIPE_SERIALIZER_KEY, () -> RecipeSerializer.SHAPELESS);
    public static final Registry<EntityAttribute> ATTRIBUTES = Registry.create(ATTRIBUTES_KEY, () -> EntityAttributes.GENERIC_LUCK);
    public static final Registry<StatType<?>> STAT_TYPE = Registry.create(STAT_TYPE_KEY, () -> Stats.USED);
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = Registry.create(VILLAGER_TYPE_KEY, "plains", () -> VillagerType.PLAINS);
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = Registry.create(VILLAGER_PROFESSION_KEY, "none", () -> VillagerProfession.NONE);
    public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = Registry.create(POINT_OF_INTEREST_TYPE_KEY, "unemployed", () -> PointOfInterestType.UNEMPLOYED);
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = Registry.create(MEMORY_MODULE_TYPE_KEY, "dummy", () -> MemoryModuleType.DUMMY);
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = Registry.create(SENSOR_TYPE_KEY, "dummy", () -> SensorType.DUMMY);
    public static final Registry<Schedule> SCHEDULE = Registry.create(SCHEDULE_KEY, () -> Schedule.EMPTY);
    public static final Registry<Activity> ACTIVITY = Registry.create(ACTIVITY_KEY, () -> Activity.IDLE);
    private final class_5321<Registry<T>> field_25098;
    private final Lifecycle field_25099;

    private static <T> class_5321<Registry<T>> method_29106(String string) {
        return class_5321.method_29180(new Identifier(string));
    }

    private static <T extends MutableRegistry<?>> void method_29103(MutableRegistry<T> arg) {
        arg.forEach(arg2 -> {
            if (arg2.getIds().isEmpty()) {
                LOGGER.error("Registry '{}' was empty after loading", (Object)arg.getId(arg2));
                if (SharedConstants.isDevelopment) {
                    throw new IllegalStateException("Registry: '" + arg.getId(arg2) + "' is empty, not allowed, fix me!");
                }
            }
            if (arg2 instanceof DefaultedRegistry) {
                Identifier lv = ((DefaultedRegistry)arg2).getDefaultId();
                Validate.notNull(arg2.get(lv), (String)("Missing default of DefaultedMappedRegistry: " + lv), (Object[])new Object[0]);
            }
        });
    }

    private static <T> Registry<T> create(class_5321<Registry<T>> arg, Supplier<T> supplier) {
        return Registry.method_29108(arg, Lifecycle.experimental(), supplier);
    }

    private static <T> DefaultedRegistry<T> create(class_5321<Registry<T>> arg, String string, Supplier<T> supplier) {
        return Registry.method_29109(arg, string, Lifecycle.experimental(), supplier);
    }

    private static <T> Registry<T> method_29108(class_5321<Registry<T>> arg, Lifecycle lifecycle, Supplier<T> supplier) {
        return Registry.putDefaultEntry(arg, new SimpleRegistry<T>(arg, lifecycle), supplier);
    }

    private static <T> DefaultedRegistry<T> method_29109(class_5321<Registry<T>> arg, String string, Lifecycle lifecycle, Supplier<T> supplier) {
        return Registry.putDefaultEntry(arg, new DefaultedRegistry<T>(string, arg, lifecycle), supplier);
    }

    private static <T, R extends MutableRegistry<T>> R putDefaultEntry(class_5321<Registry<T>> arg, R arg2, Supplier<T> supplier) {
        Identifier lv = arg.method_29177();
        DEFAULT_ENTRIES.put(lv, supplier);
        MutableRegistry<MutableRegistry<?>> lv2 = field_25101;
        return lv2.add(arg, arg2);
    }

    protected Registry(class_5321<Registry<T>> arg, Lifecycle lifecycle) {
        this.field_25098 = arg;
        this.field_25099 = lifecycle;
    }

    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicOps, U object2) {
        if (dynamicOps.compressMaps()) {
            return dynamicOps.getNumberValue(object2).flatMap(number -> {
                int i = number.intValue();
                if (!this.method_29111(i)) {
                    return DataResult.error((String)("Unknown registry id: " + number));
                }
                Object object = this.get(i);
                return DataResult.success((Object)object, (Lifecycle)this.field_25099);
            }).map(object -> Pair.of((Object)object, (Object)dynamicOps.empty()));
        }
        return Identifier.field_25139.decode(dynamicOps, object2).addLifecycle(this.field_25099).flatMap(pair -> {
            if (!this.containsId((Identifier)pair.getFirst())) {
                return DataResult.error((String)("Unknown registry key: " + pair.getFirst()));
            }
            return DataResult.success((Object)pair.mapFirst(this::get), (Lifecycle)this.field_25099);
        });
    }

    public <U> DataResult<U> encode(T object, DynamicOps<U> dynamicOps, U object2) {
        Identifier lv = this.getId(object);
        if (lv == null) {
            return DataResult.error((String)("Unknown registry element " + object));
        }
        if (dynamicOps.compressMaps()) {
            return dynamicOps.mergeToPrimitive(object2, dynamicOps.createInt(this.getRawId(object))).setLifecycle(this.field_25099);
        }
        return dynamicOps.mergeToPrimitive(object2, dynamicOps.createString(lv.toString())).setLifecycle(this.field_25099);
    }

    public <U> Stream<U> keys(DynamicOps<U> dynamicOps) {
        return this.getIds().stream().map(arg -> dynamicOps.createString(arg.toString()));
    }

    @Nullable
    public abstract Identifier getId(T var1);

    public abstract class_5321<T> method_29113(T var1);

    public abstract int getRawId(@Nullable T var1);

    @Nullable
    public abstract T method_29107(@Nullable class_5321<T> var1);

    @Nullable
    public abstract T get(@Nullable Identifier var1);

    public abstract Optional<T> getOrEmpty(@Nullable Identifier var1);

    public abstract Set<Identifier> getIds();

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean containsId(Identifier var1);

    public abstract boolean method_29112(class_5321<T> var1);

    public abstract boolean method_29111(int var1);

    public static <T> T register(Registry<? super T> arg, String string, T object) {
        return Registry.register(arg, new Identifier(string), object);
    }

    public static <V, T extends V> T register(Registry<V> arg, Identifier arg2, T object) {
        return ((MutableRegistry)arg).add(class_5321.method_29179(arg.field_25098, arg2), object);
    }

    public static <V, T extends V> T register(Registry<V> arg, int i, String string, T object) {
        return ((MutableRegistry)arg).set(i, class_5321.method_29179(arg.field_25098, new Identifier(string)), object);
    }

    static {
        DEFAULT_ENTRIES.forEach((? super K arg, ? super V supplier) -> {
            if (supplier.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", arg);
            }
        });
        Registry.method_29103(field_25101);
    }
}

