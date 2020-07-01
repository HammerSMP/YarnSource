/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupInfoScreen
extends RealmsScreen {
    private final Screen parent;
    private final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen arg, Backup arg2) {
        this.parent = arg;
        this.backup = arg2;
    }

    @Override
    public void tick() {
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
        this.backupInfoList = new BackupInfoList(this.client);
        this.addChild(this.backupInfoList);
        this.focusOn(this.backupInfoList);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredString(arg, this.textRenderer, "Changes from last backup", this.width / 2, 10, 0xFFFFFF);
        this.backupInfoList.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }

    private Text checkForSpecificMetadata(String string, String string2) {
        String string3 = string.toLowerCase(Locale.ROOT);
        if (string3.contains("game") && string3.contains("mode")) {
            return this.gameModeMetadata(string2);
        }
        if (string3.contains("game") && string3.contains("difficulty")) {
            return this.gameDifficultyMetadata(string2);
        }
        return new LiteralText(string2);
    }

    private Text gameDifficultyMetadata(String string) {
        try {
            return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(string)];
        }
        catch (Exception exception) {
            return new LiteralText("UNKNOWN");
        }
    }

    private Text gameModeMetadata(String string) {
        try {
            return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(string)];
        }
        catch (Exception exception) {
            return new LiteralText("UNKNOWN");
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupInfoList
    extends AlwaysSelectedEntryListWidget<class_5344> {
        public BackupInfoList(MinecraftClient arg2) {
            super(arg2, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);
            if (((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList != null) {
                ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList.forEach((string, string2) -> this.addEntry(new class_5344((String)string, (String)string2)));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class class_5344
    extends AlwaysSelectedEntryListWidget.Entry<class_5344> {
        private final String field_25258;
        private final String field_25259;

        public class_5344(String string, String string2) {
            this.field_25258 = string;
            this.field_25259 = string2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            TextRenderer lv = ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).client.textRenderer;
            RealmsBackupInfoScreen.this.drawStringWithShadow(arg, lv, this.field_25258, k, j, 0xA0A0A0);
            RealmsBackupInfoScreen.this.drawTextWithShadow(arg, lv, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.field_25258, this.field_25259), k, j + 12, 0xFFFFFF);
        }
    }
}

