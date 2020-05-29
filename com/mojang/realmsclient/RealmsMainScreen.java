/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.KeyCombo;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsGetServerDetailsTask;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsMainScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
    private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
    private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
    private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final Identifier LEAVE_ICON = new Identifier("realms", "textures/gui/realms/leave_icon.png");
    private static final Identifier INVITATION_ICON = new Identifier("realms", "textures/gui/realms/invitation_icons.png");
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier WORLD_ICON = new Identifier("realms", "textures/gui/realms/world_icon.png");
    private static final Identifier REALMS = new Identifier("realms", "textures/gui/title/realms.png");
    private static final Identifier CONFIGURE_ICON = new Identifier("realms", "textures/gui/realms/configure_icon.png");
    private static final Identifier QUESTIONMARK = new Identifier("realms", "textures/gui/realms/questionmark.png");
    private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
    private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
    private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
    private static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier WIDGETS = new Identifier("minecraft", "textures/gui/widgets.png");
    private static List<Identifier> IMAGES = ImmutableList.of();
    private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
    private static boolean overrideConfigure;
    private static int lastScrollYPosition;
    private static volatile boolean hasParentalConsent;
    private static volatile boolean checkedParentalConsent;
    private static volatile boolean checkedClientCompatability;
    private static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter rateLimiter;
    private boolean dontSetConnectedToRealms;
    private final Screen lastScreen;
    private volatile RealmSelectionList realmSelectionList;
    private long selectedServerId = -1L;
    private ButtonWidget playButton;
    private ButtonWidget backButton;
    private ButtonWidget renewButton;
    private ButtonWidget configureButton;
    private ButtonWidget leaveButton;
    private List<Text> toolTip;
    private List<RealmsServer> realmsServers = Lists.newArrayList();
    private volatile int numberOfPendingInvites;
    private int animTick;
    private boolean hasFetchedServers;
    private boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
    private volatile boolean createdTrial;
    private volatile boolean showingPopup;
    private volatile boolean hasUnreadNews;
    private volatile String newsLink;
    private int carouselIndex;
    private int carouselTick;
    private boolean hasSwitchedCarouselImage;
    private List<KeyCombo> keyCombos;
    private int clicks;
    private ReentrantLock connectLock = new ReentrantLock();
    private class_5220 field_24198;
    private ButtonWidget showPopupButton;
    private ButtonWidget pendingInvitesButton;
    private ButtonWidget newsButton;
    private ButtonWidget createTrialButton;
    private ButtonWidget buyARealmButton;
    private ButtonWidget closeButton;

    public RealmsMainScreen(Screen arg) {
        this.lastScreen = arg;
        this.rateLimiter = RateLimiter.create((double)0.01666666753590107);
    }

    public boolean shouldShowMessageInList() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialsAvailable && !this.createdTrial) {
            return true;
        }
        for (RealmsServer lv : this.realmsServers) {
            if (!lv.ownerUUID.equals(this.client.getSession().getUuid())) continue;
            return false;
        }
        return true;
    }

    public boolean shouldShowPopup() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.popupOpenedByUser) {
            return true;
        }
        if (this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty()) {
            return true;
        }
        return this.realmsServers.isEmpty();
    }

    @Override
    public void init() {
        this.keyCombos = Lists.newArrayList((Object[])new KeyCombo[]{new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
            overrideConfigure = !overrideConfigure;
        }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
                this.switchToProd();
            } else {
                this.switchToStage();
            }
        }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
                this.switchToProd();
            } else {
                this.switchToLocal();
            }
        })});
        if (realmsGenericErrorScreen != null) {
            this.client.openScreen(realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (checkedClientCompatability && !RealmsMainScreen.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatability();
        this.checkUnreadNews();
        if (!this.dontSetConnectedToRealms) {
            this.client.setConnectedToRealms(false);
        }
        this.client.keyboard.enableRepeatEvents(true);
        if (RealmsMainScreen.hasParentalConsent()) {
            realmsDataFetcher.forceUpdate();
        }
        this.showingPopup = false;
        if (RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers) {
            this.addButtons();
        }
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addChild(this.realmSelectionList);
        this.focusOn(this.realmSelectionList);
    }

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addButtons() {
        this.configureButton = this.addButton(new ButtonWidget(this.width / 2 - 190, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.configure"), arg -> this.configureClicked(this.findServer(this.selectedServerId))));
        this.playButton = this.addButton(new ButtonWidget(this.width / 2 - 93, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.play"), arg -> {
            RealmsServer lv = this.findServer(this.selectedServerId);
            if (lv == null) {
                return;
            }
            this.play(lv, this);
        }));
        this.backButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 32, 90, 20, ScreenTexts.BACK, arg -> {
            if (!this.justClosedPopup) {
                this.client.openScreen(this.lastScreen);
            }
        }));
        this.renewButton = this.addButton(new ButtonWidget(this.width / 2 + 100, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.expiredRenew"), arg -> this.onRenew()));
        this.leaveButton = this.addButton(new ButtonWidget(this.width / 2 - 202, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.leave"), arg -> this.leaveClicked(this.findServer(this.selectedServerId))));
        this.pendingInvitesButton = this.addButton(new PendingInvitesButton());
        this.newsButton = this.addButton(new NewsButton());
        this.showPopupButton = this.addButton(new ShowPopupButton());
        this.closeButton = this.addButton(new CloseButton());
        this.createTrialButton = this.addButton(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslatableText("mco.selectServer.trial"), arg -> {
            if (!this.trialsAvailable || this.createdTrial) {
                return;
            }
            Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
            this.client.openScreen(this.lastScreen);
        }));
        this.buyARealmButton = this.addButton(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslatableText("mco.selectServer.buy"), arg -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms")));
        RealmsServer lv = this.findServer(this.selectedServerId);
        this.updateButtonStates(lv);
    }

    private void updateButtonStates(@Nullable RealmsServer arg) {
        boolean bl;
        this.playButton.active = this.shouldPlayButtonBeActive(arg) && !this.shouldShowPopup();
        this.renewButton.visible = this.shouldRenewButtonBeActive(arg);
        this.configureButton.visible = this.shouldConfigureButtonBeVisible(arg);
        this.leaveButton.visible = this.shouldLeaveButtonBeVisible(arg);
        this.createTrialButton.visible = bl = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
        this.createTrialButton.active = bl;
        this.buyARealmButton.visible = this.shouldShowPopup();
        this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
        this.renewButton.active = !this.shouldShowPopup();
        this.configureButton.active = !this.shouldShowPopup();
        this.leaveButton.active = !this.shouldShowPopup();
        this.newsButton.active = true;
        this.pendingInvitesButton.active = true;
        this.backButton.active = true;
        this.showPopupButton.active = !this.shouldShowPopup();
    }

    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers;
    }

    private boolean shouldPlayButtonBeActive(@Nullable RealmsServer arg) {
        return arg != null && !arg.expired && arg.state == RealmsServer.State.OPEN;
    }

    private boolean shouldRenewButtonBeActive(@Nullable RealmsServer arg) {
        return arg != null && arg.expired && this.isSelfOwnedServer(arg);
    }

    private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer arg) {
        return arg != null && this.isSelfOwnedServer(arg);
    }

    private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer arg) {
        return arg != null && !this.isSelfOwnedServer(arg);
    }

    @Override
    public void tick() {
        super.tick();
        this.justClosedPopup = false;
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
        if (!RealmsMainScreen.hasParentalConsent()) {
            return;
        }
        realmsDataFetcher.init();
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            boolean bl;
            List<RealmsServer> list = realmsDataFetcher.getServers();
            this.realmSelectionList.clear();
            boolean bl2 = bl = !this.hasFetchedServers;
            if (bl) {
                this.hasFetchedServers = true;
            }
            if (list != null) {
                boolean bl22 = false;
                for (RealmsServer lv : list) {
                    if (!this.method_25001(lv)) continue;
                    bl22 = true;
                }
                this.realmsServers = list;
                if (this.shouldShowMessageInList()) {
                    this.realmSelectionList.addEntry(new RealmSelectionListTrialEntry());
                }
                for (RealmsServer lv2 : this.realmsServers) {
                    this.realmSelectionList.addEntry(new RealmSelectionListEntry(lv2));
                }
                if (!regionsPinged && bl22) {
                    regionsPinged = true;
                    this.pingRegions();
                }
            }
            if (bl) {
                this.addButtons();
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.rateLimiter.tryAcquire(1)) {
                Realms.narrateNow(I18n.translate("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean bl3 = realmsDataFetcher.isTrialAvailable();
            if (bl3 != this.trialsAvailable && this.shouldShowPopup()) {
                this.trialsAvailable = bl3;
                this.showingPopup = false;
            } else {
                this.trialsAvailable = bl3;
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists lv3 = realmsDataFetcher.getLivestats();
            block2: for (RealmsServerPlayerList lv4 : lv3.servers) {
                for (RealmsServer lv5 : this.realmsServers) {
                    if (lv5.id != lv4.serverId) continue;
                    lv5.updateServerPing(lv4);
                    continue block2;
                }
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = realmsDataFetcher.hasUnreadNews();
            this.newsLink = realmsDataFetcher.newsLink();
        }
        realmsDataFetcher.markClean();
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.visible = this.shouldShowPopupButton();
        }
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> list = Ping.pingAllRegions();
            RealmsClient lv = RealmsClient.createRealmsClient();
            PingResult lv2 = new PingResult();
            lv2.pingResults = list;
            lv2.worldIds = this.getOwnedNonExpiredWorldIds();
            try {
                lv.sendPingResults(lv2);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList list = Lists.newArrayList();
        for (RealmsServer lv : this.realmsServers) {
            if (!this.method_25001(lv)) continue;
            list.add(lv.id);
        }
        return list;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        this.stopRealmsFetcher();
    }

    private void onRenew() {
        RealmsServer lv = this.findServer(this.selectedServerId);
        if (lv == null) {
            return;
        }
        String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + lv.remoteSubscriptionId + "&profileId=" + this.client.getSession().getUuid() + "&ref=" + (lv.expiredTrial ? "expiredTrial" : "expiredRealm");
        this.client.keyboard.setClipboard(string);
        Util.getOperatingSystem().open(string);
    }

    private void checkClientCompatability() {
        if (!checkedClientCompatability) {
            checkedClientCompatability = true;
            new Thread("MCO Compatability Checker #1"){

                @Override
                public void run() {
                    RealmsClient lv = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse lv2 = lv.clientCompatible();
                        if (lv2 == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                            return;
                        }
                        if (lv2 == RealmsClient.CompatibleVersionResponse.OTHER) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    }
                    catch (RealmsServiceException lv3) {
                        checkedClientCompatability = false;
                        LOGGER.error("Couldn't connect to realms", (Throwable)lv3);
                        if (lv3.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslatableText("mco.error.invalid.session.title"), new TranslatableText("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(lv3, RealmsMainScreen.this.lastScreen)));
                    }
                }
            }.start();
        }
    }

    private void checkUnreadNews() {
    }

    private void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1"){

            @Override
            public void run() {
                RealmsClient lv = RealmsClient.createRealmsClient();
                try {
                    Boolean lv2 = lv.mcoEnabled();
                    if (lv2.booleanValue()) {
                        LOGGER.info("Realms is available for this user");
                        hasParentalConsent = true;
                    } else {
                        LOGGER.info("Realms is not available for this user");
                        hasParentalConsent = false;
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                    }
                    checkedParentalConsent = true;
                }
                catch (RealmsServiceException lv3) {
                    LOGGER.error("Couldn't connect to realms", (Throwable)lv3);
                    RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(lv3, RealmsMainScreen.this.lastScreen)));
                }
            }
        }.start();
    }

    private void switchToStage() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
            new Thread("MCO Stage Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient lv = RealmsClient.createRealmsClient();
                    try {
                        Boolean lv2 = lv.stageAvailable();
                        if (lv2.booleanValue()) {
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            realmsDataFetcher.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException lv3) {
                        LOGGER.error("Couldn't connect to Realms: " + lv3);
                    }
                }
            }.start();
        }
    }

    private void switchToLocal() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
            new Thread("MCO Local Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient lv = RealmsClient.createRealmsClient();
                    try {
                        Boolean lv2 = lv.stageAvailable();
                        if (lv2.booleanValue()) {
                            RealmsClient.switchToLocal();
                            LOGGER.info("Switched to local");
                            realmsDataFetcher.forceUpdate();
                        }
                    }
                    catch (RealmsServiceException lv3) {
                        LOGGER.error("Couldn't connect to Realms: " + lv3);
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        RealmsClient.switchToProd();
        realmsDataFetcher.forceUpdate();
    }

    private void stopRealmsFetcher() {
        realmsDataFetcher.stop();
    }

    private void configureClicked(RealmsServer arg) {
        if (this.client.getSession().getUuid().equals(arg.ownerUUID) || overrideConfigure) {
            this.saveListScrollPosition();
            this.client.openScreen(new RealmsConfigureWorldScreen(this, arg.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer arg) {
        if (arg != null && !this.client.getSession().getUuid().equals(arg.ownerUUID)) {
            this.saveListScrollPosition();
            TranslatableText lv = new TranslatableText("mco.configure.world.leave.question.line1");
            TranslatableText lv2 = new TranslatableText("mco.configure.world.leave.question.line2");
            this.client.openScreen(new RealmsLongConfirmationScreen(this::method_24991, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
    }

    @Nullable
    private RealmsServer findServer(long l) {
        for (RealmsServer lv : this.realmsServers) {
            if (lv.id != l) continue;
            return lv;
        }
        return null;
    }

    private void method_24991(boolean bl) {
        if (bl) {
            new Thread("Realms-leave-server"){

                @Override
                public void run() {
                    try {
                        RealmsServer lv = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                        if (lv != null) {
                            RealmsClient lv2 = RealmsClient.createRealmsClient();
                            lv2.uninviteMyselfFrom(lv.id);
                            realmsDataFetcher.removeItem(lv);
                            RealmsMainScreen.this.realmsServers.remove(lv);
                            RealmsMainScreen.this.realmSelectionList.children().removeIf(arg -> arg instanceof RealmSelectionListEntry && ((RealmSelectionListEntry)((RealmSelectionListEntry)arg)).mServerData.id == RealmsMainScreen.this.selectedServerId);
                            RealmsMainScreen.this.realmSelectionList.setSelected((Entry)null);
                            RealmsMainScreen.this.updateButtonStates(null);
                            RealmsMainScreen.this.selectedServerId = -1L;
                            ((RealmsMainScreen)RealmsMainScreen.this).playButton.active = false;
                        }
                    }
                    catch (RealmsServiceException lv3) {
                        LOGGER.error("Couldn't configure world");
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(lv3, (Screen)RealmsMainScreen.this)));
                    }
                }
            }.start();
        }
        this.client.openScreen(this);
    }

    public void removeSelection() {
        this.selectedServerId = -1L;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.keyCombos.forEach(KeyCombo::reset);
            this.onClosePopup();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void onClosePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        } else {
            this.client.openScreen(this.lastScreen);
        }
    }

    @Override
    public boolean charTyped(char c, int i) {
        this.keyCombos.forEach(arg -> arg.keyPressed(c));
        return true;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.field_24198 = class_5220.NONE;
        this.toolTip = null;
        this.renderBackground(arg);
        this.realmSelectionList.render(arg, i, j, f);
        this.drawRealmsLogo(arg, this.width / 2 - 50, 7);
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.renderStage(arg);
        }
        if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.renderLocal(arg);
        }
        if (this.shouldShowPopup()) {
            this.drawPopup(arg, i, j);
        } else {
            if (this.showingPopup) {
                this.updateButtonStates(null);
                if (!this.children.contains(this.realmSelectionList)) {
                    this.children.add(this.realmSelectionList);
                }
                RealmsServer lv = this.findServer(this.selectedServerId);
                this.playButton.active = this.shouldPlayButtonBeActive(lv);
            }
            this.showingPopup = false;
        }
        super.render(arg, i, j, f);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(arg, this.toolTip, i, j);
        }
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            this.client.getTextureManager().bindTexture(TRIAL_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int k = 8;
            int l = 8;
            int m = 0;
            if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                m = 8;
            }
            DrawableHelper.drawTexture(arg, this.createTrialButton.x + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y + this.createTrialButton.getHeight() / 2 - 4, 0.0f, m, 8, 8, 8, 16);
        }
    }

    private void drawRealmsLogo(MatrixStack arg, int i, int j) {
        this.client.getTextureManager().bindTexture(REALMS);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        DrawableHelper.drawTexture(arg, i * 2, j * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.isOutsidePopup(d, e) && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
            this.justClosedPopup = true;
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    private boolean isOutsidePopup(double d, double e) {
        int i = this.popupX0();
        int j = this.popupY0();
        return d < (double)(i - 5) || d > (double)(i + 315) || e < (double)(j - 5) || e > (double)(j + 171);
    }

    private void drawPopup(MatrixStack arg, int i, int j) {
        int k = this.popupX0();
        int l = this.popupY0();
        TranslatableText lv = new TranslatableText("mco.selectServer.popup");
        List<class_5348> list = this.textRenderer.wrapLines(lv, 100);
        if (!this.showingPopup) {
            RealmSelectionList lv2;
            this.carouselIndex = 0;
            this.carouselTick = 0;
            this.hasSwitchedCarouselImage = true;
            this.updateButtonStates(null);
            if (this.children.contains(this.realmSelectionList) && !this.children.remove(lv2 = this.realmSelectionList)) {
                LOGGER.error("Unable to remove widget: " + lv2);
            }
            Realms.narrateNow(lv.getString());
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.7f);
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(DARKEN);
        boolean m = false;
        int n = 32;
        DrawableHelper.drawTexture(arg, 0, 32, 0.0f, 0.0f, this.width, this.height - 40 - 32, 310, 166);
        RenderSystem.disableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(POPUP);
        DrawableHelper.drawTexture(arg, k, l, 0.0f, 0.0f, 310, 166, 310, 166);
        if (!IMAGES.isEmpty()) {
            this.client.getTextureManager().bindTexture(IMAGES.get(this.carouselIndex));
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(arg, k + 7, l + 7, 0.0f, 0.0f, 195, 152, 195, 152);
            if (this.carouselTick % 95 < 5) {
                if (!this.hasSwitchedCarouselImage) {
                    this.carouselIndex = (this.carouselIndex + 1) % IMAGES.size();
                    this.hasSwitchedCarouselImage = true;
                }
            } else {
                this.hasSwitchedCarouselImage = false;
            }
        }
        int o = 0;
        for (class_5348 lv3 : list) {
            int p = l + 10 * ++o - 3;
            this.textRenderer.draw(arg, lv3, (float)(this.width / 2 + 52), (float)p, 0x4C4C4C);
        }
    }

    private int popupX0() {
        return (this.width - 310) / 2;
    }

    private int popupY0() {
        return this.height / 2 - 80;
    }

    private void drawInvitationPendingIcon(MatrixStack arg, int i, int j, int k, int l, boolean bl, boolean bl2) {
        boolean bl7;
        boolean bl6;
        boolean bl4;
        int m = this.numberOfPendingInvites;
        boolean bl3 = this.inPendingInvitationArea(i, j);
        boolean bl5 = bl4 = bl2 && bl;
        if (bl4) {
            float f = 0.25f + (1.0f + MathHelper.sin((float)this.animTick * 0.5f)) * 0.25f;
            int n = 0xFF000000 | (int)(f * 64.0f) << 16 | (int)(f * 64.0f) << 8 | (int)(f * 64.0f) << 0;
            this.fillGradient(arg, k - 2, l - 2, k + 18, l + 18, n, n);
            n = 0xFF000000 | (int)(f * 255.0f) << 16 | (int)(f * 255.0f) << 8 | (int)(f * 255.0f) << 0;
            this.fillGradient(arg, k - 2, l - 2, k + 18, l - 1, n, n);
            this.fillGradient(arg, k - 2, l - 2, k - 1, l + 18, n, n);
            this.fillGradient(arg, k + 17, l - 2, k + 18, l + 18, n, n);
            this.fillGradient(arg, k - 2, l + 17, k + 18, l + 18, n, n);
        }
        this.client.getTextureManager().bindTexture(INVITE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        boolean bl52 = bl2 && bl;
        float g = bl52 ? 16.0f : 0.0f;
        DrawableHelper.drawTexture(arg, k, l - 6, g, 0.0f, 15, 25, 31, 25);
        boolean bl8 = bl6 = bl2 && m != 0;
        if (bl6) {
            int o = (Math.min(m, 6) - 1) * 8;
            int p = (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.client.getTextureManager().bindTexture(INVITATION_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float h = bl3 ? 8.0f : 0.0f;
            DrawableHelper.drawTexture(arg, k + 4, l + 4 + p, o, h, 8, 8, 48, 16);
        }
        int q = i + 12;
        int r = j;
        boolean bl9 = bl7 = bl2 && bl3;
        if (bl7) {
            String string = m == 0 ? "mco.invites.nopending" : "mco.invites.pending";
            String string2 = I18n.translate(string, new Object[0]);
            int s = this.textRenderer.getWidth(string2);
            this.fillGradient(arg, q - 3, r - 3, q + s + 3, r + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(arg, string2, (float)q, (float)r, -1);
        }
    }

    private boolean inPendingInvitationArea(double d, double e) {
        int i = this.width / 2 + 50;
        int j = this.width / 2 + 66;
        int k = 11;
        int l = 23;
        if (this.numberOfPendingInvites != 0) {
            i -= 3;
            j += 3;
            k -= 5;
            l += 5;
        }
        return (double)i <= d && d <= (double)j && (double)k <= e && e <= (double)l;
    }

    public void play(RealmsServer arg, Screen arg2) {
        if (arg != null) {
            try {
                if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                if (this.connectLock.getHoldCount() > 1) {
                    return;
                }
            }
            catch (InterruptedException interruptedException) {
                return;
            }
            this.dontSetConnectedToRealms = true;
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(arg2, new RealmsGetServerDetailsTask(this, arg2, arg, this.connectLock)));
        }
    }

    private boolean isSelfOwnedServer(RealmsServer arg) {
        return arg.ownerUUID != null && arg.ownerUUID.equals(this.client.getSession().getUuid());
    }

    private boolean method_25001(RealmsServer arg) {
        return this.isSelfOwnedServer(arg) && !arg.expired;
    }

    private void drawExpired(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(EXPIRED_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.expired"));
        }
    }

    private void method_24987(MatrixStack arg, int i, int j, int k, int l, int m) {
        this.client.getTextureManager().bindTexture(EXPIRES_SOON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(arg, i, j, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            if (m <= 0) {
                this.method_27452(new TranslatableText("mco.selectServer.expires.soon"));
            } else if (m == 1) {
                this.method_27452(new TranslatableText("mco.selectServer.expires.day"));
            } else {
                this.method_27452(new TranslatableText("mco.selectServer.expires.days", m));
            }
        }
    }

    private void drawOpen(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(ON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.open"));
        }
    }

    private void drawClose(MatrixStack arg, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(OFF_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.closed"));
        }
    }

    private void drawLeave(MatrixStack arg, int i, int j, int k, int l) {
        boolean bl = false;
        if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.client.getTextureManager().bindTexture(LEAVE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(arg, i, j, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.method_27452(new TranslatableText("mco.selectServer.leave"));
            this.field_24198 = class_5220.LEAVE;
        }
    }

    private void drawConfigure(MatrixStack arg, int i, int j, int k, int l) {
        boolean bl = false;
        if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.client.getTextureManager().bindTexture(CONFIGURE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(arg, i, j, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.method_27452(new TranslatableText("mco.selectServer.configure"));
            this.field_24198 = class_5220.CONFIGURE;
        }
    }

    protected void renderMousehoverTooltip(MatrixStack arg, List<Text> list, int i, int j) {
        if (list.isEmpty()) {
            return;
        }
        int k = 0;
        int l = 0;
        for (Text lv : list) {
            int m = this.textRenderer.getWidth(lv);
            if (m <= l) continue;
            l = m;
        }
        int n = i - l - 5;
        int o = j;
        if (n < 0) {
            n = i + 12;
        }
        for (Text lv2 : list) {
            int p = o - (k == 0 ? 3 : 0) + k;
            this.fillGradient(arg, n - 3, p, n + l + 3, o + 8 + 3 + k, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(arg, lv2, (float)n, (float)(o + k), 0xFFFFFF);
            k += 10;
        }
    }

    private void renderMoreInfo(MatrixStack arg, int i, int j, int k, int l, boolean bl) {
        boolean bl2 = false;
        if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
            bl2 = true;
        }
        this.client.getTextureManager().bindTexture(QUESTIONMARK);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(arg, k, l, f, 0.0f, 20, 20, 40, 20);
        if (bl2) {
            this.method_27452(new TranslatableText("mco.selectServer.info"));
        }
    }

    private void renderNews(MatrixStack arg, int i, int j, boolean bl, int k, int l, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
            bl4 = true;
        }
        this.client.getTextureManager().bindTexture(NEWS_ICON);
        if (bl3) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean bl5 = bl3 && bl2;
        float f = bl5 ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(arg, k, l, f, 0.0f, 20, 20, 40, 20);
        if (bl4 && bl3) {
            this.method_27452(new TranslatableText("mco.news"));
        }
        if (bl && bl3) {
            int m = bl4 ? 0 : (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.client.getTextureManager().bindTexture(INVITATION_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(arg, k + 10, l + 2 + m, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(MatrixStack arg) {
        String string = "LOCAL!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(arg, "LOCAL!", 0.0f, 0.0f, 0x7FFF7F);
        RenderSystem.popMatrix();
    }

    private void renderStage(MatrixStack arg) {
        String string = "STAGE!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(arg, "STAGE!", 0.0f, 0.0f, -256);
        RenderSystem.popMatrix();
    }

    public RealmsMainScreen newScreen() {
        return new RealmsMainScreen(this.lastScreen);
    }

    public static void method_23765(ResourceManager arg2) {
        Collection<Identifier> collection = arg2.findResources("textures/gui/images", string -> string.endsWith(".png"));
        IMAGES = (List)collection.stream().filter(arg -> arg.getNamespace().equals("realms")).collect(ImmutableList.toImmutableList());
    }

    private void method_27452(Text ... args) {
        this.toolTip = Arrays.asList(args);
    }

    private void method_24985(ButtonWidget arg) {
        this.client.openScreen(new RealmsPendingInvitesScreen(this.lastScreen));
    }

    static {
        lastScrollYPosition = -1;
    }

    @Environment(value=EnvType.CLIENT)
    class CloseButton
    extends ButtonWidget {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslatableText("mco.selectServer.close"), arg2 -> RealmsMainScreen.this.onClosePopup());
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            RealmsMainScreen.this.client.getTextureManager().bindTexture(CROSS_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float g = this.isHovered() ? 12.0f : 0.0f;
            CloseButton.drawTexture(arg, this.x, this.y, 0.0f, g, 12, 12, 12, 24);
            if (this.isMouseOver(i, j)) {
                RealmsMainScreen.this.method_27452(new Text[]{this.getMessage()});
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ShowPopupButton
    extends ButtonWidget {
        public ShowPopupButton() {
            super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslatableText("mco.selectServer.info"), arg2 -> RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser);
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            RealmsMainScreen.this.renderMoreInfo(arg, i, j, this.x, this.y, this.isHovered());
        }
    }

    @Environment(value=EnvType.CLIENT)
    class NewsButton
    extends ButtonWidget {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 62, 6, 20, 20, LiteralText.EMPTY, arg2 -> {
                if (RealmsMainScreen.this.newsLink == null) {
                    return;
                }
                Util.getOperatingSystem().open(RealmsMainScreen.this.newsLink);
                if (RealmsMainScreen.this.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData lv = RealmsPersistence.readFile();
                    lv.hasUnreadNews = false;
                    RealmsMainScreen.this.hasUnreadNews = false;
                    RealmsPersistence.writeFile(lv);
                }
            });
            this.setMessage(new TranslatableText("mco.news"));
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            RealmsMainScreen.this.renderNews(arg, i, j, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitesButton
    extends ButtonWidget
    implements TickableElement {
        public PendingInvitesButton() {
            super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, LiteralText.EMPTY, arg2 -> RealmsMainScreen.this.method_24985(arg2));
        }

        @Override
        public void tick() {
            this.setMessage(new TranslatableText(RealmsMainScreen.this.numberOfPendingInvites == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            RealmsMainScreen.this.drawInvitationPendingIcon(arg, i, j, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListEntry
    extends Entry {
        private final RealmsServer mServerData;

        public RealmSelectionListEntry(RealmsServer arg2) {
            this.mServerData = arg2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.method_20945(this.mServerData, arg, k, j, n, o);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                RealmsMainScreen.this.client.openScreen(new RealmsCreateRealmScreen(this.mServerData, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = this.mServerData.id;
            }
            return true;
        }

        private void method_20945(RealmsServer arg, MatrixStack arg2, int i, int j, int k, int l) {
            this.renderMcoServerItem(arg, arg2, i + 36, j, k, l);
        }

        private void renderMcoServerItem(RealmsServer arg, MatrixStack arg2, int i, int j, int k, int l) {
            if (arg.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.client.getTextureManager().bindTexture(WORLD_ICON);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableAlphaTest();
                DrawableHelper.drawTexture(arg2, i + 10, j + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float f = 0.5f + (1.0f + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int m = 0xFF000000 | (int)(127.0f * f) << 16 | (int)(255.0f * f) << 8 | (int)(127.0f * f);
                RealmsMainScreen.this.drawCenteredString(arg2, RealmsMainScreen.this.textRenderer, I18n.translate("mco.selectServer.uninitialized", new Object[0]), i + 10 + 40 + 75, j + 12, m);
                return;
            }
            int n = 225;
            int o = 2;
            if (arg.expired) {
                RealmsMainScreen.this.drawExpired(arg2, i + 225 - 14, j + 2, k, l);
            } else if (arg.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(arg2, i + 225 - 14, j + 2, k, l);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(arg) && arg.daysLeft < 7) {
                RealmsMainScreen.this.method_24987(arg2, i + 225 - 14, j + 2, k, l, arg.daysLeft);
            } else if (arg.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(arg2, i + 225 - 14, j + 2, k, l);
            }
            if (!RealmsMainScreen.this.isSelfOwnedServer(arg) && !overrideConfigure) {
                RealmsMainScreen.this.drawLeave(arg2, i + 225, j + 2, k, l);
            } else {
                RealmsMainScreen.this.drawConfigure(arg2, i + 225, j + 2, k, l);
            }
            if (!"0".equals(arg.serverPing.nrOfPlayers)) {
                String string = (Object)((Object)Formatting.GRAY) + "" + arg.serverPing.nrOfPlayers;
                RealmsMainScreen.this.textRenderer.draw(arg2, string, (float)(i + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)), (float)(j + 3), 0x808080);
                if (k >= i + 207 - RealmsMainScreen.this.textRenderer.getWidth(string) && k <= i + 207 && l >= j + 1 && l <= j + 10 && l < RealmsMainScreen.this.height - 40 && l > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.method_27452(new Text[]{new LiteralText(arg.serverPing.playerList)});
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(arg) && arg.expired) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableBlend();
                RealmsMainScreen.this.client.getTextureManager().bindTexture(WIDGETS);
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                String string2 = I18n.translate("mco.selectServer.expiredList", new Object[0]);
                String string3 = I18n.translate("mco.selectServer.expiredRenew", new Object[0]);
                if (arg.expiredTrial) {
                    string2 = I18n.translate("mco.selectServer.expiredTrial", new Object[0]);
                    string3 = I18n.translate("mco.selectServer.expiredSubscribe", new Object[0]);
                }
                int p = RealmsMainScreen.this.textRenderer.getWidth(string3) + 17;
                int q = 16;
                int r = i + RealmsMainScreen.this.textRenderer.getWidth(string2) + 8;
                int s = j + 13;
                boolean bl = false;
                if (k >= r && k < r + p && l > s && l <= s + 16 & l < RealmsMainScreen.this.height - 40 && l > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    bl = true;
                    RealmsMainScreen.this.field_24198 = class_5220.EXPIRED;
                }
                int t = bl ? 2 : 1;
                DrawableHelper.drawTexture(arg2, r, s, 0.0f, 46 + t * 20, p / 2, 8, 256, 256);
                DrawableHelper.drawTexture(arg2, r + p / 2, s, 200 - p / 2, 46 + t * 20, p / 2, 8, 256, 256);
                DrawableHelper.drawTexture(arg2, r, s + 8, 0.0f, 46 + t * 20 + 12, p / 2, 8, 256, 256);
                DrawableHelper.drawTexture(arg2, r + p / 2, s + 8, 200 - p / 2, 46 + t * 20 + 12, p / 2, 8, 256, 256);
                RenderSystem.disableBlend();
                int u = j + 11 + 5;
                int v = bl ? 0xFFFFA0 : 0xFFFFFF;
                RealmsMainScreen.this.textRenderer.draw(arg2, string2, (float)(i + 2), (float)(u + 1), 15553363);
                RealmsMainScreen.this.drawCenteredString(arg2, RealmsMainScreen.this.textRenderer, string3, r + p / 2, u + 1, v);
            } else {
                if (arg.worldType == RealmsServer.WorldType.MINIGAME) {
                    int w = 0xCCAC5C;
                    String string4 = I18n.translate("mco.selectServer.minigame", new Object[0]) + " ";
                    int x = RealmsMainScreen.this.textRenderer.getWidth(string4);
                    RealmsMainScreen.this.textRenderer.draw(arg2, string4, (float)(i + 2), (float)(j + 12), 0xCCAC5C);
                    RealmsMainScreen.this.textRenderer.draw(arg2, arg.getMinigameName(), (float)(i + 2 + x), (float)(j + 12), 0x6C6C6C);
                } else {
                    RealmsMainScreen.this.textRenderer.draw(arg2, arg.getDescription(), (float)(i + 2), (float)(j + 12), 0x6C6C6C);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(arg)) {
                    RealmsMainScreen.this.textRenderer.draw(arg2, arg.owner, (float)(i + 2), (float)(j + 12 + 11), 0x4C4C4C);
                }
            }
            RealmsMainScreen.this.textRenderer.draw(arg2, arg.getName(), (float)(i + 2), (float)(j + 1), 0xFFFFFF);
            RealmsTextureManager.withBoundFace(arg.ownerUUID, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(arg2, i - 36, j, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                DrawableHelper.drawTexture(arg2, i - 36, j, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListTrialEntry
    extends Entry {
        private RealmSelectionListTrialEntry() {
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.renderTrialItem(arg, i, k, j, n, o);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            RealmsMainScreen.this.popupOpenedByUser = true;
            return true;
        }

        private void renderTrialItem(MatrixStack arg, int i, int j, int k, int l, int m) {
            int n = k + 8;
            int o = 0;
            String string = I18n.translate("mco.trial.message.line1", new Object[0]) + "\\n" + I18n.translate("mco.trial.message.line2", new Object[0]);
            boolean bl = false;
            if (j <= l && l <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && k <= m && m <= k + 32) {
                bl = true;
            }
            int p = 0x7FFF7F;
            if (bl && !RealmsMainScreen.this.shouldShowPopup()) {
                p = 6077788;
            }
            for (String string2 : string.split("\\\\n")) {
                RealmsMainScreen.this.drawCenteredString(arg, RealmsMainScreen.this.textRenderer, string2, RealmsMainScreen.this.width / 2, n + o, p);
                o += 10;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private Entry() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
        }

        @Override
        public boolean isFocused() {
            return RealmsMainScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (i == 257 || i == 32 || i == 335) {
                AlwaysSelectedEntryListWidget.Entry lv = (AlwaysSelectedEntryListWidget.Entry)this.getSelected();
                if (lv == null) {
                    return super.keyPressed(i, j, k);
                }
                return lv.mouseClicked(0.0, 0.0, 0);
            }
            return super.keyPressed(i, j, k);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (i == 0 && d < (double)this.getScrollbarPositionX() && e >= (double)this.top && e <= (double)this.bottom) {
                int j = RealmsMainScreen.this.realmSelectionList.getRowLeft();
                int k = this.getScrollbarPositionX();
                int l = (int)Math.floor(e - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int m = l / this.itemHeight;
                if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                    this.itemClicked(l, m, d, e, this.width);
                    RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
                    this.setSelected(m);
                }
                return true;
            }
            return super.mouseClicked(d, e, i);
        }

        @Override
        public void setSelected(int i) {
            RealmsServer lv3;
            this.setSelectedItem(i);
            if (i == -1) {
                return;
            }
            if (RealmsMainScreen.this.shouldShowMessageInList()) {
                if (i == 0) {
                    Realms.narrateNow(I18n.translate("mco.trial.message.line1", new Object[0]), I18n.translate("mco.trial.message.line2", new Object[0]));
                    Object lv = null;
                } else {
                    if (i - 1 >= RealmsMainScreen.this.realmsServers.size()) {
                        RealmsMainScreen.this.selectedServerId = -1L;
                        return;
                    }
                    RealmsServer lv2 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(i - 1);
                }
            } else {
                if (i >= RealmsMainScreen.this.realmsServers.size()) {
                    RealmsMainScreen.this.selectedServerId = -1L;
                    return;
                }
                lv3 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(i);
            }
            RealmsMainScreen.this.updateButtonStates(lv3);
            if (lv3 == null) {
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            if (lv3.state == RealmsServer.State.UNINITIALIZED) {
                Realms.narrateNow(I18n.translate("mco.selectServer.uninitialized", new Object[0]) + I18n.translate("mco.gui.button", new Object[0]));
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            RealmsMainScreen.this.selectedServerId = lv3.id;
            if (RealmsMainScreen.this.clicks >= 10 && ((RealmsMainScreen)RealmsMainScreen.this).playButton.active) {
                RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
            }
            Realms.narrateNow(I18n.translate("narrator.select", lv3.name));
        }

        @Override
        public void setSelected(@Nullable Entry arg) {
            super.setSelected(arg);
            RealmsServer lv = (RealmsServer)RealmsMainScreen.this.realmsServers.get(this.children().indexOf(arg) - (RealmsMainScreen.this.shouldShowMessageInList() ? 1 : 0));
            RealmsMainScreen.this.selectedServerId = lv.id;
            RealmsMainScreen.this.updateButtonStates(lv);
        }

        @Override
        public void itemClicked(int i, int j, double d, double e, int k) {
            if (RealmsMainScreen.this.shouldShowMessageInList()) {
                if (j == 0) {
                    RealmsMainScreen.this.popupOpenedByUser = true;
                    return;
                }
                --j;
            }
            if (j >= RealmsMainScreen.this.realmsServers.size()) {
                return;
            }
            RealmsServer lv = (RealmsServer)RealmsMainScreen.this.realmsServers.get(j);
            if (lv == null) {
                return;
            }
            if (lv.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                MinecraftClient.getInstance().openScreen(new RealmsCreateRealmScreen(lv, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = lv.id;
            }
            if (RealmsMainScreen.this.field_24198 == class_5220.CONFIGURE) {
                RealmsMainScreen.this.selectedServerId = lv.id;
                RealmsMainScreen.this.configureClicked(lv);
            } else if (RealmsMainScreen.this.field_24198 == class_5220.LEAVE) {
                RealmsMainScreen.this.selectedServerId = lv.id;
                RealmsMainScreen.this.leaveClicked(lv);
            } else if (RealmsMainScreen.this.field_24198 == class_5220.EXPIRED) {
                RealmsMainScreen.this.onRenew();
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum class_5220 {
        NONE,
        EXPIRED,
        LEAVE,
        CONFIGURE;

    }
}

