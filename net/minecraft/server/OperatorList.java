/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;

public class OperatorList
extends ServerConfigList<GameProfile, OperatorEntry> {
    public OperatorList(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
        return new OperatorEntry(jsonObject);
    }

    @Override
    public String[] getNames() {
        String[] strings = new String[this.values().size()];
        int i = 0;
        for (ServerConfigEntry lv : this.values()) {
            strings[i++] = ((GameProfile)lv.getKey()).getName();
        }
        return strings;
    }

    public boolean isOp(GameProfile gameProfile) {
        OperatorEntry lv = (OperatorEntry)this.get(gameProfile);
        if (lv != null) {
            return lv.canBypassPlayerLimit();
        }
        return false;
    }

    @Override
    protected String toString(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    @Override
    protected /* synthetic */ String toString(Object object) {
        return this.toString((GameProfile)object);
    }
}

