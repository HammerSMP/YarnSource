/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.tuple.Triple
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelLoader {
    public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/fire_0"));
    public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/fire_1"));
    public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/lava_flow"));
    public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/water_flow"));
    public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/water_overlay"));
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/shield_base_nopattern"));
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(i -> new Identifier("block/destroy_stage_" + i)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(arg -> new Identifier("textures/" + arg.getPath() + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
    private static final Set<SpriteIdentifier> DEFAULT_TEXTURES = Util.make(Sets.newHashSet(), hashSet -> {
        hashSet.add(WATER_FLOW);
        hashSet.add(LAVA_FLOW);
        hashSet.add(WATER_OVERLAY);
        hashSet.add(FIRE_0);
        hashSet.add(FIRE_1);
        hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.BASE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.CAGE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.WIND_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.WIND_VERTICAL_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.OPEN_EYE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.CLOSED_EYE_TEXTURE);
        hashSet.add(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE);
        hashSet.add(BANNER_BASE);
        hashSet.add(SHIELD_BASE);
        hashSet.add(SHIELD_BASE_NO_PATTERN);
        for (Identifier lv : BLOCK_DESTRUCTION_STAGES) {
            hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, lv));
        }
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT));
        TexturedRenderLayers.addDefaultTextures(hashSet::add);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
    private static final String field_21773 = MISSING.toString();
    @VisibleForTesting
    public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().getPath() + "',       'missingno': '" + MissingSprite.getMissingSpriteId().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap((Map)ImmutableMap.of((Object)"missing", (Object)MISSING_DEFINITION));
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on((char)'=').limit(2);
    public static final JsonUnbakedModel GENERATION_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"), arg -> {
        arg.id = "generation marker";
    });
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"side\"}"), arg -> {
        arg.id = "block entity marker";
    });
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(Block::getDefaultState, BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of((Object)new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    @Nullable
    private SpriteAtlasManager spriteAtlasManager;
    private final BlockColors blockColors;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, AffineTransformation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;
    private int nextStateId = 1;
    private final Object2IntMap<BlockState> stateLookup = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager arg2, BlockColors arg23, Profiler arg3, int i) {
        this.resourceManager = arg2;
        this.blockColors = arg23;
        arg3.push("missing_model");
        try {
            this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
            this.addModel(MISSING);
        }
        catch (IOException iOException) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException);
            throw new RuntimeException(iOException);
        }
        arg3.swap("static_definitions");
        STATIC_DEFINITIONS.forEach((arg, arg22) -> arg22.getStates().forEach(arg2 -> this.addModel(BlockModels.getModelId(arg, arg2))));
        arg3.swap("blocks");
        for (Block lv : Registry.BLOCK) {
            lv.getStateManager().getStates().forEach(arg -> this.addModel(BlockModels.getModelId(arg)));
        }
        arg3.swap("items");
        for (Identifier lv2 : Registry.ITEM.getIds()) {
            this.addModel(new ModelIdentifier(lv2, "inventory"));
        }
        arg3.swap("special");
        this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        arg3.swap("textures");
        LinkedHashSet set = Sets.newLinkedHashSet();
        Set set2 = this.modelsToBake.values().stream().flatMap(arg -> arg.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(DEFAULT_TEXTURES);
        set.stream().filter(pair -> !((String)pair.getSecond()).equals(field_21773)).forEach(pair -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
        Map<Identifier, List<SpriteIdentifier>> map = set2.stream().collect(Collectors.groupingBy(SpriteIdentifier::getAtlasId));
        arg3.swap("stitching");
        this.spriteAtlasData = Maps.newHashMap();
        for (Map.Entry<Identifier, List<SpriteIdentifier>> entry : map.entrySet()) {
            SpriteAtlasTexture lv3 = new SpriteAtlasTexture(entry.getKey());
            SpriteAtlasTexture.Data lv4 = lv3.stitch(this.resourceManager, entry.getValue().stream().map(SpriteIdentifier::getTextureId), arg3, i);
            this.spriteAtlasData.put(entry.getKey(), (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>)Pair.of((Object)lv3, (Object)lv4));
        }
        arg3.pop();
    }

    public SpriteAtlasManager upload(TextureManager arg2, Profiler arg22) {
        arg22.push("atlas");
        for (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data> pair : this.spriteAtlasData.values()) {
            SpriteAtlasTexture lv = (SpriteAtlasTexture)pair.getFirst();
            SpriteAtlasTexture.Data lv2 = (SpriteAtlasTexture.Data)pair.getSecond();
            lv.upload(lv2);
            arg2.registerTexture(lv.getId(), lv);
            arg2.bindTexture(lv.getId());
            lv.applyTextureFilter(lv2);
        }
        this.spriteAtlasManager = new SpriteAtlasManager(this.spriteAtlasData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
        arg22.swap("baking");
        this.modelsToBake.keySet().forEach(arg -> {
            BakedModel lv = null;
            try {
                lv = this.bake((Identifier)arg, ModelRotation.X0_Y0);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", arg, (Object)exception);
            }
            if (lv != null) {
                this.bakedModels.put((Identifier)arg, lv);
            }
        });
        arg22.pop();
        return this.spriteAtlasManager;
    }

    private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> arg, String string) {
        HashMap map = Maps.newHashMap();
        for (String string2 : COMMA_SPLITTER.split((CharSequence)string)) {
            Iterator iterator = KEY_VALUE_SPLITTER.split((CharSequence)string2).iterator();
            if (!iterator.hasNext()) continue;
            String string3 = (String)iterator.next();
            Property<?> lv = arg.getProperty(string3);
            if (lv != null && iterator.hasNext()) {
                String string4 = (String)iterator.next();
                Object comparable = ModelLoader.getPropertyValue(lv, string4);
                if (comparable != null) {
                    map.put(lv, comparable);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + lv.getValues());
            }
            if (string3.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
        }
        Block lv2 = arg.getOwner();
        return arg2 -> {
            if (arg2 == null || lv2 != arg2.getBlock()) {
                return false;
            }
            for (Map.Entry entry : map.entrySet()) {
                if (Objects.equals(arg2.get((Property)entry.getKey()), entry.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    static <T extends Comparable<T>> T getPropertyValue(Property<T> arg, String string) {
        return (T)((Comparable)arg.parse(string).orElse(null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UnbakedModel getOrLoadModel(Identifier arg) {
        if (this.unbakedModels.containsKey(arg)) {
            return this.unbakedModels.get(arg);
        }
        if (this.modelsToLoad.contains(arg)) {
            throw new IllegalStateException("Circular reference while loading " + arg);
        }
        this.modelsToLoad.add(arg);
        UnbakedModel lv = this.unbakedModels.get(MISSING);
        while (!this.modelsToLoad.isEmpty()) {
            Identifier lv2 = this.modelsToLoad.iterator().next();
            try {
                if (this.unbakedModels.containsKey(lv2)) continue;
                this.loadModel(lv2);
            }
            catch (ModelLoaderException lv3) {
                LOGGER.warn(lv3.getMessage());
                this.unbakedModels.put(lv2, lv);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", (Object)lv2, (Object)arg, (Object)exception);
                this.unbakedModels.put(lv2, lv);
            }
            finally {
                this.modelsToLoad.remove(lv2);
            }
        }
        return this.unbakedModels.getOrDefault(arg, lv);
    }

    /*
     * WARNING - void declaration
     */
    private void loadModel(Identifier arg4) throws Exception {
        if (!(arg4 instanceof ModelIdentifier)) {
            this.putModel(arg4, this.loadModelFromJson(arg4));
            return;
        }
        ModelIdentifier lv = (ModelIdentifier)arg4;
        if (Objects.equals(lv.getVariant(), "inventory")) {
            Identifier lv2 = new Identifier(arg4.getNamespace(), "item/" + arg4.getPath());
            JsonUnbakedModel lv3 = this.loadModelFromJson(lv2);
            this.putModel(lv, lv3);
            this.unbakedModels.put(lv2, lv3);
        } else {
            Identifier lv4 = new Identifier(arg4.getNamespace(), arg4.getPath());
            StateManager lv5 = Optional.ofNullable(STATIC_DEFINITIONS.get(lv4)).orElseGet(() -> Registry.BLOCK.get(lv4).getStateManager());
            this.variantMapDeserializationContext.setStateFactory(lv5);
            ImmutableList list = ImmutableList.copyOf(this.blockColors.getProperties((Block)lv5.getOwner()));
            ImmutableList immutableList = lv5.getStates();
            HashMap map = Maps.newHashMap();
            immutableList.forEach(arg2 -> map.put(BlockModels.getModelId(lv4, arg2), arg2));
            HashMap map2 = Maps.newHashMap();
            Identifier lv6 = new Identifier(arg4.getNamespace(), "blockstates/" + arg4.getPath() + ".json");
            UnbakedModel lv7 = this.unbakedModels.get(MISSING);
            ModelDefinition lv8 = new ModelDefinition((List<UnbakedModel>)ImmutableList.of((Object)lv7), (List<Object>)ImmutableList.of());
            Pair pair = Pair.of((Object)lv7, () -> lv8);
            try {
                void list3;
                try {
                    List list2 = this.resourceManager.getAllResources(lv6).stream().map(arg -> {
                        try (InputStream inputStream = arg.getInputStream();){
                            Pair pair = Pair.of((Object)arg.getResourcePackName(), (Object)ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                            return pair;
                        }
                        catch (Exception exception) {
                            throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", arg.getId(), arg.getResourcePackName(), exception.getMessage()));
                        }
                    }).collect(Collectors.toList());
                }
                catch (IOException iOException) {
                    LOGGER.warn("Exception loading blockstate definition: {}: {}", (Object)lv6, (Object)iOException);
                    HashMap map3 = Maps.newHashMap();
                    map.forEach((arg22, arg3) -> {
                        Pair pair2 = (Pair)map2.get(arg3);
                        if (pair2 == null) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)lv6, arg22);
                            pair2 = pair;
                        }
                        this.putModel((Identifier)arg22, (UnbakedModel)pair2.getFirst());
                        try {
                            ModelDefinition lv = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                            map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(arg3);
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", arg22, (Object)exception);
                        }
                    });
                    map3.forEach((arg, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState lv = (BlockState)iterator.next();
                            if (lv.getRenderType() == BlockRenderType.MODEL) continue;
                            iterator.remove();
                            this.stateLookup.put((Object)lv, 0);
                        }
                        if (set.size() > 1) {
                            this.addStates((Iterable<BlockState>)set);
                        }
                    });
                    return;
                }
                for (Pair pair2 : list3) {
                    MultipartUnbakedModel lv11;
                    ModelVariantMap lv9 = (ModelVariantMap)pair2.getSecond();
                    IdentityHashMap map4 = Maps.newIdentityHashMap();
                    if (lv9.hasMultipartModel()) {
                        MultipartUnbakedModel lv10 = lv9.getMultipartModel();
                        immutableList.forEach(arg_0 -> ModelLoader.method_4738(map4, lv10, (List)list, arg_0));
                    } else {
                        lv11 = null;
                    }
                    lv9.getVariantMap().forEach((arg_0, arg_1) -> ModelLoader.method_4731(immutableList, lv5, map4, (List)list, lv11, pair, lv9, lv6, pair2, arg_0, arg_1));
                    map2.putAll(map4);
                }
            }
            catch (ModelLoaderException lv12) {
                throw lv12;
            }
            catch (Exception exception) {
                throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", lv6, exception));
            }
            finally {
                HashMap map6 = Maps.newHashMap();
                map.forEach((arg22, arg3) -> {
                    Pair pair2 = (Pair)map2.get(arg3);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)lv6, arg22);
                        pair2 = pair;
                    }
                    this.putModel((Identifier)arg22, (UnbakedModel)pair2.getFirst());
                    try {
                        ModelDefinition lv = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                        map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(arg3);
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", arg22, (Object)exception);
                    }
                });
                map6.forEach((arg, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState lv = (BlockState)iterator.next();
                        if (lv.getRenderType() == BlockRenderType.MODEL) continue;
                        iterator.remove();
                        this.stateLookup.put((Object)lv, 0);
                    }
                    if (set.size() > 1) {
                        this.addStates((Iterable<BlockState>)set);
                    }
                });
            }
        }
    }

    private void putModel(Identifier arg, UnbakedModel arg2) {
        this.unbakedModels.put(arg, arg2);
        this.modelsToLoad.addAll(arg2.getModelDependencies());
    }

    private void addModel(ModelIdentifier arg) {
        UnbakedModel lv = this.getOrLoadModel(arg);
        this.unbakedModels.put(arg, lv);
        this.modelsToBake.put(arg, lv);
    }

    private void addStates(Iterable<BlockState> iterable) {
        int i = this.nextStateId++;
        iterable.forEach(arg -> this.stateLookup.put(arg, i));
    }

    @Nullable
    public BakedModel bake(Identifier arg, ModelBakeSettings arg2) {
        JsonUnbakedModel lv2;
        Triple triple = Triple.of((Object)arg, (Object)arg2.getRotation(), (Object)arg2.isShaded());
        if (this.bakedModelCache.containsKey((Object)triple)) {
            return this.bakedModelCache.get((Object)triple);
        }
        if (this.spriteAtlasManager == null) {
            throw new IllegalStateException("bake called too early");
        }
        UnbakedModel lv = this.getOrLoadModel(arg);
        if (lv instanceof JsonUnbakedModel && (lv2 = (JsonUnbakedModel)lv).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, lv2).bake(this, lv2, this.spriteAtlasManager::getSprite, arg2, arg, false);
        }
        BakedModel lv3 = lv.bake(this, this.spriteAtlasManager::getSprite, arg2, arg);
        this.bakedModelCache.put((Triple<Identifier, AffineTransformation, Boolean>)triple, lv3);
        return lv3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonUnbakedModel loadModelFromJson(Identifier arg) throws IOException {
        String string;
        Resource lv;
        Reader reader;
        block8: {
            block7: {
                JsonUnbakedModel jsonUnbakedModel;
                reader = null;
                lv = null;
                try {
                    string = arg.getPath();
                    if (!"builtin/generated".equals(string)) break block7;
                    jsonUnbakedModel = GENERATION_MARKER;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(lv);
                    throw throwable;
                }
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(lv);
                return jsonUnbakedModel;
            }
            if (!"builtin/entity".equals(string)) break block8;
            JsonUnbakedModel jsonUnbakedModel = BLOCK_ENTITY_MARKER;
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(lv);
            return jsonUnbakedModel;
        }
        if (string.startsWith("builtin/")) {
            String string2 = string.substring("builtin/".length());
            String string3 = BUILTIN_MODEL_DEFINITIONS.get(string2);
            if (string3 == null) {
                throw new FileNotFoundException(arg.toString());
            }
            reader = new StringReader(string3);
        } else {
            lv = this.resourceManager.getResource(new Identifier(arg.getNamespace(), "models/" + arg.getPath() + ".json"));
            reader = new InputStreamReader(lv.getInputStream(), StandardCharsets.UTF_8);
        }
        JsonUnbakedModel lv2 = JsonUnbakedModel.deserialize(reader);
        lv2.id = arg.toString();
        JsonUnbakedModel jsonUnbakedModel = lv2;
        IOUtils.closeQuietly((Reader)reader);
        IOUtils.closeQuietly((Closeable)lv);
        return jsonUnbakedModel;
    }

    public Map<Identifier, BakedModel> getBakedModelMap() {
        return this.bakedModels;
    }

    public Object2IntMap<BlockState> getStateLookup() {
        return this.stateLookup;
    }

    private static /* synthetic */ void method_4731(ImmutableList immutableList, StateManager arg, Map map, List list, MultipartUnbakedModel arg2, Pair pair, ModelVariantMap arg3, Identifier arg42, Pair pair2, String string, WeightedUnbakedModel arg5) {
        try {
            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(arg, string)).forEach(arg4 -> {
                Pair pair2 = map.put(arg4, Pair.of((Object)arg5, () -> ModelDefinition.create(arg4, arg5, list)));
                if (pair2 != null && pair2.getFirst() != arg2) {
                    map.put(arg4, pair);
                    throw new RuntimeException("Overlapping definition with: " + (String)arg3.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair2.getFirst()).findFirst().get().getKey());
                }
            });
        }
        catch (Exception exception) {
            LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", (Object)arg42, pair2.getFirst(), (Object)string, (Object)exception.getMessage());
        }
    }

    private static /* synthetic */ void method_4738(Map map, MultipartUnbakedModel arg, List list, BlockState arg2) {
        map.put(arg2, Pair.of((Object)arg, () -> ModelDefinition.create(arg2, arg, list)));
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelDefinition {
        private final List<UnbakedModel> components;
        private final List<Object> values;

        public ModelDefinition(List<UnbakedModel> list, List<Object> list2) {
            this.components = list;
            this.values = list2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ModelDefinition) {
                ModelDefinition lv = (ModelDefinition)object;
                return Objects.equals(this.components, lv.components) && Objects.equals(this.values, lv.values);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.components.hashCode() + this.values.hashCode();
        }

        public static ModelDefinition create(BlockState arg, MultipartUnbakedModel arg2, Collection<Property<?>> collection) {
            StateManager<Block, BlockState> lv = arg.getBlock().getStateManager();
            List list = (List)arg2.getComponents().stream().filter(arg3 -> arg3.getPredicate(lv).test(arg)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            List<Object> list2 = ModelDefinition.getStateValues(arg, collection);
            return new ModelDefinition(list, list2);
        }

        public static ModelDefinition create(BlockState arg, UnbakedModel arg2, Collection<Property<?>> collection) {
            List<Object> list = ModelDefinition.getStateValues(arg, collection);
            return new ModelDefinition((List<UnbakedModel>)ImmutableList.of((Object)arg2), list);
        }

        private static List<Object> getStateValues(BlockState arg, Collection<Property<?>> collection) {
            return (List)collection.stream().map(arg::get).collect(ImmutableList.toImmutableList());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelLoaderException
    extends RuntimeException {
        public ModelLoaderException(String string) {
            super(string);
        }
    }
}

