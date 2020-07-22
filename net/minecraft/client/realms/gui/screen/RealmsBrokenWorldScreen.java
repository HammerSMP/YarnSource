/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.OpenServerTask;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.SwitchSlotTask;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen parent;
    private final RealmsMainScreen mainScreen;
    private RealmsServer field_20492;
    private final long serverId;
    private final Text field_24204;
    private final Text[] message = new Text[]{new TranslatableText("mco.brokenworld.message.line1"), new TranslatableText("mco.brokenworld.message.line2")};
    private int left_x;
    private int right_x;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen parent, RealmsMainScreen mainScreen, long serverId, boolean bl) {
        this.parent = parent;
        this.mainScreen = mainScreen;
        this.serverId = serverId;
        this.field_24204 = bl ? new TranslatableText("mco.brokenworld.minigame.title") : new TranslatableText("mco.brokenworld.title");
    }

    @Override
    public void init() {
        this.left_x = this.width / 2 - 150;
        this.right_x = this.width / 2 + 190;
        this.addButton(new ButtonWidget(this.right_x - 80 + 8, RealmsBrokenWorldScreen.row(13) - 5, 70, 20, ScreenTexts.BACK, arg -> this.backButtonClicked()));
        if (this.field_20492 == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
        this.client.keyboard.enableRepeatEvents(true);
        Realms.narrateNow(Stream.concat(Stream.of(this.field_24204), Stream.of(this.message)).map(Text::getString).collect(Collectors.joining(" ")));
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            ButtonWidget lv2;
            boolean bl;
            int i = entry.getKey();
            boolean bl2 = bl = i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
            if (bl) {
                ButtonWidget lv = new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableText("mco.brokenworld.play"), arg -> {
                    if (this.field_20492.slots.get((Object)Integer.valueOf((int)i)).empty) {
                        RealmsResetWorldScreen lv = new RealmsResetWorldScreen(this, this.field_20492, new TranslatableText("mco.configure.world.switch.slot"), new TranslatableText("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, ScreenTexts.CANCEL, this::method_25123, () -> {
                            this.client.openScreen(this);
                            this.method_25123();
                        });
                        lv.setSlot(i);
                        lv.setResetTitle(I18n.translate("mco.create.world.reset.title", new Object[0]));
                        this.client.openScreen(lv);
                    } else {
                        this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.field_20492.id, i, this::method_25123)));
                    }
                });
            } else {
                lv2 = new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(8), 80, 20, new TranslatableText("mco.brokenworld.download"), arg -> {
                    TranslatableText lv = new TranslatableText("mco.configure.world.restore.download.question.line1");
                    TranslatableText lv2 = new TranslatableText("mco.configure.world.restore.download.question.line2");
                    this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                        if (bl) {
                            this.downloadWorld(i);
                        } else {
                            this.client.openScreen(this);
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
                });
            }
            if (this.slotsThatHasBeenDownloaded.contains(i)) {
                lv2.active = false;
                lv2.setMessage(new TranslatableText("mco.brokenworld.downloaded"));
            }
            this.addButton(lv2);
            this.addButton(new ButtonWidget(this.getFramePositionX(i), RealmsBrokenWorldScreen.row(10), 80, 20, new TranslatableText("mco.brokenworld.reset"), arg -> {
                RealmsResetWorldScreen lv = new RealmsResetWorldScreen(this, this.field_20492, this::method_25123, () -> {
                    this.client.openScreen(this);
                    this.method_25123();
                });
                if (i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME) {
                    lv.setSlot(i);
                }
                this.client.openScreen(lv);
            }));
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.field_24204, this.width / 2, 17, 0xFFFFFF);
        for (int k = 0; k < this.message.length; ++k) {
            this.drawCenteredText(matrices, this.textRenderer, this.message[k], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + k * 12, 0xA0A0A0);
        }
        if (this.field_20492 == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
            if (entry.getValue().templateImage != null && entry.getValue().templateId != -1L) {
                this.drawSlotFrame(matrices, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), entry.getValue().templateId, entry.getValue().templateImage, entry.getValue().empty);
                continue;
            }
            this.drawSlotFrame(matrices, this.getFramePositionX(entry.getKey()), RealmsBrokenWorldScreen.row(1) + 5, mouseX, mouseY, this.field_20492.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, null, entry.getValue().empty);
        }
    }

    private int getFramePositionX(int i) {
        return this.left_x + (i - 1) * 110;
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
        this.client.openScreen(this.parent);
    }

    private void fetchServerData(long worldId) {
        new Thread(() -> {
            RealmsClient lv = RealmsClient.createRealmsClient();
            try {
                this.field_20492 = lv.getOwnWorld(worldId);
                this.addButtons();
            }
            catch (RealmsServiceException lv2) {
                LOGGER.error("Couldn't get own world");
                this.client.openScreen(new RealmsGenericErrorScreen(Text.method_30163(lv2.getMessage()), this.parent));
            }
        }).start();
    }

    public void method_25123() {
        new Thread(() -> {
            RealmsClient lv = RealmsClient.createRealmsClient();
            if (this.field_20492.state == RealmsServer.State.CLOSED) {
                this.client.execute(() -> this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.field_20492, this, this.mainScreen, true))));
            } else {
                try {
                    this.mainScreen.newScreen().play(lv.getOwnWorld(this.serverId), this);
                }
                catch (RealmsServiceException lv2) {
                    LOGGER.error("Couldn't get own world");
                    this.client.execute(() -> this.client.openScreen(this.parent));
                }
            }
        }).start();
    }

    private void downloadWorld(int slotId) {
        RealmsClient lv = RealmsClient.createRealmsClient();
        try {
            WorldDownload lv2 = lv.download(this.field_20492.id, slotId);
            RealmsDownloadLatestWorldScreen lv3 = new RealmsDownloadLatestWorldScreen(this, lv2, this.field_20492.getWorldName(slotId), bl -> {
                if (bl) {
                    this.slotsThatHasBeenDownloaded.add(slotId);
                    this.children.clear();
                    this.addButtons();
                } else {
                    this.client.openScreen(this);
                }
            });
            this.client.openScreen(lv3);
        }
        catch (RealmsServiceException lv4) {
            LOGGER.error("Couldn't download world data");
            this.client.openScreen(new RealmsGenericErrorScreen(lv4, (Screen)this));
        }
    }

    private boolean isMinigame() {
        return this.field_20492 != null && this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
    }

    private void drawSlotFrame(MatrixStack arg, int y, int xm, int ym, int l, boolean bl, String string, int m, long n, String string2, boolean bl2) {
        if (bl2) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.EMPTY_FRAME);
        } else if (string2 != null && n != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
        } else if (m == 1) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_0);
        } else if (m == 2) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_2);
        } else if (m == 3) {
            this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_3);
        } else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
        }
        if (!bl) {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        } else if (bl) {
            float f = 0.9f + 0.1f * MathHelper.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        }
        DrawableHelper.drawTexture(arg, y + 3, xm + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.SLOT_FRAME);
        if (bl) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        DrawableHelper.drawTexture(arg, y, xm, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(arg, this.textRenderer, string, y + 40, xm + 66, 0xFFFFFF);
    }
}

