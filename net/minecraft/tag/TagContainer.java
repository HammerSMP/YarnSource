/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContainer<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Tag<T> empty = Tag.of(ImmutableSet.of());
    private volatile BiMap<Identifier, Tag<T>> entries = HashBiMap.create();
    private final Function<Identifier, Optional<T>> getter;
    private final String dataType;
    private final String entryType;

    public TagContainer(Function<Identifier, Optional<T>> function, String string, String string2) {
        this.getter = function;
        this.dataType = string;
        this.entryType = string2;
    }

    @Nullable
    public Tag<T> get(Identifier arg) {
        return (Tag)this.entries.get((Object)arg);
    }

    public Tag<T> getOrCreate(Identifier arg) {
        return (Tag)this.entries.getOrDefault((Object)arg, this.empty);
    }

    @Environment(value=EnvType.CLIENT)
    public Tag<T> getEmpty() {
        return this.empty;
    }

    @Nullable
    public Identifier getId(Tag<T> arg) {
        if (arg instanceof Tag.Identified) {
            return ((Tag.Identified)arg).getId();
        }
        return (Identifier)this.entries.inverse().get(arg);
    }

    public Identifier checkId(Tag<T> arg) {
        Identifier lv = this.getId(arg);
        if (lv == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return lv;
    }

    public Collection<Identifier> getKeys() {
        return this.entries.keySet();
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<Identifier> getTagsFor(T object) {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry entry : this.entries.entrySet()) {
            if (!((Tag)entry.getValue()).contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    public CompletableFuture<Map<Identifier, Tag.Builder>> prepareReload(ResourceManager arg, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap map = Maps.newHashMap();
            for (Identifier lv : arg.findResources(this.dataType, string -> string.endsWith(".json"))) {
                String string2 = lv.getPath();
                Identifier lv2 = new Identifier(lv.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
                try {
                    for (Resource lv3 : arg.getAllResources(lv)) {
                        try {
                            InputStream inputStream = lv3.getInputStream();
                            Throwable throwable = null;
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                Throwable throwable2 = null;
                                try {
                                    JsonObject jsonObject = JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class);
                                    if (jsonObject == null) {
                                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", (Object)this.entryType, (Object)lv2, (Object)lv, (Object)lv3.getResourcePackName());
                                        continue;
                                    }
                                    map.computeIfAbsent(lv2, arg -> Tag.Builder.create()).read(jsonObject, lv3.getResourcePackName());
                                }
                                catch (Throwable throwable3) {
                                    throwable2 = throwable3;
                                    throw throwable3;
                                }
                                finally {
                                    if (reader == null) continue;
                                    if (throwable2 != null) {
                                        try {
                                            ((Reader)reader).close();
                                        }
                                        catch (Throwable throwable4) {
                                            throwable2.addSuppressed(throwable4);
                                        }
                                        continue;
                                    }
                                    ((Reader)reader).close();
                                }
                            }
                            catch (Throwable throwable5) {
                                throwable = throwable5;
                                throw throwable5;
                            }
                            finally {
                                if (inputStream == null) continue;
                                if (throwable != null) {
                                    try {
                                        inputStream.close();
                                    }
                                    catch (Throwable throwable6) {
                                        throwable.addSuppressed(throwable6);
                                    }
                                    continue;
                                }
                                inputStream.close();
                            }
                        }
                        catch (IOException | RuntimeException exception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", (Object)this.entryType, (Object)lv2, (Object)lv, (Object)lv3.getResourcePackName(), (Object)exception);
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)lv3);
                        }
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", (Object)this.entryType, (Object)lv2, (Object)lv, (Object)iOException);
                }
            }
            return map;
        }, executor);
    }

    public void applyReload(Map<Identifier, Tag.Builder> map) {
        HashMap map2 = Maps.newHashMap();
        Function function = map2::get;
        Function<Identifier, Object> function2 = arg -> this.getter.apply((Identifier)arg).orElse(null);
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Tag.Builder>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Tag.Builder> entry = iterator.next();
                Optional<Tag<Object>> optional = entry.getValue().build(function, function2);
                if (!optional.isPresent()) continue;
                map2.put(entry.getKey(), optional.get());
                iterator.remove();
                bl = true;
            }
            if (bl) continue;
            break;
        }
        map.forEach((arg, arg2) -> LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", (Object)this.entryType, arg, (Object)arg2.streamUnresolvedEntries(function, function2).map(Objects::toString).collect(Collectors.joining(","))));
        this.setEntries(map2);
    }

    protected void setEntries(Map<Identifier, Tag<T>> map) {
        this.entries = ImmutableBiMap.copyOf(map);
    }

    public Map<Identifier, Tag<T>> getEntries() {
        return this.entries;
    }
}

