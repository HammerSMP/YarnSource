/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ModelVariantMap {
    private final Map<String, WeightedUnbakedModel> variantMap = Maps.newLinkedHashMap();
    private MultipartUnbakedModel multipartModel;

    public static ModelVariantMap deserialize(DeserializationContext context, Reader reader) {
        return JsonHelper.deserialize(context.gson, reader, ModelVariantMap.class);
    }

    public ModelVariantMap(Map<String, WeightedUnbakedModel> variantMap, MultipartUnbakedModel multipartModel) {
        this.multipartModel = multipartModel;
        this.variantMap.putAll(variantMap);
    }

    public ModelVariantMap(List<ModelVariantMap> variantMapList) {
        ModelVariantMap lv = null;
        for (ModelVariantMap lv2 : variantMapList) {
            if (lv2.hasMultipartModel()) {
                this.variantMap.clear();
                lv = lv2;
            }
            this.variantMap.putAll(lv2.variantMap);
        }
        if (lv != null) {
            this.multipartModel = lv.multipartModel;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelVariantMap) {
            ModelVariantMap lv = (ModelVariantMap)o;
            if (this.variantMap.equals(lv.variantMap)) {
                return this.hasMultipartModel() ? this.multipartModel.equals(lv.multipartModel) : !lv.hasMultipartModel();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.variantMap.hashCode() + (this.hasMultipartModel() ? this.multipartModel.hashCode() : 0);
    }

    public Map<String, WeightedUnbakedModel> getVariantMap() {
        return this.variantMap;
    }

    public boolean hasMultipartModel() {
        return this.multipartModel != null;
    }

    public MultipartUnbakedModel getMultipartModel() {
        return this.multipartModel;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelVariantMap> {
        public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, WeightedUnbakedModel> map = this.deserializeVariants(jsonDeserializationContext, jsonObject);
            MultipartUnbakedModel lv = this.deserializeMultipart(jsonDeserializationContext, jsonObject);
            if (map.isEmpty() && (lv == null || lv.getModels().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new ModelVariantMap(map, lv);
        }

        protected Map<String, WeightedUnbakedModel> deserializeVariants(JsonDeserializationContext context, JsonObject object) {
            HashMap map = Maps.newHashMap();
            if (object.has("variants")) {
                JsonObject jsonObject2 = JsonHelper.getObject(object, "variants");
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    map.put(entry.getKey(), context.deserialize((JsonElement)entry.getValue(), WeightedUnbakedModel.class));
                }
            }
            return map;
        }

        @Nullable
        protected MultipartUnbakedModel deserializeMultipart(JsonDeserializationContext context, JsonObject object) {
            if (!object.has("multipart")) {
                return null;
            }
            JsonArray jsonArray = JsonHelper.getArray(object, "multipart");
            return (MultipartUnbakedModel)context.deserialize((JsonElement)jsonArray, MultipartUnbakedModel.class);
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DeserializationContext {
        protected final Gson gson = new GsonBuilder().registerTypeAdapter(ModelVariantMap.class, (Object)new Deserializer()).registerTypeAdapter(ModelVariant.class, (Object)new ModelVariant.Deserializer()).registerTypeAdapter(WeightedUnbakedModel.class, (Object)new WeightedUnbakedModel.Deserializer()).registerTypeAdapter(MultipartUnbakedModel.class, (Object)new MultipartUnbakedModel.Deserializer(this)).registerTypeAdapter(MultipartModelComponent.class, (Object)new MultipartModelComponent.Deserializer()).create();
        private StateManager<Block, BlockState> stateFactory;

        public StateManager<Block, BlockState> getStateFactory() {
            return this.stateFactory;
        }

        public void setStateFactory(StateManager<Block, BlockState> stateFactory) {
            this.stateFactory = stateFactory;
        }
    }
}

