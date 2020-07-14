/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5455;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.WorldSaveHandler;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    private CompoundTag userData;

    public IntegratedPlayerManager(IntegratedServer server, class_5455.class_5457 tracker, WorldSaveHandler saveHandler) {
        super(server, tracker, saveHandler, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity player) {
        if (player.getName().getString().equals(this.getServer().getUserName())) {
            this.userData = player.toTag(new CompoundTag());
        }
        super.savePlayerData(player);
    }

    @Override
    public Text checkCanJoin(SocketAddress address, GameProfile profile) {
        if (profile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(profile.getName()) != null) {
            return new TranslatableText("multiplayer.disconnect.name_taken");
        }
        return super.checkCanJoin(address, profile);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public CompoundTag getUserData() {
        return this.userData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

