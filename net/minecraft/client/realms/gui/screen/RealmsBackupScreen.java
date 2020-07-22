/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.DownloadTask;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.RestoreTask;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier field_22686 = new Identifier("realms", "textures/gui/realms/plus_icon.png");
    private static final Identifier field_22687 = new Identifier("realms", "textures/gui/realms/restore_icon.png");
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen parent;
    private List<Backup> backups = Collections.emptyList();
    private String toolTip;
    private BackupObjectSelectionList backupObjectSelectionList;
    private int selectedBackup = -1;
    private final int slotId;
    private ButtonWidget downloadButton;
    private ButtonWidget restoreButton;
    private ButtonWidget changesButton;
    private Boolean noBackups = false;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;

    public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
        this.parent = parent;
        this.serverData = serverData;
        this.slotId = slotId;
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.backupObjectSelectionList = new BackupObjectSelectionList();
        if (lastScrollPosition != -1) {
            this.backupObjectSelectionList.setScrollAmount(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient lv = RealmsClient.createRealmsClient();
                try {
                    List<Backup> list = lv.backupsFor((long)((RealmsBackupScreen)RealmsBackupScreen.this).serverData.id).backups;
                    RealmsBackupScreen.this.client.execute(() -> {
                        RealmsBackupScreen.this.backups = list;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        RealmsBackupScreen.this.backupObjectSelectionList.clear();
                        for (Backup lv : RealmsBackupScreen.this.backups) {
                            RealmsBackupScreen.this.backupObjectSelectionList.addEntry(lv);
                        }
                        RealmsBackupScreen.this.generateChangeList();
                    });
                }
                catch (RealmsServiceException lv2) {
                    LOGGER.error("Couldn't request backups", (Throwable)lv2);
                }
            }
        }.start();
        this.downloadButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(1), 120, 20, new TranslatableText("mco.backup.button.download"), arg -> this.downloadClicked()));
        this.restoreButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(3), 120, 20, new TranslatableText("mco.backup.button.restore"), arg -> this.restoreClicked(this.selectedBackup)));
        this.changesButton = this.addButton(new ButtonWidget(this.width - 135, RealmsBackupScreen.row(5), 120, 20, new TranslatableText("mco.backup.changes.tooltip"), arg -> {
            this.client.openScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
            this.selectedBackup = -1;
        }));
        this.addButton(new ButtonWidget(this.width - 100, this.height - 35, 85, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
        this.addChild(this.backupObjectSelectionList);
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.backup"), this.width / 2, 12, 0xFFFFFF));
        this.focusOn(this.backupObjectSelectionList);
        this.updateButtonStates();
        this.narrateLabels();
    }

    private void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int i = 0; i < this.backups.size() - 1; ++i) {
            Backup lv = this.backups.get(i);
            Backup lv2 = this.backups.get(i + 1);
            if (lv.metadata.isEmpty() || lv2.metadata.isEmpty()) continue;
            for (String string : lv.metadata.keySet()) {
                if (!string.contains("Uploaded") && lv2.metadata.containsKey(string)) {
                    if (lv.metadata.get(string).equals(lv2.metadata.get(string))) continue;
                    this.addToChangeList(lv, string);
                    continue;
                }
                this.addToChangeList(lv, string);
            }
        }
    }

    private void addToChangeList(Backup backup, String key) {
        if (key.contains("Uploaded")) {
            String string2 = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(key, string2);
            backup.setUploadedVersion(true);
        } else {
            backup.changeList.put(key, backup.metadata.get(key));
        }
    }

    private void updateButtonStates() {
        this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
        this.changesButton.visible = this.shouldChangesButtonBeVisible();
    }

    private boolean shouldChangesButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.backups.get((int)this.selectedBackup).changeList.isEmpty();
    }

    private boolean shouldRestoreButtonBeVisible() {
        if (this.selectedBackup == -1) {
            return false;
        }
        return !this.serverData.expired;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void restoreClicked(int selectedBackup) {
        if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = selectedBackup;
            Date date = this.backups.get((int)selectedBackup).lastModifiedDate;
            String string = DateFormat.getDateTimeInstance(3, 3).format(date);
            String string2 = RealmsUtil.method_25282(date);
            TranslatableText lv = new TranslatableText("mco.configure.world.restore.question.line1", string, string2);
            TranslatableText lv2 = new TranslatableText("mco.configure.world.restore.question.line2");
            this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                if (bl) {
                    this.restore();
                } else {
                    this.selectedBackup = -1;
                    this.client.openScreen(this);
                }
            }, RealmsLongConfirmationScreen.Type.Warning, lv, lv2, true));
        }
    }

    private void downloadClicked() {
        TranslatableText lv = new TranslatableText("mco.configure.world.restore.download.question.line1");
        TranslatableText lv2 = new TranslatableText("mco.configure.world.restore.download.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.downloadWorldData();
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
    }

    private void downloadWorldData() {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
    }

    private void restore() {
        Backup lv = this.backups.get(this.selectedBackup);
        this.selectedBackup = -1;
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new RestoreTask(lv, this.serverData.id, this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.renderBackground(matrices);
        this.backupObjectSelectionList.render(matrices, mouseX, mouseY, delta);
        this.titleLabel.render(this, matrices);
        this.textRenderer.draw(matrices, I18n.translate("mco.configure.world.backup", new Object[0]), (float)((this.width - 150) / 2 - 90), 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.textRenderer.draw(matrices, I18n.translate("mco.backup.nobackups", new Object[0]), 20.0f, (float)(this.height / 2 - 10), 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(matrices, mouseX, mouseY, delta);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
        }
    }

    protected void renderMousehoverTooltip(MatrixStack arg, String string, int i, int j) {
        if (string == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.textRenderer.getWidth(string);
        this.fillGradient(arg, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(arg, string, (float)k, (float)l, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<BackupObjectSelectionListEntry> {
        private final Backup mBackup;

        public BackupObjectSelectionListEntry(Backup backup) {
            this.mBackup = backup;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderBackupItem(matrices, this.mBackup, x - 40, y, mouseX, mouseY);
        }

        private void renderBackupItem(MatrixStack arg, Backup arg2, int i, int j, int k, int l) {
            int m = arg2.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.textRenderer.draw(arg, "Backup (" + RealmsUtil.method_25282(arg2.lastModifiedDate) + ")", (float)(i + 40), (float)(j + 1), m);
            RealmsBackupScreen.this.textRenderer.draw(arg, this.getMediumDatePresentation(arg2.lastModifiedDate), (float)(i + 40), (float)(j + 12), 0x4C4C4C);
            int n = RealmsBackupScreen.this.width - 175;
            int o = -3;
            int p = n - 10;
            boolean q = false;
            if (!((RealmsBackupScreen)RealmsBackupScreen.this).serverData.expired) {
                this.drawRestore(arg, n, j + -3, k, l);
            }
            if (!arg2.changeList.isEmpty()) {
                this.drawInfo(arg, p, j + 0, k, l);
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }

        private void drawRestore(MatrixStack arg, int i, int j, int k, int l) {
            boolean bl = k >= i && k <= i + 12 && l >= j && l <= j + 14 && l < RealmsBackupScreen.this.height - 15 && l > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22687);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 28.0f : 0.0f;
            DrawableHelper.drawTexture(arg, i * 2, j * 2, 0.0f, f, 23, 28, 23, 56);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.button.restore", new Object[0]);
            }
        }

        private void drawInfo(MatrixStack arg, int i, int j, int k, int l) {
            boolean bl = k >= i && k <= i + 8 && l >= j && l <= j + 8 && l < RealmsBackupScreen.this.height - 15 && l > 32;
            RealmsBackupScreen.this.client.getTextureManager().bindTexture(field_22686);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            float f = bl ? 15.0f : 0.0f;
            DrawableHelper.drawTexture(arg, i * 2, j * 2, 0.0f, f, 15, 15, 15, 30);
            RenderSystem.popMatrix();
            if (bl) {
                RealmsBackupScreen.this.toolTip = I18n.translate("mco.backup.changes.tooltip", new Object[0]);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionList
    extends RealmsObjectSelectionList<BackupObjectSelectionListEntry> {
        public BackupObjectSelectionList() {
            super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
        }

        public void addEntry(Backup backup) {
            this.addEntry(new BackupObjectSelectionListEntry(backup));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.93);
        }

        @Override
        public boolean isFocused() {
            return RealmsBackupScreen.this.getFocused() == this;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground(MatrixStack matrices) {
            RealmsBackupScreen.this.renderBackground(matrices);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            if (mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
                int j = this.width / 2 - 92;
                int k = this.width;
                int l = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount();
                int m = l / this.itemHeight;
                if (mouseX >= (double)j && mouseX <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                    this.setSelected(m);
                    this.itemClicked(l, m, mouseX, mouseY, this.width);
                }
                return true;
            }
            return false;
        }

        @Override
        public int getScrollbarPositionX() {
            return this.width - 5;
        }

        @Override
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
            int l = this.width - 35;
            int m = selectionIndex * this.itemHeight + 36 - (int)this.getScrollAmount();
            int n = l + 10;
            int o = m - 3;
            if (mouseX >= (double)l && mouseX <= (double)(l + 9) && mouseY >= (double)m && mouseY <= (double)(m + 9)) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)selectionIndex)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.client.openScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(selectionIndex)));
                }
            } else if (mouseX >= (double)n && mouseX < (double)(n + 13) && mouseY >= (double)o && mouseY < (double)(o + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(selectionIndex);
            }
        }

        @Override
        public void setSelected(int index) {
            this.setSelectedItem(index);
            if (index != -1) {
                Realms.narrateNow(I18n.translate("narrator.select", ((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)index)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(index);
        }

        public void selectInviteListItem(int item) {
            RealmsBackupScreen.this.selectedBackup = item;
            RealmsBackupScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable BackupObjectSelectionListEntry arg) {
            super.setSelected(arg);
            RealmsBackupScreen.this.selectedBackup = this.children().indexOf(arg);
            RealmsBackupScreen.this.updateButtonStates();
        }
    }
}

