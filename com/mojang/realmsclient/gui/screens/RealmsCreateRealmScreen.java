/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.WorldCreationTask;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsCreateRealmScreen
extends RealmsScreen {
    private final RealmsServer server;
    private final RealmsMainScreen lastScreen;
    private TextFieldWidget nameBox;
    private TextFieldWidget descriptionBox;
    private ButtonWidget createButton;
    private RealmsLabel createRealmLabel;

    public RealmsCreateRealmScreen(RealmsServer arg, RealmsMainScreen arg2) {
        this.server = arg;
        this.lastScreen = arg2;
    }

    @Override
    public void tick() {
        if (this.nameBox != null) {
            this.nameBox.tick();
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.tick();
        }
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.createButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, new TranslatableText("mco.create.world"), arg -> this.createWorld()));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.lastScreen)));
        this.createButton.active = false;
        this.nameBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, 65, 200, 20, null, new TranslatableText("mco.configure.world.name"));
        this.addChild(this.nameBox);
        this.setInitialFocus(this.nameBox);
        this.descriptionBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, 115, 200, 20, null, new TranslatableText("mco.configure.world.description"));
        this.addChild(this.descriptionBox);
        this.createRealmLabel = new RealmsLabel(new TranslatableText("mco.selectServer.create"), this.width / 2, 11, 0xFFFFFF);
        this.addChild(this.createRealmLabel);
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean charTyped(char c, int i) {
        boolean bl = super.charTyped(c, i);
        this.createButton.active = this.valid();
        return bl;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.lastScreen);
            return true;
        }
        boolean bl = super.keyPressed(i, j, k);
        this.createButton.active = this.valid();
        return bl;
    }

    private void createWorld() {
        if (this.valid()) {
            RealmsResetWorldScreen lv = new RealmsResetWorldScreen(this.lastScreen, this.server, new TranslatableText("mco.selectServer.create"), new TranslatableText("mco.create.world.subtitle"), 0xA0A0A0, new TranslatableText("mco.create.world.skip"), () -> this.client.openScreen(this.lastScreen.newScreen()), () -> this.client.openScreen(this.lastScreen.newScreen()));
            lv.setResetTitle(I18n.translate("mco.create.world.reset.title", new Object[0]));
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), lv)));
        }
    }

    private boolean valid() {
        return !this.nameBox.getText().trim().isEmpty();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.createRealmLabel.render(this, arg);
        this.textRenderer.draw(arg, I18n.translate("mco.configure.world.name", new Object[0]), (float)(this.width / 2 - 100), 52.0f, 0xA0A0A0);
        this.textRenderer.draw(arg, I18n.translate("mco.configure.world.description", new Object[0]), (float)(this.width / 2 - 100), 102.0f, 0xA0A0A0);
        if (this.nameBox != null) {
            this.nameBox.render(arg, i, j, f);
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.render(arg, i, j, f);
        }
        super.render(arg, i, j, f);
    }
}

