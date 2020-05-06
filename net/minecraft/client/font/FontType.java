/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TextureFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public enum FontType {
    BITMAP("bitmap", TextureFont.Loader::fromJson),
    TTF("ttf", TrueTypeFontLoader::fromJson),
    LEGACY_UNICODE("legacy_unicode", UnicodeTextureFont.Loader::fromJson);

    private static final Map<String, FontType> REGISTRY;
    private final String id;
    private final Function<JsonObject, FontLoader> loaderFactory;

    private FontType(String string2, Function<JsonObject, FontLoader> function) {
        this.id = string2;
        this.loaderFactory = function;
    }

    public static FontType byId(String string) {
        FontType lv = REGISTRY.get(string);
        if (lv == null) {
            throw new IllegalArgumentException("Invalid type: " + string);
        }
        return lv;
    }

    public FontLoader createLoader(JsonObject jsonObject) {
        return this.loaderFactory.apply(jsonObject);
    }

    static {
        REGISTRY = Util.make(Maps.newHashMap(), hashMap -> {
            for (FontType lv : FontType.values()) {
                hashMap.put(lv.id, lv);
            }
        });
    }
}

