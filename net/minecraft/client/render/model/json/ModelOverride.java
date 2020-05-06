/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ModelOverride {
    private final Identifier modelId;
    private final Map<Identifier, Float> predicateToThresholds;

    public ModelOverride(Identifier arg, Map<Identifier, Float> map) {
        this.modelId = arg;
        this.predicateToThresholds = map;
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    boolean matches(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3) {
        Item lv = arg.getItem();
        for (Map.Entry<Identifier, Float> entry : this.predicateToThresholds.entrySet()) {
            ModelPredicateProvider lv2 = ModelPredicateProviderRegistry.get(lv, entry.getKey());
            if (lv2 != null && !(lv2.call(arg, arg2, arg3) < entry.getValue().floatValue())) continue;
            return false;
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelOverride> {
        protected Deserializer() {
        }

        public ModelOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "model"));
            Map<Identifier, Float> map = this.deserializeMinPropertyValues(jsonObject);
            return new ModelOverride(lv, map);
        }

        protected Map<Identifier, Float> deserializeMinPropertyValues(JsonObject jsonObject) {
            LinkedHashMap map = Maps.newLinkedHashMap();
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "predicate");
            for (Map.Entry entry : jsonObject2.entrySet()) {
                map.put(new Identifier((String)entry.getKey()), Float.valueOf(JsonHelper.asFloat((JsonElement)entry.getValue(), (String)entry.getKey())));
            }
            return map;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

