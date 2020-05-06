/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsSettingsScreen
extends RealmsScreen {
    private final RealmsConfigureWorldScreen configureWorldScreen;
    private final RealmsServer serverData;
    private ButtonWidget doneButton;
    private TextFieldWidget descEdit;
    private TextFieldWidget nameEdit;
    private RealmsLabel titleLabel;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen arg, RealmsServer arg2) {
        this.configureWorldScreen = arg;
        this.serverData = arg2;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active = !this.nameEdit.getText().trim().isEmpty();
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        int i = this.width / 2 - 106;
        this.doneButton = this.addButton(new ButtonWidget(i - 2, RealmsSettingsScreen.row(12), 106, 20, new TranslatableText("mco.configure.world.buttons.done"), arg -> this.save()));
        this.addButton(new ButtonWidget(this.width / 2 + 2, RealmsSettingsScreen.row(12), 106, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.configureWorldScreen)));
        String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        ButtonWidget lv = new ButtonWidget(this.width / 2 - 53, RealmsSettingsScreen.row(0), 106, 20, new TranslatableText(string), arg -> {
            if (this.serverData.state == RealmsServer.State.OPEN) {
                TranslatableText lv = new TranslatableText("mco.configure.world.close.question.line1");
                TranslatableText lv2 = new TranslatableText("mco.configure.world.close.question.line2");
                this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                    if (bl) {
                        this.configureWorldScreen.closeTheWorld(this);
                    } else {
                        this.client.openScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, lv, lv2, true));
            } else {
                this.configureWorldScreen.openTheWorld(false, this);
            }
        });
        this.addButton(lv);
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, i, RealmsSettingsScreen.row(4), 212, 20, null, new TranslatableText("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        this.nameEdit.setText(this.serverData.getName());
        this.addChild(this.nameEdit);
        this.focusOn(this.nameEdit);
        this.descEdit = new TextFieldWidget(this.client.textRenderer, i, RealmsSettingsScreen.row(8), 212, 20, null, new TranslatableText("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        this.descEdit.setText(this.serverData.getDescription());
        this.addChild(this.descEdit);
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.settings.title"), this.width / 2, 17, 0xFFFFFF));
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.configureWorldScreen);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.titleLabel.render(this, arg);
        this.textRenderer.draw(arg, I18n.translate("mco.configure.world.name", new Object[0]), (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(3), 0xA0A0A0);
        this.textRenderer.draw(arg, I18n.translate("mco.configure.world.description", new Object[0]), (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(7), 0xA0A0A0);
        this.nameEdit.render(arg, i, j, f);
        this.descEdit.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }

    public void save() {
        this.configureWorldScreen.saveSettings(this.nameEdit.getText(), this.descEdit.getText());
    }
}

