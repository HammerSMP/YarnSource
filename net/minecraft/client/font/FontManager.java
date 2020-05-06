/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
    private final FontStorage missingStorage;
    private final Map<Identifier, FontStorage> fontStorages = Maps.newHashMap();
    private final TextureManager textureManager;
    private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();
    private final ResourceReloadListener resourceReloadListener = new SinglePreparationResourceReloadListener<Map<Identifier, List<Font>>>(){

        @Override
        protected Map<Identifier, List<Font>> prepare(ResourceManager arg2, Profiler arg22) {
            arg22.startTick();
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            HashMap map = Maps.newHashMap();
            for (Identifier lv : arg2.findResources("font", string -> string.endsWith(".json"))) {
                String string2 = lv.getPath();
                Identifier lv2 = new Identifier(lv.getNamespace(), string2.substring("font/".length(), string2.length() - ".json".length()));
                List list = map.computeIfAbsent(lv2, arg -> Lists.newArrayList((Object[])new Font[]{new BlankFont()}));
                arg22.push(lv2::toString);
                try {
                    for (Resource lv3 : arg2.getAllResources(lv)) {
                        arg22.push(lv3::getResourcePackName);
                        try (InputStream inputStream = lv3.getInputStream();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
                            arg22.push("reading");
                            JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(gson, (Reader)reader, JsonObject.class), "providers");
                            arg22.swap("parsing");
                            for (int i2 = jsonArray.size() - 1; i2 >= 0; --i2) {
                                JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i2), "providers[" + i2 + "]");
                                try {
                                    String string22 = JsonHelper.getString(jsonObject, "type");
                                    FontType lv4 = FontType.byId(string22);
                                    arg22.push(string22);
                                    Font lv5 = lv4.createLoader(jsonObject).load(arg2);
                                    if (lv5 != null) {
                                        list.add(lv5);
                                    }
                                    arg22.pop();
                                    continue;
                                }
                                catch (RuntimeException runtimeException) {
                                    LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", (Object)lv2, (Object)lv3.getResourcePackName(), (Object)runtimeException.getMessage());
                                }
                            }
                            arg22.pop();
                        }
                        catch (RuntimeException runtimeException2) {
                            LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", (Object)lv2, (Object)lv3.getResourcePackName(), (Object)runtimeException2.getMessage());
                        }
                        arg22.pop();
                    }
                }
                catch (IOException iOException) {
                    LOGGER.warn("Unable to load font '{}' in fonts.json: {}", (Object)lv2, (Object)iOException.getMessage());
                }
                arg22.push("caching");
                IntOpenHashSet intSet = new IntOpenHashSet();
                for (Font lv6 : list) {
                    intSet.addAll((IntCollection)lv6.method_27442());
                }
                intSet.forEach(i -> {
                    Font lv;
                    if (i == 32) {
                        return;
                    }
                    Iterator iterator = Lists.reverse((List)list).iterator();
                    while (iterator.hasNext() && (lv = (Font)iterator.next()).getGlyph(i) == null) {
                    }
                });
                arg22.pop();
                arg22.pop();
            }
            arg22.endTick();
            return map;
        }

        @Override
        protected void apply(Map<Identifier, List<Font>> map, ResourceManager arg2, Profiler arg22) {
            arg22.startTick();
            arg22.push("closing");
            FontManager.this.fontStorages.values().forEach(FontStorage::close);
            FontManager.this.fontStorages.clear();
            arg22.swap("reloading");
            map.forEach((arg, list) -> {
                FontStorage lv = new FontStorage(FontManager.this.textureManager, (Identifier)arg);
                lv.setFonts(Lists.reverse((List)list));
                FontManager.this.fontStorages.put(arg, lv);
            });
            arg22.pop();
            arg22.endTick();
        }

        @Override
        public String getName() {
            return "FontManager";
        }

        @Override
        protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
            return this.prepare(arg, arg2);
        }
    };

    public FontManager(TextureManager arg2) {
        this.textureManager = arg2;
        this.missingStorage = Util.make(new FontStorage(arg2, MISSING_STORAGE_ID), arg -> arg.setFonts(Lists.newArrayList((Object[])new Font[]{new BlankFont()})));
    }

    public void setIdOverrides(Map<Identifier, Identifier> map) {
        this.idOverrides = map;
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer(arg -> this.fontStorages.getOrDefault(this.idOverrides.getOrDefault(arg, (Identifier)arg), this.missingStorage));
    }

    public ResourceReloadListener getResourceReloadListener() {
        return this.resourceReloadListener;
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.missingStorage.close();
    }
}

