/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSubscriptionInfoScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private final Screen mainScreen;
    private final String subscriptionTitle;
    private final String subscriptionStartLabelText;
    private final String timeLeftLabelText;
    private final String daysLeftLabelText;
    private int daysLeft;
    private String startDate;
    private Subscription.SubscriptionType type;

    public RealmsSubscriptionInfoScreen(Screen arg, RealmsServer arg2, Screen arg3) {
        this.lastScreen = arg;
        this.serverData = arg2;
        this.mainScreen = arg3;
        this.subscriptionTitle = I18n.translate("mco.configure.world.subscription.title", new Object[0]);
        this.subscriptionStartLabelText = I18n.translate("mco.configure.world.subscription.start", new Object[0]);
        this.timeLeftLabelText = I18n.translate("mco.configure.world.subscription.timeleft", new Object[0]);
        this.daysLeftLabelText = I18n.translate("mco.configure.world.subscription.recurring.daysleft", new Object[0]);
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        Realms.narrateNow(this.subscriptionTitle, this.subscriptionStartLabelText, this.startDate, this.timeLeftLabelText, this.daysLeftPresentation(this.daysLeft));
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(6), 200, 20, new TranslatableText("mco.configure.world.subscription.extend"), arg -> {
            String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + this.serverData.remoteSubscriptionId + "&profileId=" + this.client.getSession().getUuid();
            this.client.keyboard.setClipboard(string);
            Util.getOperatingSystem().open(string);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(12), 200, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.lastScreen)));
        if (this.serverData.expired) {
            this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(10), 200, 20, new TranslatableText("mco.configure.world.delete.button"), arg -> {
                TranslatableText lv = new TranslatableText("mco.configure.world.delete.question.line1");
                TranslatableText lv2 = new TranslatableText("mco.configure.world.delete.question.line2");
                this.client.openScreen(new RealmsLongConfirmationScreen(this::method_25271, RealmsLongConfirmationScreen.Type.Warning, lv, lv2, true));
            }));
        }
    }

    private void method_25271(boolean bl) {
        if (bl) {
            new Thread("Realms-delete-realm"){

                @Override
                public void run() {
                    try {
                        RealmsClient lv = RealmsClient.createRealmsClient();
                        lv.deleteWorld(((RealmsSubscriptionInfoScreen)RealmsSubscriptionInfoScreen.this).serverData.id);
                    }
                    catch (RealmsServiceException lv2) {
                        LOGGER.error("Couldn't delete world");
                        LOGGER.error((Object)lv2);
                    }
                    RealmsSubscriptionInfoScreen.this.client.execute(() -> RealmsSubscriptionInfoScreen.this.client.openScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
                }
            }.start();
        }
        this.client.openScreen(this);
    }

    private void getSubscription(long l) {
        RealmsClient lv = RealmsClient.createRealmsClient();
        try {
            Subscription lv2 = lv.subscriptionFor(l);
            this.daysLeft = lv2.daysLeft;
            this.startDate = this.localPresentation(lv2.startDate);
            this.type = lv2.type;
        }
        catch (RealmsServiceException lv3) {
            LOGGER.error("Couldn't get subscription");
            this.client.openScreen(new RealmsGenericErrorScreen(lv3, this.lastScreen));
        }
    }

    private String localPresentation(long l) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(l);
        return DateFormat.getDateTimeInstance().format(calendar.getTime());
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
        int k = this.width / 2 - 100;
        this.drawCenteredString(arg, this.textRenderer, this.subscriptionTitle, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(arg, this.subscriptionStartLabelText, (float)k, (float)RealmsSubscriptionInfoScreen.row(0), 0xA0A0A0);
        this.textRenderer.draw(arg, this.startDate, (float)k, (float)RealmsSubscriptionInfoScreen.row(1), 0xFFFFFF);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.textRenderer.draw(arg, this.timeLeftLabelText, (float)k, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.textRenderer.draw(arg, this.daysLeftLabelText, (float)k, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        }
        this.textRenderer.draw(arg, this.daysLeftPresentation(this.daysLeft), (float)k, (float)RealmsSubscriptionInfoScreen.row(4), 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    private String daysLeftPresentation(int i) {
        if (i == -1 && this.serverData.expired) {
            return I18n.translate("mco.configure.world.subscription.expired", new Object[0]);
        }
        if (i <= 1) {
            return I18n.translate("mco.configure.world.subscription.less_than_a_day", new Object[0]);
        }
        int j = i / 30;
        int k = i % 30;
        StringBuilder stringBuilder = new StringBuilder();
        if (j > 0) {
            stringBuilder.append(j).append(" ");
            if (j == 1) {
                stringBuilder.append(I18n.translate("mco.configure.world.subscription.month", new Object[0]).toLowerCase(Locale.ROOT));
            } else {
                stringBuilder.append(I18n.translate("mco.configure.world.subscription.months", new Object[0]).toLowerCase(Locale.ROOT));
            }
        }
        if (k > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(k).append(" ");
            if (k == 1) {
                stringBuilder.append(I18n.translate("mco.configure.world.subscription.day", new Object[0]).toLowerCase(Locale.ROOT));
            } else {
                stringBuilder.append(I18n.translate("mco.configure.world.subscription.days", new Object[0]).toLowerCase(Locale.ROOT));
            }
        }
        return stringBuilder.toString();
    }
}

