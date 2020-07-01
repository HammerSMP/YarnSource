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
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.DownloadTask;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RestoreTask;
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

    public RealmsBackupScreen(RealmsConfigureWorldScreen arg, RealmsServer arg2, int i) {
        this.parent = arg;
        this.serverData = arg2;
        this.slotId = i;
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

    private void addToChangeList(Backup arg, String string) {
        if (string.contains("Uploaded")) {
            String string2 = DateFormat.getDateTimeInstance(3, 3).format(arg.lastModifiedDate);
            arg.changeList.put(string, string2);
            arg.setUploadedVersion(true);
        } else {
            arg.changeList.put(string, arg.metadata.get(string));
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
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void restoreClicked(int i) {
        if (i >= 0 && i < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = i;
            Date date = this.backups.get((int)i).lastModifiedDate;
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
    public void render(MatrixStack arg, int i, int j, float f) {
        this.toolTip = null;
        this.renderBackground(arg);
        this.backupObjectSelectionList.render(arg, i, j, f);
        this.titleLabel.render(this, arg);
        this.textRenderer.draw(arg, I18n.translate("mco.configure.world.backup", new Object[0]), (float)((this.width - 150) / 2 - 90), 20.0f, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.textRenderer.draw(arg, I18n.translate("mco.backup.nobackups", new Object[0]), 20.0f, (float)(this.height / 2 - 10), 0xFFFFFF);
        }
        this.downloadButton.active = this.noBackups == false;
        super.render(arg, i, j, f);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(arg, this.toolTip, i, j);
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

        public BackupObjectSelectionListEntry(Backup arg2) {
            this.mBackup = arg2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.renderBackupItem(arg, this.mBackup, k - 40, j, n, o);
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

        private String getMediumDatePresentation(Date date) {
            return DateFormat.getDateTimeInstance(3, 3).format(date);
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

        public void addEntry(Backup arg) {
            this.addEntry(new BackupObjectSelectionListEntry(arg));
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
        public void renderBackground(MatrixStack arg) {
            RealmsBackupScreen.this.renderBackground(arg);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (i != 0) {
                return false;
            }
            if (d < (double)this.getScrollbarPositionX() && e >= (double)this.top && e <= (double)this.bottom) {
                int j = this.width / 2 - 92;
                int k = this.width;
                int l = (int)Math.floor(e - (double)this.top) - this.headerHeight + (int)this.getScrollAmount();
                int m = l / this.itemHeight;
                if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                    this.setSelected(m);
                    this.itemClicked(l, m, d, e, this.width);
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
        public void itemClicked(int i, int j, double d, double e, int k) {
            int l = this.width - 35;
            int m = j * this.itemHeight + 36 - (int)this.getScrollAmount();
            int n = l + 10;
            int o = m - 3;
            if (d >= (double)l && d <= (double)(l + 9) && e >= (double)m && e <= (double)(m + 9)) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)j)).changeList.isEmpty()) {
                    RealmsBackupScreen.this.selectedBackup = -1;
                    lastScrollPosition = (int)this.getScrollAmount();
                    this.client.openScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(j)));
                }
            } else if (d >= (double)n && d < (double)(n + 13) && e >= (double)o && e < (double)(o + 15)) {
                lastScrollPosition = (int)this.getScrollAmount();
                RealmsBackupScreen.this.restoreClicked(j);
            }
        }

        @Override
        public void setSelected(int i) {
            this.setSelectedItem(i);
            if (i != -1) {
                Realms.narrateNow(I18n.translate("narrator.select", ((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)i)).lastModifiedDate.toString()));
            }
            this.selectInviteListItem(i);
        }

        public void selectInviteListItem(int i) {
            RealmsBackupScreen.this.selectedBackup = i;
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

