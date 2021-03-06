/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.WorldCreationTask;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsCreateRealmScreen
extends RealmsScreen {
    private final RealmsServer server;
    private final RealmsMainScreen parent;
    private TextFieldWidget nameBox;
    private TextFieldWidget descriptionBox;
    private ButtonWidget createButton;
    private RealmsLabel createRealmLabel;

    public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen parent) {
        this.server = server;
        this.parent = parent;
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
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
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
    public boolean charTyped(char chr, int keyCode) {
        boolean bl = super.charTyped(chr, keyCode);
        this.createButton.active = this.valid();
        return bl;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
        this.createButton.active = this.valid();
        return bl;
    }

    private void createWorld() {
        if (this.valid()) {
            RealmsResetWorldScreen lv = new RealmsResetWorldScreen(this.parent, this.server, new TranslatableText("mco.selectServer.create"), new TranslatableText("mco.create.world.subtitle"), 0xA0A0A0, new TranslatableText("mco.create.world.skip"), () -> this.client.openScreen(this.parent.newScreen()), () -> this.client.openScreen(this.parent.newScreen()));
            lv.setResetTitle(I18n.translate("mco.create.world.reset.title", new Object[0]));
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), lv)));
        }
    }

    private boolean valid() {
        return !this.nameBox.getText().trim().isEmpty();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.createRealmLabel.render(this, matrices);
        this.textRenderer.draw(matrices, I18n.translate("mco.configure.world.name", new Object[0]), (float)(this.width / 2 - 100), 52.0f, 0xA0A0A0);
        this.textRenderer.draw(matrices, I18n.translate("mco.configure.world.description", new Object[0]), (float)(this.width / 2 - 100), 102.0f, 0xA0A0A0);
        if (this.nameBox != null) {
            this.nameBox.render(matrices, mouseX, mouseY, delta);
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.render(matrices, mouseX, mouseY, delta);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

