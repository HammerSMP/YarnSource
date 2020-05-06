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
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetNormalWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.gui.screens.RealmsSelectFileToUploadScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.ResettingWorldTask;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsResetWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private Text title = new TranslatableText("mco.reset.world.title");
    private Text subtitle = new TranslatableText("mco.reset.world.warning");
    private Text buttonTitle = ScreenTexts.CANCEL;
    private int subtitleColor = 0xFF0000;
    private static final Identifier field_22713 = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    private static final Identifier field_22714 = new Identifier("realms", "textures/gui/realms/upload.png");
    private static final Identifier field_22715 = new Identifier("realms", "textures/gui/realms/adventure.png");
    private static final Identifier field_22716 = new Identifier("realms", "textures/gui/realms/survival_spawn.png");
    private static final Identifier field_22708 = new Identifier("realms", "textures/gui/realms/new_world.png");
    private static final Identifier field_22709 = new Identifier("realms", "textures/gui/realms/experience.png");
    private static final Identifier field_22710 = new Identifier("realms", "textures/gui/realms/inspiration.png");
    private WorldTemplatePaginatedList field_20495;
    private WorldTemplatePaginatedList field_20496;
    private WorldTemplatePaginatedList field_20497;
    private WorldTemplatePaginatedList field_20498;
    public int slot = -1;
    private ResetType typeToReset = ResetType.NONE;
    private ResetWorldInfo field_20499;
    private WorldTemplate field_20500;
    private String field_20501;
    private final Runnable field_22711;
    private final Runnable field_22712;

    public RealmsResetWorldScreen(Screen arg, RealmsServer arg2, Runnable runnable, Runnable runnable2) {
        this.lastScreen = arg;
        this.serverData = arg2;
        this.field_22711 = runnable;
        this.field_22712 = runnable2;
    }

    public RealmsResetWorldScreen(Screen arg, RealmsServer arg2, Text arg3, Text arg4, int i, Text arg5, Runnable runnable, Runnable runnable2) {
        this(arg, arg2, runnable, runnable2);
        this.title = arg3;
        this.subtitle = arg4;
        this.subtitleColor = i;
        this.buttonTitle = arg5;
    }

    public void setSlot(int i) {
        this.slot = i;
    }

    public void setResetTitle(String string) {
        this.field_20501 = string;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 40, RealmsResetWorldScreen.row(14) - 10, 80, 20, this.buttonTitle, arg -> this.client.openScreen(this.lastScreen)));
        new Thread("Realms-reset-world-fetcher"){

            @Override
            public void run() {
                RealmsClient lv = RealmsClient.createRealmsClient();
                try {
                    WorldTemplatePaginatedList lv2 = lv.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList lv3 = lv.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList lv4 = lv.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList lv5 = lv.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsResetWorldScreen.this.client.execute(() -> {
                        RealmsResetWorldScreen.this.field_20495 = lv2;
                        RealmsResetWorldScreen.this.field_20496 = lv3;
                        RealmsResetWorldScreen.this.field_20497 = lv4;
                        RealmsResetWorldScreen.this.field_20498 = lv5;
                    });
                }
                catch (RealmsServiceException lv6) {
                    LOGGER.error("Couldn't fetch templates in reset world", (Throwable)lv6);
                }
            }
        }.start();
        this.titleLabel = this.addChild(new RealmsLabel(this.title, this.width / 2, 7, 0xFFFFFF));
        this.subtitleLabel = this.addChild(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.generate"), field_22708, arg -> this.client.openScreen(new RealmsResetNormalWorldScreen(this, this.title))));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.upload"), field_22714, arg -> {
            RealmsSelectFileToUploadScreen lv = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.field_22712);
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.template"), field_22716, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.NORMAL, this.field_20495);
            lv.setTitle(new TranslatableText("mco.reset.world.template"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.adventure"), field_22715, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.ADVENTUREMAP, this.field_20496);
            lv.setTitle(new TranslatableText("mco.reset.world.adventure"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.experience"), field_22709, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.EXPERIENCE, this.field_20497);
            lv.setTitle(new TranslatableText("mco.reset.world.experience"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.inspiration"), field_22710, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.INSPIRATION, this.field_20498);
            lv.setTitle(new TranslatableText("mco.reset.world.inspiration"));
            this.client.openScreen(lv);
        }));
        this.narrateLabels();
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

    private int frame(int i) {
        return this.width / 2 - 130 + (i - 1) * 100;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.titleLabel.render(this, arg);
        this.subtitleLabel.render(this, arg);
        super.render(arg, i, j, f);
    }

    private void drawFrame(MatrixStack arg, int i, int j, Text arg2, Identifier arg3, boolean bl, boolean bl2) {
        this.client.getTextureManager().bindTexture(arg3);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        DrawableHelper.drawTexture(arg, i + 2, j + 14, 0.0f, 0.0f, 56, 56, 56, 56);
        this.client.getTextureManager().bindTexture(field_22713);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        DrawableHelper.drawTexture(arg, i, j + 12, 0.0f, 0.0f, 60, 60, 60, 60);
        int k = bl ? 0xA0A0A0 : 0xFFFFFF;
        this.drawStringWithShadow(arg, this.textRenderer, arg2, i + 30, j, k);
    }

    @Override
    protected void callback(@Nullable WorldTemplate arg) {
        if (arg == null) {
            return;
        }
        if (this.slot == -1) {
            this.resetWorldWithTemplate(arg);
        } else {
            switch (arg.type) {
                case WORLD_TEMPLATE: {
                    this.typeToReset = ResetType.SURVIVAL_SPAWN;
                    break;
                }
                case ADVENTUREMAP: {
                    this.typeToReset = ResetType.ADVENTURE;
                    break;
                }
                case EXPERIENCE: {
                    this.typeToReset = ResetType.EXPERIENCE;
                    break;
                }
                case INSPIRATION: {
                    this.typeToReset = ResetType.INSPIRATION;
                }
            }
            this.field_20500 = arg;
            this.switchSlot();
        }
    }

    private void switchSlot() {
        this.switchSlot(() -> {
            switch (this.typeToReset) {
                case ADVENTURE: 
                case SURVIVAL_SPAWN: 
                case EXPERIENCE: 
                case INSPIRATION: {
                    if (this.field_20500 == null) break;
                    this.resetWorldWithTemplate(this.field_20500);
                    break;
                }
                case GENERATE: {
                    if (this.field_20499 == null) break;
                    this.triggerResetWorld(this.field_20499);
                    break;
                }
            }
        });
    }

    public void switchSlot(Runnable runnable) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, this.slot, runnable)));
    }

    public void resetWorldWithTemplate(WorldTemplate arg) {
        this.method_25207(null, arg, -1, true);
    }

    private void triggerResetWorld(ResetWorldInfo arg) {
        this.method_25207(arg.seed, null, arg.levelType, arg.generateStructures);
    }

    private void method_25207(@Nullable String string, @Nullable WorldTemplate arg, int i, boolean bl) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ResettingWorldTask(string, arg, i, bl, this.serverData.id, this.field_20501, this.field_22711)));
    }

    public void resetWorld(ResetWorldInfo arg) {
        if (this.slot == -1) {
            this.triggerResetWorld(arg);
        } else {
            this.typeToReset = ResetType.GENERATE;
            this.field_20499 = arg;
            this.switchSlot();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FrameButton
    extends ButtonWidget {
        private final Identifier image;

        public FrameButton(int i, int j, Text arg2, Identifier arg3, ButtonWidget.PressAction arg4) {
            super(i, j, 60, 72, arg2, arg4);
            this.image = arg3;
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            RealmsResetWorldScreen.this.drawFrame(arg, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver(i, j));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResetWorldInfo {
        private final String seed;
        private final int levelType;
        private final boolean generateStructures;

        public ResetWorldInfo(String string, int i, boolean bl) {
            this.seed = string;
            this.levelType = i;
            this.generateStructures = bl;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum ResetType {
        NONE,
        GENERATE,
        UPLOAD,
        ADVENTURE,
        SURVIVAL_SPAWN,
        EXPERIENCE,
        INSPIRATION;

    }
}

