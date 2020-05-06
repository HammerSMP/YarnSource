/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity
extends PlayerEntity {
    private PlayerListEntry cachedScoreboardEntry;
    public float elytraPitch;
    public float elytraYaw;
    public float elytraRoll;
    public final ClientWorld clientWorld;

    public AbstractClientPlayerEntity(ClientWorld arg, GameProfile gameProfile) {
        super(arg, arg.getSpawnPos(), gameProfile);
        this.clientWorld = arg;
    }

    @Override
    public boolean isSpectator() {
        PlayerListEntry lv = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
        return lv != null && lv.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        PlayerListEntry lv = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
        return lv != null && lv.getGameMode() == GameMode.CREATIVE;
    }

    public boolean canRenderCapeTexture() {
        return this.getPlayerListEntry() != null;
    }

    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        if (this.cachedScoreboardEntry == null) {
            this.cachedScoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
        }
        return this.cachedScoreboardEntry;
    }

    public boolean hasSkinTexture() {
        PlayerListEntry lv = this.getPlayerListEntry();
        return lv != null && lv.hasSkinTexture();
    }

    public Identifier getSkinTexture() {
        PlayerListEntry lv = this.getPlayerListEntry();
        return lv == null ? DefaultSkinHelper.getTexture(this.getUuid()) : lv.getSkinTexture();
    }

    @Nullable
    public Identifier getCapeTexture() {
        PlayerListEntry lv = this.getPlayerListEntry();
        return lv == null ? null : lv.getCapeTexture();
    }

    public boolean canRenderElytraTexture() {
        return this.getPlayerListEntry() != null;
    }

    @Nullable
    public Identifier getElytraTexture() {
        PlayerListEntry lv = this.getPlayerListEntry();
        return lv == null ? null : lv.getElytraTexture();
    }

    public static PlayerSkinTexture loadSkin(Identifier arg, String string) {
        TextureManager lv = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture lv2 = lv.getTexture(arg);
        if (lv2 == null) {
            lv2 = new PlayerSkinTexture(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", ChatUtil.stripTextFormat(string)), DefaultSkinHelper.getTexture(AbstractClientPlayerEntity.getOfflinePlayerUuid(string)), true, null);
            lv.registerTexture(arg, lv2);
        }
        return (PlayerSkinTexture)lv2;
    }

    public static Identifier getSkinId(String string) {
        return new Identifier("skins/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)ChatUtil.stripTextFormat(string)));
    }

    public String getModel() {
        PlayerListEntry lv = this.getPlayerListEntry();
        return lv == null ? DefaultSkinHelper.getModel(this.getUuid()) : lv.getModel();
    }

    public float getSpeed() {
        float f = 1.0f;
        if (this.abilities.flying) {
            f *= 1.1f;
        }
        f = (float)((double)f * ((this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
        if (this.abilities.getWalkSpeed() == 0.0f || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0f;
        }
        if (this.isUsingItem() && this.getActiveItem().getItem() == Items.BOW) {
            int i = this.getItemUseTime();
            float g = (float)i / 20.0f;
            g = g > 1.0f ? 1.0f : (g *= g);
            f *= 1.0f - g * 0.15f;
        }
        return f;
    }
}

