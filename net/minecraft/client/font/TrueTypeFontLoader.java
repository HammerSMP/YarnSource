/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFontLoader
implements FontLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier filename;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String excludedCharacters;

    public TrueTypeFontLoader(Identifier arg, float f, float g, float h, float i, String string) {
        this.filename = arg;
        this.size = f;
        this.oversample = g;
        this.shiftX = h;
        this.shiftY = i;
        this.excludedCharacters = string;
    }

    public static FontLoader fromJson(JsonObject jsonObject) {
        float f = 0.0f;
        float g = 0.0f;
        if (jsonObject.has("shift")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("shift");
            if (jsonArray.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
            }
            f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
            g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (jsonObject.has("skip")) {
            JsonElement jsonElement = jsonObject.get("skip");
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");
                for (int i = 0; i < jsonArray2.size(); ++i) {
                    stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
                }
            } else {
                stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
            }
        }
        return new TrueTypeFontLoader(new Identifier(JsonHelper.getString(jsonObject, "file")), JsonHelper.getFloat(jsonObject, "size", 11.0f), JsonHelper.getFloat(jsonObject, "oversample", 1.0f), f, g, stringBuilder.toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public Font load(ResourceManager arg) {
        STBTTFontinfo sTBTTFontinfo = null;
        ByteBuffer byteBuffer = null;
        try (Resource lv = arg.getResource(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()));){
            LOGGER.debug("Loading font {}", (Object)this.filename);
            sTBTTFontinfo = STBTTFontinfo.malloc();
            byteBuffer = TextureUtil.readAllToByteBuffer(lv.getInputStream());
            byteBuffer.flip();
            LOGGER.debug("Reading font {}", (Object)this.filename);
            if (!STBTruetype.stbtt_InitFont((STBTTFontinfo)sTBTTFontinfo, (ByteBuffer)byteBuffer)) {
                throw new IOException("Invalid ttf");
            }
            TrueTypeFont trueTypeFont = new TrueTypeFont(byteBuffer, sTBTTFontinfo, this.size, this.oversample, this.shiftX, this.shiftY, this.excludedCharacters);
            return trueTypeFont;
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load truetype font {}", (Object)this.filename, (Object)exception);
            if (sTBTTFontinfo != null) {
                sTBTTFontinfo.free();
            }
            MemoryUtil.memFree(byteBuffer);
            return null;
        }
    }
}

