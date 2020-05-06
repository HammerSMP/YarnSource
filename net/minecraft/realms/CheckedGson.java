/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

@Environment(value=EnvType.CLIENT)
public class CheckedGson {
    private final Gson GSON = new Gson();

    public String toJson(RealmsSerializable arg) {
        return this.GSON.toJson((Object)arg);
    }

    public <T extends RealmsSerializable> T fromJson(String string, Class<T> arg) {
        return (T)((RealmsSerializable)this.GSON.fromJson(string, arg));
    }
}

