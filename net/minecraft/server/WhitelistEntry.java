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
import java.util.UUID;
import net.minecraft.server.ServerConfigEntry;

public class WhitelistEntry
extends ServerConfigEntry<GameProfile> {
    public WhitelistEntry(GameProfile gameProfile) {
        super(gameProfile);
    }

    public WhitelistEntry(JsonObject jsonObject) {
        super(WhitelistEntry.profileFromJson(jsonObject));
    }

    @Override
    protected void fromJson(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
    }

    /*
     * WARNING - void declaration
     */
    private static GameProfile profileFromJson(JsonObject jsonObject) {
        void uUID2;
        if (!jsonObject.has("uuid") || !jsonObject.has("name")) {
            return null;
        }
        String string = jsonObject.get("uuid").getAsString();
        try {
            UUID uUID = UUID.fromString(string);
        }
        catch (Throwable throwable) {
            return null;
        }
        return new GameProfile((UUID)uUID2, jsonObject.get("name").getAsString());
    }
}

