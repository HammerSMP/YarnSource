/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, (Object)new Deserializer()).registerTypeAdapter(ModelElement.class, (Object)new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, (Object)new ModelElementFace.Deserializer()).registerTypeAdapter(ModelElementTexture.class, (Object)new ModelElementTexture.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, (Object)new ModelTransformation.Deserializer()).registerTypeAdapter(ModelOverride.class, (Object)new ModelOverride.Deserializer()).create();
    private final List<ModelElement> elements;
    @Nullable
    private final GuiLight guiLight;
    private final boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelOverride> overrides;
    public String id = "";
    @VisibleForTesting
    protected final Map<String, Either<SpriteIdentifier, String>> textureMap;
    @Nullable
    protected JsonUnbakedModel parent;
    @Nullable
    protected Identifier parentId;

    public static JsonUnbakedModel deserialize(Reader input) {
        return JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
    }

    public static JsonUnbakedModel deserialize(String json) {
        return JsonUnbakedModel.deserialize(new StringReader(json));
    }

    public JsonUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, boolean ambientOcclusion, @Nullable GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight = guiLight;
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.transformations = transformations;
        this.overrides = overrides;
    }

    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public boolean useAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.useAmbientOcclusion();
        }
        return this.ambientOcclusion;
    }

    public GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        }
        if (this.parent != null) {
            return this.parent.getGuiLight();
        }
        return GuiLight.field_21859;
    }

    public List<ModelOverride> getOverrides() {
        return this.overrides;
    }

    private ModelOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel parent) {
        if (this.overrides.isEmpty()) {
            return ModelOverrideList.EMPTY;
        }
        return new ModelOverrideList(modelLoader, parent, modelLoader::getOrLoadModel, this.overrides);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        HashSet set = Sets.newHashSet();
        for (ModelOverride lv : this.overrides) {
            set.add(lv.getModelId());
        }
        if (this.parentId != null) {
            set.add(this.parentId);
        }
        return set;
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        LinkedHashSet set2 = Sets.newLinkedHashSet();
        JsonUnbakedModel lv = this;
        while (lv.parentId != null && lv.parent == null) {
            set2.add(lv);
            UnbakedModel lv2 = unbakedModelGetter.apply(lv.parentId);
            if (lv2 == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentId, (Object)lv);
            }
            if (set2.contains(lv2)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", (Object)lv, (Object)set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")), (Object)this.parentId);
                lv2 = null;
            }
            if (lv2 == null) {
                lv.parentId = ModelLoader.MISSING;
                lv2 = unbakedModelGetter.apply(lv.parentId);
            }
            if (!(lv2 instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            lv.parent = (JsonUnbakedModel)lv2;
            lv = lv.parent;
        }
        HashSet set3 = Sets.newHashSet((Object[])new SpriteIdentifier[]{this.resolveSprite("particle")});
        for (ModelElement lv3 : this.getElements()) {
            for (ModelElementFace lv4 : lv3.faces.values()) {
                SpriteIdentifier lv5 = this.resolveSprite(lv4.textureId);
                if (Objects.equals(lv5.getTextureId(), MissingSprite.getMissingSpriteId())) {
                    unresolvedTextureReferences.add((Pair<String, String>)Pair.of((Object)lv4.textureId, (Object)this.id));
                }
                set3.add(lv5);
            }
        }
        this.overrides.forEach(arg -> {
            UnbakedModel lv = (UnbakedModel)unbakedModelGetter.apply(arg.getModelId());
            if (Objects.equals(lv, this)) {
                return;
            }
            set3.addAll(lv.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
        });
        if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(string -> set3.add(this.resolveSprite((String)string)));
        }
        return set3;
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return this.bake(loader, this, textureGetter, rotationContainer, modelId, true);
    }

    public BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        Sprite lv = textureGetter.apply(this.resolveSprite("particle"));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(loader, parent), lv, this.getGuiLight().isSide());
        }
        BasicBakedModel.Builder lv2 = new BasicBakedModel.Builder(this, this.compileOverrides(loader, parent), hasDepth).setParticle(lv);
        for (ModelElement lv3 : this.getElements()) {
            for (Direction lv4 : lv3.faces.keySet()) {
                ModelElementFace lv5 = lv3.faces.get(lv4);
                Sprite lv6 = textureGetter.apply(this.resolveSprite(lv5.textureId));
                if (lv5.cullFace == null) {
                    lv2.addQuad(JsonUnbakedModel.createQuad(lv3, lv5, lv6, lv4, settings, id));
                    continue;
                }
                lv2.addQuad(Direction.transform(settings.getRotation().getMatrix(), lv5.cullFace), JsonUnbakedModel.createQuad(lv3, lv5, lv6, lv4, settings, id));
            }
        }
        return lv2.build();
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
        return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade, id);
    }

    public boolean textureExists(String name) {
        return !MissingSprite.getMissingSpriteId().equals(this.resolveSprite(name).getTextureId());
    }

    public SpriteIdentifier resolveSprite(String spriteName) {
        if (JsonUnbakedModel.isTextureReference(spriteName)) {
            spriteName = spriteName.substring(1);
        }
        ArrayList list = Lists.newArrayList();
        Either<SpriteIdentifier, String> either;
        Optional optional;
        while (!(optional = (either = this.resolveTexture(spriteName)).left()).isPresent()) {
            spriteName = (String)either.right().get();
            if (list.contains(spriteName)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", (Object)Joiner.on((String)"->").join((Iterable)list), (Object)spriteName, (Object)this.id);
                return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId());
            }
            list.add(spriteName);
        }
        return (SpriteIdentifier)optional.get();
    }

    private Either<SpriteIdentifier, String> resolveTexture(String name) {
        JsonUnbakedModel lv = this;
        while (lv != null) {
            Either<SpriteIdentifier, String> either = lv.textureMap.get(name);
            if (either != null) {
                return either;
            }
            lv = lv.parent;
        }
        return Either.left((Object)new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId()));
    }

    private static boolean isTextureReference(String reference) {
        return reference.charAt(0) == '#';
    }

    public JsonUnbakedModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ModelTransformation getTransformations() {
        Transformation lv = this.getTransformation(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND);
        Transformation lv2 = this.getTransformation(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
        Transformation lv3 = this.getTransformation(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND);
        Transformation lv4 = this.getTransformation(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
        Transformation lv5 = this.getTransformation(ModelTransformation.Mode.HEAD);
        Transformation lv6 = this.getTransformation(ModelTransformation.Mode.GUI);
        Transformation lv7 = this.getTransformation(ModelTransformation.Mode.GROUND);
        Transformation lv8 = this.getTransformation(ModelTransformation.Mode.FIXED);
        return new ModelTransformation(lv, lv2, lv3, lv4, lv5, lv6, lv7, lv8);
    }

    private Transformation getTransformation(ModelTransformation.Mode renderMode) {
        if (this.parent != null && !this.transformations.isTransformationDefined(renderMode)) {
            return this.parent.getTransformation(renderMode);
        }
        return this.transformations.getTransformation(renderMode);
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GuiLight {
        field_21858("front"),
        field_21859("side");

        private final String name;

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight deserialize(String value) {
            for (GuiLight lv : GuiLight.values()) {
                if (!lv.name.equals(value)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == field_21859;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.deserializeElements(jsonDeserializationContext, jsonObject);
            String string = this.deserializeParent(jsonObject);
            Map<String, Either<SpriteIdentifier, String>> map = this.deserializeTextures(jsonObject);
            boolean bl = this.deserializeAmbientOcclusion(jsonObject);
            ModelTransformation lv = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                lv = (ModelTransformation)jsonDeserializationContext.deserialize((JsonElement)jsonObject2, ModelTransformation.class);
            }
            List<ModelOverride> list2 = this.deserializeOverrides(jsonDeserializationContext, jsonObject);
            GuiLight lv2 = null;
            if (jsonObject.has("gui_light")) {
                lv2 = GuiLight.deserialize(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier lv3 = string.isEmpty() ? null : new Identifier(string);
            return new JsonUnbakedModel(lv3, list, map, bl, lv2, lv, list2);
        }

        protected List<ModelOverride> deserializeOverrides(JsonDeserializationContext context, JsonObject object) {
            ArrayList list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add(context.deserialize(jsonElement, ModelOverride.class));
                }
            }
            return list;
        }

        private Map<String, Either<SpriteIdentifier, String>> deserializeTextures(JsonObject object) {
            Identifier lv = SpriteAtlasTexture.BLOCK_ATLAS_TEX;
            HashMap map = Maps.newHashMap();
            if (object.has("textures")) {
                JsonObject jsonObject2 = JsonHelper.getObject(object, "textures");
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    map.put(entry.getKey(), Deserializer.resolveReference(lv, ((JsonElement)entry.getValue()).getAsString()));
                }
            }
            return map;
        }

        private static Either<SpriteIdentifier, String> resolveReference(Identifier id, String name) {
            if (JsonUnbakedModel.isTextureReference(name)) {
                return Either.right((Object)name.substring(1));
            }
            Identifier lv = Identifier.tryParse(name);
            if (lv == null) {
                throw new JsonParseException(name + " is not valid resource location");
            }
            return Either.left((Object)new SpriteIdentifier(id, lv));
        }

        private String deserializeParent(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        protected boolean deserializeAmbientOcclusion(JsonObject json) {
            return JsonHelper.getBoolean(json, "ambientocclusion", true);
        }

        protected List<ModelElement> deserializeElements(JsonDeserializationContext context, JsonObject json) {
            ArrayList list = Lists.newArrayList();
            if (json.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add(context.deserialize(jsonElement, ModelElement.class));
                }
            }
            return list;
        }

        public /* synthetic */ Object deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
            return this.deserialize(element, unused, ctx);
        }
    }
}

