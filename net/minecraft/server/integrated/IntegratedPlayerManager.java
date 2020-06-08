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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.WorldSaveHandler;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    private CompoundTag userData;

    public IntegratedPlayerManager(IntegratedServer arg, RegistryTracker.Modifiable arg2, WorldSaveHandler arg3) {
        super(arg, arg2, arg3, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity arg) {
        if (arg.getName().getString().equals(this.getServer().getUserName())) {
            this.userData = arg.toTag(new CompoundTag());
        }
        super.savePlayerData(arg);
    }

    @Override
    public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(gameProfile.getName()) != null) {
            return new TranslatableText("multiplayer.disconnect.name_taken");
        }
        return super.checkCanJoin(socketAddress, gameProfile);
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

