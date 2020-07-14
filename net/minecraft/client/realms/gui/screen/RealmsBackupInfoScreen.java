/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupInfoScreen
extends RealmsScreen {
    private final Screen parent;
    private final Backup backup;
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen parent, Backup backup) {
        this.parent = parent;
        this.backup = backup;
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, "Changes from last backup", this.width / 2, 10, 0xFFFFFF);
        this.backupInfoList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private Text checkForSpecificMetadata(String key, String value) {
        String string3 = key.toLowerCase(Locale.ROOT);
        if (string3.contains("game") && string3.contains("mode")) {
            return this.gameModeMetadata(value);
        }
        if (string3.contains("game") && string3.contains("difficulty")) {
            return this.gameDifficultyMetadata(value);
        }
        return new LiteralText(value);
    }

    private Text gameDifficultyMetadata(String value) {
        try {
            return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(value)];
        }
        catch (Exception exception) {
            return new LiteralText("UNKNOWN");
        }
    }

    private Text gameModeMetadata(String value) {
        try {
            return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(value)];
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
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer lv = ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).client.textRenderer;
            RealmsBackupInfoScreen.this.drawStringWithShadow(matrices, lv, this.field_25258, x, y, 0xA0A0A0);
            RealmsBackupInfoScreen.this.drawTextWithShadow(matrices, lv, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.field_25258, this.field_25259), x, y + 12, 0xFFFFFF);
        }
    }
}

