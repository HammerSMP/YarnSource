/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsGetServerDetailsTask;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen parent;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;
    private final String realmsToSUrl = "https://minecraft.net/realms/terms";

    public RealmsTermsScreen(Screen parent, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
        this.parent = parent;
        this.mainScreen = mainScreen;
        this.realmsServer = realmsServer;
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        int i = this.width / 4 - 2;
        this.addButton(new ButtonWidget(this.width / 4, RealmsTermsScreen.row(12), i, 20, new TranslatableText("mco.terms.buttons.agree"), arg -> this.agreedToTos()));
        this.addButton(new ButtonWidget(this.width / 2 + 4, RealmsTermsScreen.row(12), i, 20, new TranslatableText("mco.terms.buttons.disagree"), arg -> this.client.openScreen(this.parent)));
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

    private void agreedToTos() {
        RealmsClient lv = RealmsClient.createRealmsClient();
        try {
            lv.agreeToTos();
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new RealmsGetServerDetailsTask(this.mainScreen, this.parent, this.realmsServer, new ReentrantLock())));
        }
        catch (RealmsServiceException lv2) {
            LOGGER.error("Couldn't agree to TOS");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.onLink) {
            this.client.keyboard.setClipboard("https://minecraft.net/realms/terms");
            Util.getOperatingSystem().open("https://minecraft.net/realms/terms");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("mco.terms.title", new Object[0]), this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, I18n.translate("mco.terms.sentence.1", new Object[0]), (float)(this.width / 2 - 120), (float)RealmsTermsScreen.row(5), 0xFFFFFF);
        int k = this.textRenderer.getWidth(I18n.translate("mco.terms.sentence.1", new Object[0]));
        int l = this.width / 2 - 121 + k;
        int m = RealmsTermsScreen.row(5);
        int n = l + this.textRenderer.getWidth("mco.terms.sentence.2") + 1;
        this.textRenderer.getClass();
        int o = m + 1 + 9;
        if (l <= mouseX && mouseX <= n && m <= mouseY && mouseY <= o) {
            this.onLink = true;
            this.textRenderer.draw(matrices, " " + I18n.translate("mco.terms.sentence.2", new Object[0]), (float)(this.width / 2 - 120 + k), (float)RealmsTermsScreen.row(5), 7107012);
        } else {
            this.onLink = false;
            this.textRenderer.draw(matrices, " " + I18n.translate("mco.terms.sentence.2", new Object[0]), (float)(this.width / 2 - 120 + k), (float)RealmsTermsScreen.row(5), 0x3366BB);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

