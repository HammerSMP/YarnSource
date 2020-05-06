/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChatScreen
extends Screen {
    private String field_2389 = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText = "";
    private CommandSuggestor commandSuggestor;

    public ChatScreen(String string) {
        super(NarratorManager.EMPTY);
        this.originalChatText = string;
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, (Text)new TranslatableText("chat.editBox")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ChatScreen.this.commandSuggestor.method_23958());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setHasBorder(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.children.add(this.chatField);
        this.commandSuggestor = new CommandSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.commandSuggestor.refresh();
        this.setInitialFocus(this.chatField);
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.chatField.getText();
        this.init(arg, i, j);
        this.setText(string);
        this.commandSuggestor.refresh();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        this.client.inGameHud.getChatHud().resetScroll();
    }

    @Override
    public void tick() {
        this.chatField.tick();
    }

    private void onChatFieldUpdate(String string) {
        String string2 = this.chatField.getText();
        this.commandSuggestor.setWindowActive(!string2.equals(this.originalChatText));
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
        if (i == 256) {
            this.client.openScreen(null);
            return true;
        }
        if (i == 257 || i == 335) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }
            this.client.openScreen(null);
            return true;
        }
        if (i == 265) {
            this.setChatFromHistory(-1);
            return true;
        }
        if (i == 264) {
            this.setChatFromHistory(1);
            return true;
        }
        if (i == 266) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        }
        if (i == 267) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (f > 1.0) {
            f = 1.0;
        }
        if (f < -1.0) {
            f = -1.0;
        }
        if (this.commandSuggestor.mouseScrolled(f)) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            f *= 7.0;
        }
        this.client.inGameHud.getChatHud().scroll(f);
        return true;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.commandSuggestor.mouseClicked((int)d, (int)e, i)) {
            return true;
        }
        if (i == 0) {
            ChatHud lv = this.client.inGameHud.getChatHud();
            if (lv.method_27146(d, e)) {
                return true;
            }
            Text lv2 = lv.getText(d, e);
            if (lv2 != null && this.handleTextClick(lv2)) {
                return true;
            }
        }
        if (this.chatField.mouseClicked(d, e, i)) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void insertText(String string, boolean bl) {
        if (bl) {
            this.chatField.setText(string);
        } else {
            this.chatField.write(string);
        }
    }

    public void setChatFromHistory(int i) {
        int j = this.messageHistorySize + i;
        int k = this.client.inGameHud.getChatHud().getMessageHistory().size();
        if ((j = MathHelper.clamp(j, 0, k)) == this.messageHistorySize) {
            return;
        }
        if (j == k) {
            this.messageHistorySize = k;
            this.chatField.setText(this.field_2389);
            return;
        }
        if (this.messageHistorySize == k) {
            this.field_2389 = this.chatField.getText();
        }
        this.chatField.setText(this.client.inGameHud.getChatHud().getMessageHistory().get(j));
        this.commandSuggestor.setWindowActive(false);
        this.messageHistorySize = j;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.setFocused(this.chatField);
        this.chatField.setSelected(true);
        ChatScreen.fill(arg, 2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(arg, i, j, f);
        this.commandSuggestor.render(arg, i, j);
        Text lv = this.client.inGameHud.getChatHud().getText(i, j);
        if (lv != null && lv.getStyle().getHoverEvent() != null) {
            this.renderTextHoverEffect(arg, lv, i, j);
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setText(String string) {
        this.chatField.setText(string);
    }
}

