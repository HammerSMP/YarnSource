/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.network;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerItemCooldownManager
extends ItemCooldownManager {
    private final ServerPlayerEntity player;

    public ServerItemCooldownManager(ServerPlayerEntity arg) {
        this.player = arg;
    }

    @Override
    protected void onCooldownUpdate(Item arg, int i) {
        super.onCooldownUpdate(arg, i);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(arg, i));
    }

    @Override
    protected void onCooldownUpdate(Item arg) {
        super.onCooldownUpdate(arg);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(arg, 0));
    }
}

