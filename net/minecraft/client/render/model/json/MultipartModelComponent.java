/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class MultipartModelComponent {
    private final MultipartModelSelector selector;
    private final WeightedUnbakedModel model;

    public MultipartModelComponent(MultipartModelSelector selector, WeightedUnbakedModel model) {
        if (selector == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if (model == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.selector = selector;
        this.model = model;
    }

    public WeightedUnbakedModel getModel() {
        return this.model;
    }

    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateFactory) {
        return this.selector.getPredicate(stateFactory);
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<MultipartModelComponent> {
        public MultipartModelComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return new MultipartModelComponent(this.deserializeSelectorOrDefault(jsonObject), (WeightedUnbakedModel)jsonDeserializationContext.deserialize(jsonObject.get("apply"), WeightedUnbakedModel.class));
        }

        private MultipartModelSelector deserializeSelectorOrDefault(JsonObject object) {
            if (object.has("when")) {
                return Deserializer.deserializeSelector(JsonHelper.getObject(object, "when"));
            }
            return MultipartModelSelector.TRUE;
        }

        @VisibleForTesting
        static MultipartModelSelector deserializeSelector(JsonObject object) {
            Set set = object.entrySet();
            if (set.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if (set.size() == 1) {
                if (object.has("OR")) {
                    List list = Streams.stream((Iterable)JsonHelper.getArray(object, "OR")).map(jsonElement -> Deserializer.deserializeSelector(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new OrMultipartModelSelector(list);
                }
                if (object.has("AND")) {
                    List list2 = Streams.stream((Iterable)JsonHelper.getArray(object, "AND")).map(jsonElement -> Deserializer.deserializeSelector(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new AndMultipartModelSelector(list2);
                }
                return Deserializer.createStatePropertySelector((Map.Entry)set.iterator().next());
            }
            return new AndMultipartModelSelector(set.stream().map(Deserializer::createStatePropertySelector).collect(Collectors.toList()));
        }

        private static MultipartModelSelector createStatePropertySelector(Map.Entry<String, JsonElement> entry) {
            return new SimpleMultipartModelSelector(entry.getKey(), entry.getValue().getAsString());
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

