/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.tag;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Function<Identifier, Optional<T>> registryGetter;
    private final String dataType;
    private final String entryType;

    public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType, String entryType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
        this.entryType = entryType;
    }

    public CompletableFuture<Map<Identifier, Tag.Builder>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap map = Maps.newHashMap();
            for (Identifier lv : manager.findResources(this.dataType, string -> string.endsWith(".json"))) {
                String string2 = lv.getPath();
                Identifier lv2 = new Identifier(lv.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
                try {
                    for (Resource lv3 : manager.getAllResources(lv)) {
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
        }, prepareExecutor);
    }

    public TagGroup<T> applyReload(Map<Identifier, Tag.Builder> tags) {
        HashMap map2 = Maps.newHashMap();
        Function function = map2::get;
        Function<Identifier, Object> function2 = arg -> this.registryGetter.apply((Identifier)arg).orElse(null);
        while (!tags.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Tag.Builder>> iterator = tags.entrySet().iterator();
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
        tags.forEach((arg, arg2) -> LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", (Object)this.entryType, arg, (Object)arg2.streamUnresolvedEntries(function, function2).map(Objects::toString).collect(Collectors.joining(","))));
        return TagGroup.create(map2);
    }
}

