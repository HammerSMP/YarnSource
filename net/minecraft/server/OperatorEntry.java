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

public class OperatorEntry
extends ServerConfigEntry<GameProfile> {
    private final int permissionLevel;
    private final boolean bypassPlayerLimit;

    public OperatorEntry(GameProfile gameProfile, int i, boolean bl) {
        super(gameProfile);
        this.permissionLevel = i;
        this.bypassPlayerLimit = bl;
    }

    public OperatorEntry(JsonObject jsonObject) {
        super(OperatorEntry.getProfileFromJson(jsonObject));
        this.permissionLevel = jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0;
        this.bypassPlayerLimit = jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean canBypassPlayerLimit() {
        return this.bypassPlayerLimit;
    }

    @Override
    protected void fromJson(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
        jsonObject.addProperty("level", (Number)this.permissionLevel);
        jsonObject.addProperty("bypassesPlayerLimit", Boolean.valueOf(this.bypassPlayerLimit));
    }

    /*
     * WARNING - void declaration
     */
    private static GameProfile getProfileFromJson(JsonObject jsonObject) {
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

