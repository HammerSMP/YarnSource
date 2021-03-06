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
import net.minecraft.client.realms.CloseServerTask;
import net.minecraft.client.realms.OpenServerTask;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.SwitchMinigameTask;
import net.minecraft.client.realms.SwitchSlotTask;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsPlayerScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreenWithCallback;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.gui.screen.RealmsSettingsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSubscriptionInfoScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConfigureWorldScreen
extends RealmsScreenWithCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
    private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
    private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
    private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
    private Text toolTip;
    private final RealmsMainScreen parent;
    @Nullable
    private RealmsServer server;
    private final long serverId;
    private int left_x;
    private int right_x;
    private ButtonWidget playersButton;
    private ButtonWidget settingsButton;
    private ButtonWidget subscriptionButton;
    private ButtonWidget optionsButton;
    private ButtonWidget backupButton;
    private ButtonWidget resetWorldButton;
    private ButtonWidget switchMinigameButton;
    private boolean stateChanged;
    private int animTick;
    private int clicks;

    public RealmsConfigureWorldScreen(RealmsMainScreen parent, long serverId) {
        this.parent = parent;
        this.serverId = serverId;
    }

    @Override
    public void init() {
        if (this.server == null) {
            this.fetchServerData(this.serverId);
        }
        this.left_x = this.width / 2 - 187;
        this.right_x = this.width / 2 + 190;
        this.client.keyboard.enableRepeatEvents(true);
        this.playersButton = this.addButton(new ButtonWidget(this.buttonCenter(0, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.players"), arg -> this.client.openScreen(new RealmsPlayerScreen(this, this.server))));
        this.settingsButton = this.addButton(new ButtonWidget(this.buttonCenter(1, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.settings"), arg -> this.client.openScreen(new RealmsSettingsScreen(this, this.server.clone()))));
        this.subscriptionButton = this.addButton(new ButtonWidget(this.buttonCenter(2, 3), RealmsConfigureWorldScreen.row(0), 100, 20, new TranslatableText("mco.configure.world.buttons.subscription"), arg -> this.client.openScreen(new RealmsSubscriptionInfoScreen(this, this.server.clone(), this.parent))));
        for (int i = 1; i < 5; ++i) {
            this.addSlotButton(i);
        }
        this.switchMinigameButton = this.addButton(new ButtonWidget(this.buttonLeft(0), RealmsConfigureWorldScreen.row(13) - 5, 100, 20, new TranslatableText("mco.configure.world.buttons.switchminigame"), arg -> {
            RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
            lv.setTitle(new TranslatableText("mco.template.title.minigame"));
            this.client.openScreen(lv);
        }));
        this.optionsButton = this.addButton(new ButtonWidget(this.buttonLeft(0), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.buttons.options"), arg -> this.client.openScreen(new RealmsSlotOptionsScreen(this, this.server.slots.get(this.server.activeSlot).clone(), this.server.worldType, this.server.activeSlot))));
        this.backupButton = this.addButton(new ButtonWidget(this.buttonLeft(1), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.backup"), arg -> this.client.openScreen(new RealmsBackupScreen(this, this.server.clone(), this.server.activeSlot))));
        this.resetWorldButton = this.addButton(new ButtonWidget(this.buttonLeft(2), RealmsConfigureWorldScreen.row(13) - 5, 90, 20, new TranslatableText("mco.configure.world.buttons.resetworld"), arg -> this.client.openScreen(new RealmsResetWorldScreen(this, this.server.clone(), () -> this.client.openScreen(this.getNewScreen()), () -> this.client.openScreen(this.getNewScreen())))));
        this.addButton(new ButtonWidget(this.right_x - 80 + 8, RealmsConfigureWorldScreen.row(13) - 5, 70, 20, ScreenTexts.BACK, arg -> this.backButtonClicked()));
        this.backupButton.active = true;
        if (this.server == null) {
            this.hideMinigameButtons();
            this.hideRegularButtons();
            this.playersButton.active = false;
            this.settingsButton.active = false;
            this.subscriptionButton.active = false;
        } else {
            this.disableButtons();
            if (this.isMinigame()) {
                this.hideRegularButtons();
            } else {
                this.hideMinigameButtons();
            }
        }
    }

    private void addSlotButton(int slotIndex) {
        int j = this.frame(slotIndex);
        int k = RealmsConfigureWorldScreen.row(5) + 5;
        RealmsWorldSlotButton lv = new RealmsWorldSlotButton(j, k, 80, 80, () -> this.server, arg -> {
            this.toolTip = arg;
        }, slotIndex, arg -> {
            RealmsWorldSlotButton.State lv = ((RealmsWorldSlotButton)arg).getState();
            if (lv != null) {
                switch (lv.action) {
                    case NOTHING: {
                        break;
                    }
                    case JOIN: {
                        this.joinRealm(this.server);
                        break;
                    }
                    case SWITCH_SLOT: {
                        if (lv.minigame) {
                            this.switchToMinigame();
                            break;
                        }
                        if (lv.empty) {
                            this.switchToEmptySlot(slotIndex, this.server);
                            break;
                        }
                        this.switchToFullSlot(slotIndex, this.server);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown action " + (Object)((Object)lv.action));
                    }
                }
            }
        });
        this.addButton(lv);
    }

    private int buttonLeft(int i) {
        return this.left_x + i * 95;
    }

    private int buttonCenter(int i, int total) {
        return this.width / 2 - (total * 105 - 5) / 2 + i * 105;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.toolTip = null;
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("mco.configure.worlds.title", new Object[0]), this.width / 2, RealmsConfigureWorldScreen.row(4), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.server == null) {
            this.drawCenteredString(matrices, this.textRenderer, I18n.translate("mco.configure.world.title", new Object[0]), this.width / 2, 17, 0xFFFFFF);
            return;
        }
        String string = this.server.getName();
        int k = this.textRenderer.getWidth(string);
        int l = this.server.state == RealmsServer.State.CLOSED ? 0xA0A0A0 : 0x7FFF7F;
        int m = this.textRenderer.getWidth(I18n.translate("mco.configure.world.title", new Object[0]));
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("mco.configure.world.title", new Object[0]), this.width / 2, 12, 0xFFFFFF);
        this.drawCenteredString(matrices, this.textRenderer, string, this.width / 2, 24, l);
        int n = Math.min(this.buttonCenter(2, 3) + 80 - 11, this.width / 2 + k / 2 + m / 2 + 10);
        this.drawServerStatus(matrices, n, 7, mouseX, mouseY);
        if (this.isMinigame()) {
            this.textRenderer.draw(matrices, I18n.translate("mco.configure.current.minigame", new Object[0]) + ": " + this.server.getMinigameName(), (float)(this.left_x + 80 + 20 + 10), (float)RealmsConfigureWorldScreen.row(13), 0xFFFFFF);
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
        }
    }

    private int frame(int ordinal) {
        return this.left_x + (ordinal - 1) * 98;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        if (this.stateChanged) {
            this.parent.removeSelection();
        }
        this.client.openScreen(this.parent);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient lv = RealmsClient.createRealmsClient();
            try {
                this.server = lv.getOwnWorld(worldId);
                this.disableButtons();
                if (this.isMinigame()) {
                    this.addButton(this.switchMinigameButton);
                } else {
                    this.addButton(this.optionsButton);
                    this.addButton(this.backupButton);
                    this.addButton(this.resetWorldButton);
                }
            }
            catch (RealmsServiceException lv2) {
                LOGGER.error("Couldn't get own world");
                this.client.execute(() -> this.client.openScreen(new RealmsGenericErrorScreen(Text.method_30163(lv2.getMessage()), (Screen)this.parent)));
            }
        }).start();
    }

    private void disableButtons() {
        this.playersButton.active = !this.server.expired;
        this.settingsButton.active = !this.server.expired;
        this.subscriptionButton.active = true;
        this.switchMinigameButton.active = !this.server.expired;
        this.optionsButton.active = !this.server.expired;
        this.resetWorldButton.active = !this.server.expired;
    }

    private void joinRealm(RealmsServer serverData) {
        if (this.server.state == RealmsServer.State.OPEN) {
            this.parent.play(serverData, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
        } else {
            this.openTheWorld(true, new RealmsConfigureWorldScreen(this.parent.newScreen(), this.serverId));
        }
    }

    private void switchToMinigame() {
        RealmsSelectWorldTemplateScreen lv = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
        lv.setTitle(new TranslatableText("mco.template.title.minigame"));
        lv.setWarning(new TranslatableText("mco.minigame.world.info.line1"), new TranslatableText("mco.minigame.world.info.line2"));
        this.client.openScreen(lv);
    }

    private void switchToFullSlot(int selectedSlot, RealmsServer serverData) {
        TranslatableText lv = new TranslatableText("mco.configure.world.slot.switch.question.line1");
        TranslatableText lv2 = new TranslatableText("mco.configure.world.slot.switch.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(arg.id, selectedSlot, () -> this.client.openScreen(this.getNewScreen()))));
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
    }

    private void switchToEmptySlot(int selectedSlot, RealmsServer serverData) {
        TranslatableText lv = new TranslatableText("mco.configure.world.slot.switch.question.line1");
        TranslatableText lv2 = new TranslatableText("mco.configure.world.slot.switch.question.line2");
        this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
            if (bl) {
                RealmsResetWorldScreen lv = new RealmsResetWorldScreen(this, serverData, new TranslatableText("mco.configure.world.switch.slot"), new TranslatableText("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, ScreenTexts.CANCEL, () -> this.client.openScreen(this.getNewScreen()), () -> this.client.openScreen(this.getNewScreen()));
                lv.setSlot(selectedSlot);
                lv.setResetTitle(I18n.translate("mco.create.world.reset.title", new Object[0]));
                this.client.openScreen(lv);
            } else {
                this.client.openScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
    }

    protected void renderMousehoverTooltip(MatrixStack arg, Text arg2, int i, int j) {
        int k = i + 12;
        int l = j - 12;
        int m = this.textRenderer.getWidth(arg2);
        if (k + m + 3 > this.right_x) {
            k = k - m - 20;
        }
        this.fillGradient(arg, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(arg, arg2, (float)k, (float)l, 0xFFFFFF);
    }

    private void drawServerStatus(MatrixStack arg, int i, int j, int k, int l) {
        if (this.server.expired) {
            this.drawExpired(arg, i, j, k, l);
        } else if (this.server.state == RealmsServer.State.CLOSED) {
            this.drawClosed(arg, i, j, k, l);
        } else if (this.server.state == RealmsServer.State.OPEN) {
            if (this.server.daysLeft < 7) {
                this.drawExpiring(arg, i, j, k, l, this.server.daysLeft);
            } else {
                this.drawOpen(arg, i, j, k, l);
            }
        }
    }

    private void drawExpired(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(EXPIRED_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = new TranslatableText("mco.selectServer.expired");
        }
    }

    private void drawExpiring(MatrixStack arg, int i, int j, int k, int l, int m) {
        this.client.getTextureManager().bindTexture(EXPIRES_SOON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(arg, i, j, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = m <= 0 ? new TranslatableText("mco.selectServer.expires.soon") : (m == 1 ? new TranslatableText("mco.selectServer.expires.day") : new TranslatableText("mco.selectServer.expires.days", m));
        }
    }

    private void drawOpen(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(ON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = new TranslatableText("mco.selectServer.open");
        }
    }

    private void drawClosed(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(OFF_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.toolTip = new TranslatableText("mco.selectServer.closed");
        }
    }

    private boolean isMinigame() {
        return this.server != null && this.server.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void hideRegularButtons() {
        this.removeButton(this.optionsButton);
        this.removeButton(this.backupButton);
        this.removeButton(this.resetWorldButton);
    }

    private void removeButton(ButtonWidget button) {
        button.visible = false;
        this.children.remove(button);
        this.buttons.remove(button);
    }

    private void addButton(ButtonWidget button) {
        button.visible = true;
        this.addButton(button);
    }

    private void hideMinigameButtons() {
        this.removeButton(this.switchMinigameButton);
    }

    public void saveSlotSettings(RealmsWorldOptions options) {
        RealmsWorldOptions lv = this.server.slots.get(this.server.activeSlot);
        options.templateId = lv.templateId;
        options.templateImage = lv.templateImage;
        RealmsClient lv2 = RealmsClient.createRealmsClient();
        try {
            lv2.updateSlot(this.server.id, this.server.activeSlot, options);
            this.server.slots.put(this.server.activeSlot, options);
        }
        catch (RealmsServiceException lv3) {
            LOGGER.error("Couldn't save slot settings");
            this.client.openScreen(new RealmsGenericErrorScreen(lv3, (Screen)this));
            return;
        }
        this.client.openScreen(this);
    }

    public void saveSettings(String name, String desc) {
        String string3 = desc.trim().isEmpty() ? null : desc;
        RealmsClient lv = RealmsClient.createRealmsClient();
        try {
            lv.update(this.server.id, name, string3);
            this.server.setName(name);
            this.server.setDescription(string3);
        }
        catch (RealmsServiceException lv2) {
            LOGGER.error("Couldn't save settings");
            this.client.openScreen(new RealmsGenericErrorScreen(lv2, (Screen)this));
            return;
        }
        this.client.openScreen(this);
    }

    public void openTheWorld(boolean join, Screen screen) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new OpenServerTask(this.server, this, this.parent, join)));
    }

    public void closeTheWorld(Screen screen) {
        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new CloseServerTask(this.server, this)));
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    @Override
    protected void callback(@Nullable WorldTemplate template) {
        if (template == null) {
            return;
        }
        if (WorldTemplate.WorldTemplateType.MINIGAME == template.type) {
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchMinigameTask(this.server.id, template, this.getNewScreen())));
        }
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        return new RealmsConfigureWorldScreen(this.parent, this.serverId);
    }
}

