/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DirectConnectScreen
extends Screen {
    private ButtonWidget selectServerButton;
    private final ServerInfo serverEntry;
    private TextFieldWidget addressField;
    private final BooleanConsumer callback;
    private final Screen parent;

    public DirectConnectScreen(Screen arg, BooleanConsumer booleanConsumer, ServerInfo arg2) {
        super(new TranslatableText("selectServer.direct"));
        this.parent = arg;
        this.serverEntry = arg2;
        this.callback = booleanConsumer;
    }

    @Override
    public void tick() {
        this.addressField.tick();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.getFocused() == this.addressField && (i == 257 || i == 335)) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.selectServerButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, new TranslatableText("selectServer.select"), arg -> this.saveAndClose()));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, arg -> this.callback.accept(false)));
        this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, new TranslatableText("addServer.enterIp"));
        this.addressField.setMaxLength(128);
        this.addressField.setSelected(true);
        this.addressField.setText(this.client.options.lastServer);
        this.addressField.setChangedListener(string -> this.onAddressFieldChanged());
        this.children.add(this.addressField);
        this.setInitialFocus(this.addressField);
        this.onAddressFieldChanged();
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.addressField.getText();
        this.init(arg, i, j);
        this.addressField.setText(string);
    }

    private void saveAndClose() {
        this.serverEntry.address = this.addressField.getText();
        this.callback.accept(true);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        this.client.options.lastServer = this.addressField.getText();
        this.client.options.write();
    }

    private void onAddressFieldChanged() {
        String string = this.addressField.getText();
        this.selectServerButton.active = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        this.drawString(arg, this.textRenderer, I18n.translate("addServer.enterIp", new Object[0]), this.width / 2 - 100, 100, 0xA0A0A0);
        this.addressField.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }
}

