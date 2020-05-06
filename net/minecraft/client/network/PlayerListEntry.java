/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class PlayerListEntry {
    private final GameProfile profile;
    private final Map<MinecraftProfileTexture.Type, Identifier> textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
    private GameMode gameMode;
    private int latency;
    private boolean texturesLoaded;
    @Nullable
    private String model;
    @Nullable
    private Text displayName;
    private int field_3738;
    private int field_3736;
    private long field_3737;
    private long field_3747;
    private long field_3746;

    public PlayerListEntry(PlayerListS2CPacket.Entry arg) {
        this.profile = arg.getProfile();
        this.gameMode = arg.getGameMode();
        this.latency = arg.getLatency();
        this.displayName = arg.getDisplayName();
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Nullable
    public GameMode getGameMode() {
        return this.gameMode;
    }

    protected void setGameMode(GameMode arg) {
        this.gameMode = arg;
    }

    public int getLatency() {
        return this.latency;
    }

    protected void setLatency(int i) {
        this.latency = i;
    }

    public boolean hasSkinTexture() {
        return this.getSkinTexture() != null;
    }

    public String getModel() {
        if (this.model == null) {
            return DefaultSkinHelper.getModel(this.profile.getId());
        }
        return this.model;
    }

    public Identifier getSkinTexture() {
        this.loadTextures();
        return (Identifier)MoreObjects.firstNonNull((Object)this.textures.get((Object)MinecraftProfileTexture.Type.SKIN), (Object)DefaultSkinHelper.getTexture(this.profile.getId()));
    }

    @Nullable
    public Identifier getCapeTexture() {
        this.loadTextures();
        return this.textures.get((Object)MinecraftProfileTexture.Type.CAPE);
    }

    @Nullable
    public Identifier getElytraTexture() {
        this.loadTextures();
        return this.textures.get((Object)MinecraftProfileTexture.Type.ELYTRA);
    }

    @Nullable
    public Team getScoreboardTeam() {
        return MinecraftClient.getInstance().world.getScoreboard().getPlayerTeam(this.getProfile().getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadTextures() {
        PlayerListEntry playerListEntry = this;
        synchronized (playerListEntry) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, arg, minecraftProfileTexture) -> {
                    this.textures.put(type, arg);
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.model = minecraftProfileTexture.getMetadata("model");
                        if (this.model == null) {
                            this.model = "default";
                        }
                    }
                }, true);
            }
        }
    }

    public void setDisplayName(@Nullable Text arg) {
        this.displayName = arg;
    }

    @Nullable
    public Text getDisplayName() {
        return this.displayName;
    }

    public int method_2973() {
        return this.field_3738;
    }

    public void method_2972(int i) {
        this.field_3738 = i;
    }

    public int method_2960() {
        return this.field_3736;
    }

    public void method_2965(int i) {
        this.field_3736 = i;
    }

    public long method_2974() {
        return this.field_3737;
    }

    public void method_2978(long l) {
        this.field_3737 = l;
    }

    public long method_2961() {
        return this.field_3747;
    }

    public void method_2975(long l) {
        this.field_3747 = l;
    }

    public long method_2976() {
        return this.field_3746;
    }

    public void method_2964(long l) {
        this.field_3746 = l;
    }
}

