/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsDataFetcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier field_22700 = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
    private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
    private volatile int numberOfPendingInvites;
    private static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    private static boolean validClient;
    private static boolean hasUnreadNews;

    @Override
    public void init() {
        this.checkIfMcoEnabled();
        this.client.keyboard.enableRepeatEvents(true);
    }

    @Override
    public void tick() {
        if (!(this.method_25169() && this.method_25170() && validClient || REALMS_DATA_FETCHER.isStopped())) {
            REALMS_DATA_FETCHER.stop();
            return;
        }
        if (!validClient || !this.method_25169()) {
            return;
        }
        REALMS_DATA_FETCHER.initWithSpecificTaskList();
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = REALMS_DATA_FETCHER.isTrialAvailable();
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
        }
        REALMS_DATA_FETCHER.markClean();
    }

    private boolean method_25169() {
        return this.client.options.realmsNotifications;
    }

    private boolean method_25170() {
        return this.client.currentScreen instanceof TitleScreen;
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient lv = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse lv2 = lv.clientCompatible();
                        if (lv2 != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    }
                    catch (RealmsServiceException lv3) {
                        if (lv3.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (validClient) {
            this.drawIcons(matrices, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawIcons(MatrixStack matrices, int mouseX, int mouseY) {
        int k = this.numberOfPendingInvites;
        int l = 24;
        int m = this.height / 4 + 48;
        int n = this.width / 2 + 80;
        int o = m + 48 + 2;
        int p = 0;
        if (hasUnreadNews) {
            this.client.getTextureManager().bindTexture(field_22700);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.4f, 0.4f, 0.4f);
            DrawableHelper.drawTexture(matrices, (int)((double)(n + 2 - p) * 2.5), (int)((double)o * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
            RenderSystem.popMatrix();
            p += 14;
        }
        if (k != 0) {
            this.client.getTextureManager().bindTexture(INVITE_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrices, n - p, o - 6, 0.0f, 0.0f, 15, 25, 31, 25);
            p += 16;
        }
        if (trialAvailable) {
            this.client.getTextureManager().bindTexture(TRIAL_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int q = 0;
            if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                q = 8;
            }
            DrawableHelper.drawTexture(matrices, n + 4 - p, o + 4, 0.0f, q, 8, 8, 8, 16);
        }
    }

    @Override
    public void removed() {
        REALMS_DATA_FETCHER.stop();
    }
}

