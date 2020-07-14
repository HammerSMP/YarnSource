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

    public Model(Optional<Identifier> parent, Optional<String> variant, TextureKey ... requiredTextures) {
        this.parent = parent;
        this.variant = variant;
        this.requiredTextures = ImmutableSet.copyOf((Object[])requiredTextures);
    }

    public Identifier upload(Block block, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, this.variant.orElse("")), texture, modelCollector);
    }

    public Identifier upload(Block block, String suffix, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix + this.variant.orElse("")), texture, modelCollector);
    }

    public Identifier uploadWithoutVariant(Block block, String suffix, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix), texture, modelCollector);
    }

    public Identifier upload(Identifier id, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        Map<TextureKey, Identifier> map = this.createTextureMap(texture);
        modelCollector.accept(id, () -> {
            JsonObject jsonObject = new JsonObject();
            this.parent.ifPresent(arg -> jsonObject.addProperty("parent", arg.toString()));
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((arg, arg2) -> jsonObject2.addProperty(arg.getName(), arg2.toString()));
                jsonObject.add("textures", (JsonElement)jsonObject2);
            }
            return jsonObject;
        });
        return id;
    }

    private Map<TextureKey, Identifier> createTextureMap(Texture texture) {
        return (Map)Streams.concat((Stream[])new Stream[]{this.requiredTextures.stream(), texture.getInherited()}).collect(ImmutableMap.toImmutableMap(Function.identity(), texture::getTexture));
    }
}

