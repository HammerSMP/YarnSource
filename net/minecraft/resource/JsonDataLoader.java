/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonDataLoader
extends SinglePreparationResourceReloadListener<Map<Identifier, JsonObject>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int FILE_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String dataType;

    public JsonDataLoader(Gson gson, String string) {
        this.gson = gson;
        this.dataType = string;
    }

    @Override
    protected Map<Identifier, JsonObject> prepare(ResourceManager arg, Profiler arg2) {
        HashMap map = Maps.newHashMap();
        int i = this.dataType.length() + 1;
        for (Identifier lv : arg.findResources(this.dataType, string -> string.endsWith(".json"))) {
            String string2 = lv.getPath();
            Identifier lv2 = new Identifier(lv.getNamespace(), string2.substring(i, string2.length() - FILE_SUFFIX_LENGTH));
            try {
                Resource lv3 = arg.getResource(lv);
                Throwable throwable = null;
                try {
                    InputStream inputStream = lv3.getInputStream();
                    Throwable throwable2 = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        Throwable throwable3 = null;
                        try {
                            JsonObject jsonObject = JsonHelper.deserialize(this.gson, (Reader)reader, JsonObject.class);
                            if (jsonObject != null) {
                                JsonObject jsonObject2 = map.put(lv2, jsonObject);
                                if (jsonObject2 == null) continue;
                                throw new IllegalStateException("Duplicate data file ignored with ID " + lv2);
                            }
                            LOGGER.error("Couldn't load data file {} from {} as it's null or empty", (Object)lv2, (Object)lv);
                        }
                        catch (Throwable throwable4) {
                            throwable3 = throwable4;
                            throw throwable4;
                        }
                        finally {
                            if (reader == null) continue;
                            if (throwable3 != null) {
                                try {
                                    ((Reader)reader).close();
                                }
                                catch (Throwable throwable5) {
                                    throwable3.addSuppressed(throwable5);
                                }
                                continue;
                            }
                            ((Reader)reader).close();
                        }
                    }
                    catch (Throwable throwable6) {
                        throwable2 = throwable6;
                        throw throwable6;
                    }
                    finally {
                        if (inputStream == null) continue;
                        if (throwable2 != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable7) {
                                throwable2.addSuppressed(throwable7);
                            }
                            continue;
                        }
                        inputStream.close();
                    }
                }
                catch (Throwable throwable8) {
                    throwable = throwable8;
                    throw throwable8;
                }
                finally {
                    if (lv3 == null) continue;
                    if (throwable != null) {
                        try {
                            lv3.close();
                        }
                        catch (Throwable throwable9) {
                            throwable.addSuppressed(throwable9);
                        }
                        continue;
                    }
                    lv3.close();
                }
            }
            catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", (Object)lv2, (Object)lv, (Object)exception);
            }
        }
        return map;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
        return this.prepare(arg, arg2);
    }
}

