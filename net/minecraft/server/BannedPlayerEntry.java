/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.BanEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BannedPlayerEntry
extends BanEntry<GameProfile> {
    public BannedPlayerEntry(GameProfile gameProfile) {
        this(gameProfile, null, null, null, null);
    }

    public BannedPlayerEntry(GameProfile gameProfile, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
        super(gameProfile, date, string, date2, string2);
    }

    public BannedPlayerEntry(JsonObject jsonObject) {
        super(BannedPlayerEntry.profileFromJson(jsonObject), jsonObject);
    }

    @Override
    protected void fromJson(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
        super.fromJson(jsonObject);
    }

    @Override
    public Text toText() {
        GameProfile gameProfile = (GameProfile)this.getKey();
        return new LiteralText(gameProfile.getName() != null ? gameProfile.getName() : Objects.toString(gameProfile.getId(), "(Unknown)"));
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

