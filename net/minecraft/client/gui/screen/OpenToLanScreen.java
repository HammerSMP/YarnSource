/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private final Screen parent;
    private ButtonWidget buttonAllowCommands;
    private ButtonWidget buttonGameMode;
    private String gameMode = "survival";
    private boolean allowCommands;

    public OpenToLanScreen(Screen arg) {
        super(new TranslatableText("lanServer.title"));
        this.parent = arg;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), arg -> {
            TranslatableText lv2;
            this.client.openScreen(null);
            int i = NetworkUtils.findLocalPort();
            if (this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i)) {
                TranslatableText lv = new TranslatableText("commands.publish.started", i);
            } else {
                lv2 = new TranslatableText("commands.publish.failed");
            }
            this.client.inGameHud.getChatHud().addMessage(lv2);
            this.client.updateWindowTitle();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.gameMode"), arg -> {
            this.gameMode = "spectator".equals(this.gameMode) ? "creative" : ("creative".equals(this.gameMode) ? "adventure" : ("adventure".equals(this.gameMode) ? "survival" : "spectator"));
            this.updateButtonText();
        }));
        this.buttonAllowCommands = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.allowCommands"), arg -> {
            this.allowCommands = !this.allowCommands;
            this.updateButtonText();
        }));
        this.updateButtonText();
    }

    private void updateButtonText() {
        this.buttonGameMode.setMessage(new TranslatableText("selectWorld.gameMode").append(": ").append(new TranslatableText("selectWorld.gameMode." + this.gameMode)));
        this.buttonAllowCommands.setMessage(new TranslatableText("selectWorld.allowCommands").append(" ").append(ScreenTexts.getToggleText(this.allowCommands)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 50, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("lanServer.otherPlayers", new Object[0]), this.width / 2, 82, 0xFFFFFF);
        super.render(arg, i, j, f);
    }
}

