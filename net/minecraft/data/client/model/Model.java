/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.util.Identifier;

public class Model {
    private final Optional<Identifier> parent;
    private final Set<TextureKey> requiredTextures;
    private Optional<String> variant;

    public Model(Optional<Identifier> optional, Optional<String> optional2, TextureKey ... args) {
        this.parent = optional;
        this.variant = optional2;
        this.requiredTextures = ImmutableSet.copyOf((Object[])args);
    }

    public Identifier upload(Block arg, Texture arg2, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer) {
        return this.upload(ModelIds.getBlockSubModelId(arg, this.variant.orElse("")), arg2, biConsumer);
    }

    public Identifier upload(Block arg, String string, Texture arg2, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer) {
        return this.upload(ModelIds.getBlockSubModelId(arg, string + this.variant.orElse("")), arg2, biConsumer);
    }

    public Identifier uploadWithoutVariant(Block arg, String string, Texture arg2, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer) {
        return this.upload(ModelIds.getBlockSubModelId(arg, string), arg2, biConsumer);
    }

    public Identifier upload(Identifier arg, Texture arg2, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer) {
        Map<TextureKey, Identifier> map = this.createTextureMap(arg2);
        biConsumer.accept(arg, () -> {
            JsonObject jsonObject = new JsonObject();
            this.parent.ifPresent(arg -> jsonObject.addProperty("parent", arg.toString()));
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((arg, arg2) -> jsonObject2.addProperty(arg.getName(), arg2.toString()));
                jsonObject.add("textures", (JsonElement)jsonObject2);
            }
            return jsonObject;
        });
        return arg;
    }

    private Map<TextureKey, Identifier> createTextureMap(Texture arg) {
        return (Map)Streams.concat((Stream[])new Stream[]{this.requiredTextures.stream(), arg.getInherited()}).collect(ImmutableMap.toImmutableMap(Function.identity(), arg::getTexture));
    }
}

