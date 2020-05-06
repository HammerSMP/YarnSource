/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.util.UUIDTypeAdapter
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Session {
    private final String username;
    private final String uuid;
    private final String accessToken;
    private final AccountType accountType;

    public Session(String string, String string2, String string3, String string4) {
        this.username = string;
        this.uuid = string2;
        this.accessToken = string3;
        this.accountType = AccountType.byName(string4);
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + this.uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public GameProfile getProfile() {
        try {
            UUID uUID = UUIDTypeAdapter.fromString((String)this.getUuid());
            return new GameProfile(uUID, this.getUsername());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return new GameProfile(null, this.getUsername());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, AccountType> BY_NAME;
        private final String name;

        private AccountType(String string2) {
            this.name = string2;
        }

        @Nullable
        public static AccountType byName(String string) {
            return BY_NAME.get(string.toLowerCase(Locale.ROOT));
        }

        static {
            BY_NAME = Arrays.stream(AccountType.values()).collect(Collectors.toMap(arg -> arg.name, Function.identity()));
        }
    }
}

