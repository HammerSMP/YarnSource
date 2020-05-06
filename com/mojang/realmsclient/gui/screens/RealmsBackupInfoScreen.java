/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupInfoScreen
extends RealmsScreen {
    private final Screen lastScreen;
    private final Backup backup;
    private final List<String> keys = Lists.newArrayList();
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen arg, Backup arg2) {
        this.lastScreen = arg;
        this.backup = arg2;
        if (arg2.changeList != null) {
            for (Map.Entry<String, String> entry : arg2.changeList.entrySet()) {
                this.keys.add(entry.getKey());
            }
        }
    }

    @Override
    public void tick() {
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.lastScreen)));
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
            this.client.openScreen(this.lastScreen);
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
    extends ListWidget {
        public BackupInfoList(MinecraftClient arg2) {
            super(arg2, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
        }

        @Override
        public int getItemCount() {
            return ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList.size();
        }

        @Override
        protected void renderItem(MatrixStack arg, int i, int j, int k, int l, int m, int n, float f) {
            String string = (String)RealmsBackupInfoScreen.this.keys.get(i);
            TextRenderer lv = this.client.textRenderer;
            this.drawString(arg, lv, string, this.width / 2 - 40, k, 0xA0A0A0);
            String string2 = ((RealmsBackupInfoScreen)RealmsBackupInfoScreen.this).backup.changeList.get(string);
            this.method_27535(arg, lv, RealmsBackupInfoScreen.this.checkForSpecificMetadata(string, string2), this.width / 2 - 40, k + 12, 0xFFFFFF);
        }

        @Override
        public boolean isSelectedItem(int i) {
            return false;
        }

        @Override
        public void renderBackground() {
        }

        @Override
        public void render(MatrixStack arg, int i, int j, float f) {
            if (!this.visible) {
                return;
            }
            this.renderBackground();
            int k = this.getScrollbarPosition();
            int l = k + 6;
            this.capYPosition();
            RenderSystem.disableFog();
            Tessellator lv = Tessellator.getInstance();
            BufferBuilder lv2 = lv.getBuffer();
            int m = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
            int n = this.top + 4 - (int)this.scrollAmount;
            if (this.renderHeader) {
                this.renderHeader(m, n, lv);
            }
            this.renderList(arg, m, n, i, j, f);
            RenderSystem.disableDepthTest();
            this.renderHoleBackground(0, this.top, 255, 255);
            this.renderHoleBackground(this.bottom, this.height, 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableTexture();
            int o = this.getMaxScroll();
            if (o > 0) {
                int p = (this.bottom - this.top) * (this.bottom - this.top) / this.getMaxPosition();
                int q = (int)this.scrollAmount * (this.bottom - this.top - (p = MathHelper.clamp(p, 32, this.bottom - this.top - 8))) / o + this.top;
                if (q < this.top) {
                    q = this.top;
                }
                lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                lv2.vertex(k, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
                lv2.vertex(l, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
                lv2.vertex(l, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
                lv2.vertex(k, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
                lv.draw();
                lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                lv2.vertex(k, q + p, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
                lv2.vertex(l, q + p, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
                lv2.vertex(l, q, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
                lv2.vertex(k, q, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
                lv.draw();
                lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                lv2.vertex(k, q + p - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
                lv2.vertex(l - 1, q + p - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
                lv2.vertex(l - 1, q, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
                lv2.vertex(k, q, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
                lv.draw();
            }
            this.renderDecorations(i, j);
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
        }
    }
}

