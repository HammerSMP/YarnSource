/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen
extends Screen {
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected ButtonWidget toggleTrackingOutputButton;
    protected boolean trackingOutput;
    private CommandSuggestor commandSuggestor;

    public AbstractCommandBlockScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public void tick() {
        this.consoleCommandTextField.tick();
    }

    abstract CommandBlockExecutor getCommandExecutor();

    abstract int getTrackOutputButtonHeight();

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.DONE, arg -> this.commitAndClose()));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, ScreenTexts.CANCEL, arg -> this.onClose()));
        this.toggleTrackingOutputButton = this.addButton(new ButtonWidget(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, new LiteralText("O"), arg -> {
            CommandBlockExecutor lv;
            lv.shouldTrackOutput(!(lv = this.getCommandExecutor()).isTrackingOutput());
            this.updateTrackedOutput();
        }));
        this.consoleCommandTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, (Text)new TranslatableText("advMode.command")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(AbstractCommandBlockScreen.this.commandSuggestor.method_23958());
            }
        };
        this.consoleCommandTextField.setMaxLength(32500);
        this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
        this.children.add(this.consoleCommandTextField);
        this.previousOutputTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, new TranslatableText("advMode.previousOutput"));
        this.previousOutputTextField.setMaxLength(32500);
        this.previousOutputTextField.setEditable(false);
        this.previousOutputTextField.setText("-");
        this.children.add(this.previousOutputTextField);
        this.setInitialFocus(this.consoleCommandTextField);
        this.consoleCommandTextField.setSelected(true);
        this.commandSuggestor = new CommandSuggestor(this.client, this, this.consoleCommandTextField, this.textRenderer, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestor.setWindowActive(true);
        this.commandSuggestor.refresh();
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.consoleCommandTextField.getText();
        this.init(arg, i, j);
        this.consoleCommandTextField.setText(string);
        this.commandSuggestor.refresh();
    }

    protected void updateTrackedOutput() {
        if (this.getCommandExecutor().isTrackingOutput()) {
            this.toggleTrackingOutputButton.setMessage(new LiteralText("O"));
            this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
        } else {
            this.toggleTrackingOutputButton.setMessage(new LiteralText("X"));
            this.previousOutputTextField.setText("-");
        }
    }

    protected void commitAndClose() {
        CommandBlockExecutor lv = this.getCommandExecutor();
        this.syncSettingsToServer(lv);
        if (!lv.isTrackingOutput()) {
            lv.setLastOutput(null);
        }
        this.client.openScreen(null);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    protected abstract void syncSettingsToServer(CommandBlockExecutor var1);

    @Override
    public void onClose() {
        this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
        this.client.openScreen(null);
    }

    private void onCommandChanged(String string) {
        this.commandSuggestor.refresh();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.commandSuggestor.keyPressed(i, j, k)) {
            return true;
        }
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 257 || i == 335) {
            this.commitAndClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (this.commandSuggestor.mouseScrolled(f)) {
            return true;
        }
        return super.mouseScrolled(d, e, f);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.commandSuggestor.mouseClicked(d, e, i)) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("advMode.setCommand", new Object[0]), this.width / 2, 20, 0xFFFFFF);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("advMode.command", new Object[0]), this.width / 2 - 150, 40, 0xA0A0A0);
        this.consoleCommandTextField.render(arg, i, j, f);
        int k = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            this.textRenderer.getClass();
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("advMode.previousOutput", new Object[0]), this.width / 2 - 150, (k += 5 * 9 + 1 + this.getTrackOutputButtonHeight() - 135) + 4, 0xA0A0A0);
            this.previousOutputTextField.render(arg, i, j, f);
        }
        super.render(arg, i, j, f);
        this.commandSuggestor.render(arg, i, j);
    }
}

