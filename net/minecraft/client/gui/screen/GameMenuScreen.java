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
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class GameMenuScreen
extends Screen {
    private final boolean showMenu;

    public GameMenuScreen(boolean bl) {
        super(bl ? new TranslatableText("menu.game") : new TranslatableText("menu.paused"));
        this.showMenu = bl;
    }

    @Override
    protected void init() {
        if (this.showMenu) {
            this.initWidgets();
        }
    }

    private void initWidgets() {
        int i = -16;
        int j = 98;
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableText("menu.returnToGame"), arg -> {
            this.client.openScreen(null);
            this.client.mouse.lockCursor();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableText("gui.advancements"), arg -> this.client.openScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()))));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableText("gui.stats"), arg -> this.client.openScreen(new StatsScreen(this, this.client.player.getStatHandler()))));
        String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableText("menu.sendFeedback"), arg -> this.client.openScreen(new ConfirmChatLinkScreen(bl -> {
            if (bl) {
                Util.getOperatingSystem().open(string);
            }
            this.client.openScreen(this);
        }, string, true))));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableText("menu.reportBugs"), arg -> this.client.openScreen(new ConfirmChatLinkScreen(bl -> {
            if (bl) {
                Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
            }
            this.client.openScreen(this);
        }, "https://aka.ms/snapshotbugs?ref=game", true))));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableText("menu.options"), arg -> this.client.openScreen(new SettingsScreen(this, this.client.options))));
        ButtonWidget lv = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableText("menu.shareToLan"), arg -> this.client.openScreen(new OpenToLanScreen(this))));
        lv.active = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
        ButtonWidget lv2 = this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslatableText("menu.returnToMenu"), arg -> {
            boolean bl = this.client.isInSingleplayer();
            boolean bl2 = this.client.isConnectedToRealms();
            arg.active = false;
            this.client.world.disconnect();
            if (bl) {
                this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
            } else {
                this.client.disconnect();
            }
            if (bl) {
                this.client.openScreen(new TitleScreen());
            } else if (bl2) {
                RealmsBridge lv = new RealmsBridge();
                lv.switchToRealms(new TitleScreen());
            } else {
                this.client.openScreen(new MultiplayerScreen(new TitleScreen()));
            }
        }));
        if (!this.client.isInSingleplayer()) {
            lv2.setMessage(new TranslatableText("menu.disconnect"));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.showMenu) {
            this.renderBackground(arg);
            this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        } else {
            this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        }
        super.render(arg, i, j, f);
    }
}

