/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenuState {
    private final SpectatorMenuCommandGroup group;
    private final List<SpectatorMenuCommand> commands;
    private final int selectedSlot;

    public SpectatorMenuState(SpectatorMenuCommandGroup group, List<SpectatorMenuCommand> commands, int selectedSlot) {
        this.group = group;
        this.commands = commands;
        this.selectedSlot = selectedSlot;
    }

    public SpectatorMenuCommand getCommand(int slot) {
        if (slot < 0 || slot >= this.commands.size()) {
            return SpectatorMenu.BLANK_COMMAND;
        }
        return (SpectatorMenuCommand)MoreObjects.firstNonNull((Object)this.commands.get(slot), (Object)SpectatorMenu.BLANK_COMMAND);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}

