/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private final List<Entry> entries = Lists.newArrayList();

    public PlayerListS2CPacket() {
    }

    public PlayerListS2CPacket(Action arg, ServerPlayerEntity ... args) {
        this.action = arg;
        for (ServerPlayerEntity lv : args) {
            this.entries.add(new Entry(lv.getGameProfile(), lv.pingMilliseconds, lv.interactionManager.getGameMode(), lv.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(Action arg, Iterable<ServerPlayerEntity> iterable) {
        this.action = arg;
        for (ServerPlayerEntity lv : iterable) {
            this.entries.add(new Entry(lv.getGameProfile(), lv.pingMilliseconds, lv.interactionManager.getGameMode(), lv.getPlayerListName()));
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            GameProfile gameProfile = null;
            int k = 0;
            GameMode lv = null;
            Text lv2 = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameProfile = new GameProfile(arg.readUuid(), arg.readString(16));
                    int l = arg.readVarInt();
                    for (int m = 0; m < l; ++m) {
                        String string = arg.readString(32767);
                        String string2 = arg.readString(32767);
                        if (arg.readBoolean()) {
                            gameProfile.getProperties().put((Object)string, (Object)new Property(string, string2, arg.readString(32767)));
                            continue;
                        }
                        gameProfile.getProperties().put((Object)string, (Object)new Property(string, string2));
                    }
                    lv = GameMode.byId(arg.readVarInt());
                    k = arg.readVarInt();
                    if (!arg.readBoolean()) break;
                    lv2 = arg.readText();
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameProfile = new GameProfile(arg.readUuid(), null);
                    lv = GameMode.byId(arg.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile = new GameProfile(arg.readUuid(), null);
                    k = arg.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile = new GameProfile(arg.readUuid(), null);
                    if (!arg.readBoolean()) break;
                    lv2 = arg.readText();
                    break;
                }
                case REMOVE_PLAYER: {
                    gameProfile = new GameProfile(arg.readUuid(), null);
                }
            }
            this.entries.add(new Entry(gameProfile, k, lv, lv2));
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.action);
        arg.writeVarInt(this.entries.size());
        for (Entry lv : this.entries) {
            switch (this.action) {
                case ADD_PLAYER: {
                    arg.writeUuid(lv.getProfile().getId());
                    arg.writeString(lv.getProfile().getName());
                    arg.writeVarInt(lv.getProfile().getProperties().size());
                    for (Property property : lv.getProfile().getProperties().values()) {
                        arg.writeString(property.getName());
                        arg.writeString(property.getValue());
                        if (property.hasSignature()) {
                            arg.writeBoolean(true);
                            arg.writeString(property.getSignature());
                            continue;
                        }
                        arg.writeBoolean(false);
                    }
                    arg.writeVarInt(lv.getGameMode().getId());
                    arg.writeVarInt(lv.getLatency());
                    if (lv.getDisplayName() == null) {
                        arg.writeBoolean(false);
                        break;
                    }
                    arg.writeBoolean(true);
                    arg.writeText(lv.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    arg.writeUuid(lv.getProfile().getId());
                    arg.writeVarInt(lv.getGameMode().getId());
                    break;
                }
                case UPDATE_LATENCY: {
                    arg.writeUuid(lv.getProfile().getId());
                    arg.writeVarInt(lv.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    arg.writeUuid(lv.getProfile().getId());
                    if (lv.getDisplayName() == null) {
                        arg.writeBoolean(false);
                        break;
                    }
                    arg.writeBoolean(true);
                    arg.writeText(lv.getDisplayName());
                    break;
                }
                case REMOVE_PLAYER: {
                    arg.writeUuid(lv.getProfile().getId());
                }
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onPlayerList(this);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    public class Entry {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        private final Text displayName;

        public Entry(GameProfile gameProfile, int i, @Nullable GameMode arg2, @Nullable Text arg3) {
            this.profile = gameProfile;
            this.latency = i;
            this.gameMode = arg2;
            this.displayName = arg3;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getLatency() {
            return this.latency;
        }

        public GameMode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", (Object)this.profile).add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName)).toString();
        }
    }

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

    }
}

