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
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;
import net.minecraft.server.WhitelistEntry;

public class Whitelist
extends ServerConfigList<GameProfile, WhitelistEntry> {
    public Whitelist(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
        return new WhitelistEntry(jsonObject);
    }

    public boolean isAllowed(GameProfile gameProfile) {
        return this.contains(gameProfile);
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

    @Override
    protected String toString(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    @Override
    protected /* synthetic */ String toString(Object object) {
        return this.toString((GameProfile)object);
    }
}

