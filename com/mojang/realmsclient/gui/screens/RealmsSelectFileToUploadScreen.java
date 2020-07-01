/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsUploadScreen;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private final RealmsResetWorldScreen parent;
    private final long worldId;
    private final int slotId;
    private ButtonWidget uploadButton;
    private List<LevelSummary> levelList = Lists.newArrayList();
    private int selectedWorld = -1;
    private WorldSelectionList worldSelectionList;
    private String worldLang;
    private String conversionLang;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private RealmsLabel field_20063;
    private final Runnable field_22717;

    public RealmsSelectFileToUploadScreen(long l, int i, RealmsResetWorldScreen arg, Runnable runnable) {
        this.parent = arg;
        this.worldId = l;
        this.slotId = i;
        this.field_22717 = runnable;
    }

    private void loadLevelList() throws Exception {
        this.levelList = this.client.getLevelStorage().getLevelList().stream().sorted((arg, arg2) -> {
            if (arg.getLastPlayed() < arg2.getLastPlayed()) {
                return 1;
            }
            if (arg.getLastPlayed() > arg2.getLastPlayed()) {
                return -1;
            }
            return arg.getName().compareTo(arg2.getName());
        }).collect(Collectors.toList());
        for (LevelSummary lv : this.levelList) {
            this.worldSelectionList.addEntry(lv);
        }
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.worldSelectionList = new WorldSelectionList();
        try {
            this.loadLevelList();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load level list", (Throwable)exception);
            this.client.openScreen(new RealmsGenericErrorScreen(new LiteralText("Unable to load worlds"), Text.method_30163(exception.getMessage()), this.parent));
            return;
        }
        this.worldLang = I18n.translate("selectWorld.world", new Object[0]);
        this.conversionLang = I18n.translate("selectWorld.conversion", new Object[0]);
        this.addChild(this.worldSelectionList);
        this.uploadButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 32, 153, 20, new TranslatableText("mco.upload.button.name"), arg -> this.upload()));
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addButton(new ButtonWidget(this.width / 2 + 6, this.height - 32, 153, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.title"), this.width / 2, 13, 0xFFFFFF));
        this.subtitleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), 0xA0A0A0));
        this.field_20063 = this.levelList.isEmpty() ? this.addChild(new RealmsLabel(new TranslatableText("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 0xFFFFFF)) : null;
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void upload() {
        if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
            LevelSummary lv = this.levelList.get(this.selectedWorld);
            this.client.openScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.parent, lv, this.field_22717));
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.worldSelectionList.render(arg, i, j, f);
        this.titleLabel.render(this, arg);
        this.subtitleLabel.render(this, arg);
        if (this.field_20063 != null) {
            this.field_20063.render(this, arg);
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private static String method_21400(LevelSummary arg) {
        return arg.getGameMode().getTranslatableName().getString();
    }

    private static String method_21404(LevelSummary arg) {
        return DATE_FORMAT.format(new Date(arg.getLastPlayed()));
    }

    @Environment(value=EnvType.CLIENT)
    class WorldListEntry
    extends AlwaysSelectedEntryListWidget.Entry<WorldListEntry> {
        private final LevelSummary field_22718;

        public WorldListEntry(LevelSummary arg2) {
            this.field_22718 = arg2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.renderItem(arg, this.field_22718, i, k, j);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.field_22718));
            return true;
        }

        protected void renderItem(MatrixStack arg, LevelSummary arg2, int i, int j, int k) {
            String string = arg2.getDisplayName();
            if (string == null || string.isEmpty()) {
                string = RealmsSelectFileToUploadScreen.this.worldLang + " " + (i + 1);
            }
            String string2 = arg2.getName();
            string2 = string2 + " (" + RealmsSelectFileToUploadScreen.method_21404(arg2);
            string2 = string2 + ")";
            String string3 = "";
            if (arg2.requiresConversion()) {
                string3 = RealmsSelectFileToUploadScreen.this.conversionLang + " " + string3;
            } else {
                string3 = RealmsSelectFileToUploadScreen.method_21400(arg2);
                if (arg2.isHardcore()) {
                    string3 = (Object)((Object)Formatting.DARK_RED) + I18n.translate("mco.upload.hardcore", new Object[0]) + (Object)((Object)Formatting.RESET);
                }
                if (arg2.hasCheats()) {
                    string3 = string3 + ", " + I18n.translate("selectWorld.cheats", new Object[0]);
                }
            }
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(arg, string, (float)(j + 2), (float)(k + 1), 0xFFFFFF);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(arg, string2, (float)(j + 2), (float)(k + 12), 0x808080);
            RealmsSelectFileToUploadScreen.this.textRenderer.draw(arg, string3, (float)(j + 2), (float)(k + 12 + 10), 0x808080);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldSelectionList
    extends RealmsObjectSelectionList<WorldListEntry> {
        public WorldSelectionList() {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void addEntry(LevelSummary arg) {
            this.addEntry(new WorldListEntry(arg));
        }

        @Override
        public int getMaxPosition() {
            return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
        }

        @Override
        public boolean isFocused() {
            return RealmsSelectFileToUploadScreen.this.getFocused() == this;
        }

        @Override
        public void renderBackground(MatrixStack arg) {
            RealmsSelectFileToUploadScreen.this.renderBackground(arg);
        }

        @Override
        public void setSelected(int i) {
            this.setSelectedItem(i);
            if (i != -1) {
                LevelSummary lv = (LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(i);
                String string = I18n.translate("narrator.select.list.position", i + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
                String string2 = Realms.joinNarrations(Arrays.asList(lv.getDisplayName(), RealmsSelectFileToUploadScreen.method_21404(lv), RealmsSelectFileToUploadScreen.method_21400(lv), string));
                Realms.narrateNow(I18n.translate("narrator.select", string2));
            }
        }

        @Override
        public void setSelected(@Nullable WorldListEntry arg) {
            super.setSelected(arg);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(arg);
            ((RealmsSelectFileToUploadScreen)RealmsSelectFileToUploadScreen.this).uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((LevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore();
        }
    }
}

