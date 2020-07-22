/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TeamTeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private final List<SpectatorMenuCommand> commands = Lists.newArrayList();

    public TeamTeleportSpectatorMenu() {
        MinecraftClient lv = MinecraftClient.getInstance();
        for (Team lv2 : lv.world.getScoreboard().getTeams()) {
            this.commands.add(new TeleportToSpecificTeamCommand(lv2));
        }
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.commands;
    }

    @Override
    public Text getPrompt() {
        return new TranslatableText("spectatorMenu.team_teleport.prompt");
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.selectElement(this);
    }

    @Override
    public Text getName() {
        return new TranslatableText("spectatorMenu.team_teleport");
    }

    @Override
    public void renderIcon(MatrixStack arg, float f, int i) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
        DrawableHelper.drawTexture(arg, 0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        for (SpectatorMenuCommand lv : this.commands) {
            if (!lv.isEnabled()) continue;
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    class TeleportToSpecificTeamCommand
    implements SpectatorMenuCommand {
        private final Team team;
        private final Identifier skinId;
        private final List<PlayerListEntry> scoreboardEntries;

        public TeleportToSpecificTeamCommand(Team arg2) {
            this.team = arg2;
            this.scoreboardEntries = Lists.newArrayList();
            for (String string : arg2.getPlayerList()) {
                PlayerListEntry lv = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(string);
                if (lv == null) continue;
                this.scoreboardEntries.add(lv);
            }
            if (this.scoreboardEntries.isEmpty()) {
                this.skinId = DefaultSkinHelper.getTexture();
            } else {
                String string2 = this.scoreboardEntries.get(new Random().nextInt(this.scoreboardEntries.size())).getProfile().getName();
                this.skinId = AbstractClientPlayerEntity.getSkinId(string2);
                AbstractClientPlayerEntity.loadSkin(this.skinId, string2);
            }
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
        }

        @Override
        public Text getName() {
            return this.team.getDisplayName();
        }

        @Override
        public void renderIcon(MatrixStack arg, float f, int i) {
            Integer integer = this.team.getColor().getColorValue();
            if (integer != null) {
                float g = (float)(integer >> 16 & 0xFF) / 255.0f;
                float h = (float)(integer >> 8 & 0xFF) / 255.0f;
                float j = (float)(integer & 0xFF) / 255.0f;
                DrawableHelper.fill(arg, 1, 1, 15, 15, MathHelper.packRgb(g * f, h * f, j * f) | i << 24);
            }
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
            RenderSystem.color4f(f, f, f, (float)i / 255.0f);
            DrawableHelper.drawTexture(arg, 2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
            DrawableHelper.drawTexture(arg, 2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
        }

        @Override
        public boolean isEnabled() {
            return !this.scoreboardEntries.isEmpty();
        }
    }
}

