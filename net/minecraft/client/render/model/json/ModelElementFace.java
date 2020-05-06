/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ModelElementFace {
    public final Direction cullFace;
    public final int tintIndex;
    public final String textureId;
    public final ModelElementTexture textureData;

    public ModelElementFace(@Nullable Direction arg, int i, String string, ModelElementTexture arg2) {
        this.cullFace = arg;
        this.tintIndex = i;
        this.textureId = string;
        this.textureData = arg2;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelElementFace> {
        protected Deserializer() {
        }

        public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Direction lv = this.deserializeCullFace(jsonObject);
            int i = this.deserializeTintIndex(jsonObject);
            String string = this.deserializeTexture(jsonObject);
            ModelElementTexture lv2 = (ModelElementTexture)jsonDeserializationContext.deserialize((JsonElement)jsonObject, ModelElementTexture.class);
            return new ModelElementFace(lv, i, string, lv2);
        }

        protected int deserializeTintIndex(JsonObject jsonObject) {
            return JsonHelper.getInt(jsonObject, "tintindex", -1);
        }

        private String deserializeTexture(JsonObject jsonObject) {
            return JsonHelper.getString(jsonObject, "texture");
        }

        @Nullable
        private Direction deserializeCullFace(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "cullface", "");
            return Direction.byName(string);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

