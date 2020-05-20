/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public class MinecartCommandBlockScreen
extends AbstractCommandBlockScreen {
    private final CommandBlockExecutor commandExecutor;

    public MinecartCommandBlockScreen(CommandBlockExecutor arg) {
        this.commandExecutor = arg;
    }

    @Override
    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    @Override
    int getTrackOutputButtonHeight() {
        return 150;
    }

    @Override
    protected void init() {
        super.init();
        this.trackingOutput = this.getCommandExecutor().isTrackingOutput();
        this.updateTrackedOutput();
        this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
    }

    @Override
    protected void syncSettingsToServer(CommandBlockExecutor arg) {
        if (arg instanceof CommandBlockMinecartEntity.CommandExecutor) {
            CommandBlockMinecartEntity.CommandExecutor lv = (CommandBlockMinecartEntity.CommandExecutor)arg;
            this.client.getNetworkHandler().sendPacket(new UpdateCommandBlockMinecartC2SPacket(lv.getMinecart().getEntityId(), this.consoleCommandTextField.getText(), arg.isTrackingOutput()));
        }
    }
}
