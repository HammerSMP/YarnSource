/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ParticleTextureData {
    @Nullable
    private final List<Identifier> textureList;

    private ParticleTextureData(@Nullable List<Identifier> list) {
        this.textureList = list;
    }

    @Nullable
    public List<Identifier> getTextureList() {
        return this.textureList;
    }

    public static ParticleTextureData load(JsonObject jsonObject) {
        List<Identifier> list2;
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "textures", null);
        if (jsonArray != null) {
            List list = (List)Streams.stream((Iterable)jsonArray).map(jsonElement -> JsonHelper.asString(jsonElement, "texture")).map(Identifier::new).collect(ImmutableList.toImmutableList());
        } else {
            list2 = null;
        }
        return new ParticleTextureData(list2);
    }
}

