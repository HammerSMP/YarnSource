/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class MultipartUnbakedModel
implements UnbakedModel {
    private final StateManager<Block, BlockState> stateFactory;
    private final List<MultipartModelComponent> components;

    public MultipartUnbakedModel(StateManager<Block, BlockState> arg, List<MultipartModelComponent> list) {
        this.stateFactory = arg;
        this.components = list;
    }

    public List<MultipartModelComponent> getComponents() {
        return this.components;
    }

    public Set<WeightedUnbakedModel> getModels() {
        HashSet set = Sets.newHashSet();
        for (MultipartModelComponent lv : this.components) {
            set.add(lv.getModel());
        }
        return set;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultipartUnbakedModel) {
            MultipartUnbakedModel lv = (MultipartUnbakedModel)object;
            return Objects.equals(this.stateFactory, lv.stateFactory) && Objects.equals(this.components, lv.components);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.stateFactory, this.components);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return this.getComponents().stream().flatMap(arg -> arg.getModel().getModelDependencies().stream()).collect(Collectors.toSet());
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
        return this.getComponents().stream().flatMap(arg -> arg.getModel().getTextureDependencies(function, set).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public BakedModel bake(ModelLoader arg, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings arg2, Identifier arg3) {
        MultipartBakedModel.Builder lv = new MultipartBakedModel.Builder();
        for (MultipartModelComponent lv2 : this.getComponents()) {
            BakedModel lv3 = lv2.getModel().bake(arg, function, arg2, arg3);
            if (lv3 == null) continue;
            lv.addComponent(lv2.getPredicate(this.stateFactory), lv3);
        }
        return lv.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<MultipartUnbakedModel> {
        private final ModelVariantMap.DeserializationContext context;

        public Deserializer(ModelVariantMap.DeserializationContext arg) {
            this.context = arg;
        }

        public MultipartUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
        }

        private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
            ArrayList list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                list.add(jsonDeserializationContext.deserialize(jsonElement, MultipartModelComponent.class));
            }
            return list;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}
