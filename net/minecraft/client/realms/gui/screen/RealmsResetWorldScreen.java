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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.ResettingWorldTask;
import net.minecraft.client.realms.SwitchSlotTask;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetNormalWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreenWithCallback;
import net.minecraft.client.realms.gui.screen.RealmsSelectFileToUploadScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsResetWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen parent;
    private final RealmsServer serverData;
    private RealmsLabel titleLabel;
    private RealmsLabel subtitleLabel;
    private Text title = new TranslatableText("mco.reset.world.title");
    private Text subtitle = new TranslatableText("mco.reset.world.warning");
    private Text buttonTitle = ScreenTexts.CANCEL;
    private int subtitleColor = 0xFF0000;
    private static final Identifier SLOT_FRAME_TEXTURE = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    private static final Identifier UPLOAD_TEXTURE = new Identifier("realms", "textures/gui/realms/upload.png");
    private static final Identifier ADVENTURE_TEXTURE = new Identifier("realms", "textures/gui/realms/adventure.png");
    private static final Identifier SURVIVAL_SPAWN_TEXTURE = new Identifier("realms", "textures/gui/realms/survival_spawn.png");
    private static final Identifier NEW_WORLD_TEXTURE = new Identifier("realms", "textures/gui/realms/new_world.png");
    private static final Identifier EXPERIENCE_TEXTURE = new Identifier("realms", "textures/gui/realms/experience.png");
    private static final Identifier INSPIRATION_TEXTURE = new Identifier("realms", "textures/gui/realms/inspiration.png");
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

    public RealmsResetWorldScreen(Screen parent, RealmsServer arg2, Runnable runnable, Runnable runnable2) {
        this.parent = parent;
        this.serverData = arg2;
        this.field_22711 = runnable;
        this.field_22712 = runnable2;
    }

    public RealmsResetWorldScreen(Screen parent, RealmsServer arg2, Text arg3, Text arg4, int i, Text arg5, Runnable runnable, Runnable runnable2) {
        this(parent, arg2, runnable, runnable2);
        this.title = arg3;
        this.subtitle = arg4;
        this.subtitleColor = i;
        this.buttonTitle = arg5;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setResetTitle(String title) {
        this.field_20501 = title;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 40, RealmsResetWorldScreen.row(14) - 10, 80, 20, this.buttonTitle, arg -> this.client.openScreen(this.parent)));
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
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.generate"), NEW_WORLD_TEXTURE, arg -> this.client.openScreen(new RealmsResetNormalWorldScreen(this, this.title))));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.upload"), UPLOAD_TEXTURE, arg -> {
            RealmsSelectFileToUploadScreen lv = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.field_22712);
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(0) + 10, new TranslatableText("mco.reset.world.template"), SURVIVAL_SPAWN_TEXTURE, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.NORMAL, this.field_20495);
            lv.setTitle(new TranslatableText("mco.reset.world.template"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.adventure"), ADVENTURE_TEXTURE, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.ADVENTUREMAP, this.field_20496);
            lv.setTitle(new TranslatableText("mco.reset.world.adventure"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.experience"), EXPERIENCE_TEXTURE, arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.EXPERIENCE, this.field_20497);
            lv.setTitle(new TranslatableText("mco.reset.world.experience"));
            this.client.openScreen(lv);
        }));
        this.addButton(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(6) + 20, new TranslatableText("mco.reset.world.inspiration"), INSPIRATION_TEXTURE, arg -> {
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int frame(int i) {
        return this.width / 2 - 130 + (i - 1) * 100;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.titleLabel.render(this, matrices);
        this.subtitleLabel.render(this, matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawFrame(MatrixStack matrices, int x, int y, Text text, Identifier arg3, boolean bl, boolean bl2) {
        this.client.getTextureManager().bindTexture(arg3);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        DrawableHelper.drawTexture(matrices, x + 2, y + 14, 0.0f, 0.0f, 56, 56, 56, 56);
        this.client.getTextureManager().bindTexture(SLOT_FRAME_TEXTURE);
        if (bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        DrawableHelper.drawTexture(matrices, x, y + 12, 0.0f, 0.0f, 60, 60, 60, 60);
        int k = bl ? 0xA0A0A0 : 0xFFFFFF;
        this.drawCenteredText(matrices, this.textRenderer, text, x + 30, y, k);
    }

    @Override
    protected void callback(@Nullable WorldTemplate template) {
        if (template == null) {
            return;
        }
        if (this.slot == -1) {
            this.resetWorldWithTemplate(template);
        } else {
            switch (template.type) {
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
            this.field_20500 = template;
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

    public void switchSlot(Runnable callback) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, this.slot, callback)));
    }

    public void resetWorldWithTemplate(WorldTemplate template) {
        this.method_25207(null, template, -1, true);
    }

    private void triggerResetWorld(ResetWorldInfo resetWorldInfo) {
        this.method_25207(resetWorldInfo.seed, null, resetWorldInfo.levelType, resetWorldInfo.generateStructures);
    }

    private void method_25207(@Nullable String string, @Nullable WorldTemplate arg, int i, boolean bl) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new ResettingWorldTask(string, arg, i, bl, this.serverData.id, this.field_20501, this.field_22711)));
    }

    public void resetWorld(ResetWorldInfo resetWorldInfo) {
        if (this.slot == -1) {
            this.triggerResetWorld(resetWorldInfo);
        } else {
            this.typeToReset = ResetType.GENERATE;
            this.field_20499 = resetWorldInfo;
            this.switchSlot();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FrameButton
    extends ButtonWidget {
        private final Identifier image;

        public FrameButton(int x, int y, Text arg2, Identifier arg3, ButtonWidget.PressAction arg4) {
            super(x, y, 60, 72, arg2, arg4);
            this.image = arg3;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsResetWorldScreen.this.drawFrame(matrices, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver(mouseX, mouseY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResetWorldInfo {
        private final String seed;
        private final int levelType;
        private final boolean generateStructures;

        public ResetWorldInfo(String seed, int levelType, boolean generateStructures) {
            this.seed = seed;
            this.levelType = levelType;
            this.generateStructures = generateStructures;
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

