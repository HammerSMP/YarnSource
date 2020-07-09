/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TextFieldWidget
extends AbstractButtonWidget
implements Drawable,
Element {
    private final TextRenderer textRenderer;
    private String text = "";
    private int maxLength = 32;
    private int focusedTicks;
    private boolean focused = true;
    private boolean focusUnlocked = true;
    private boolean editable = true;
    private boolean selecting;
    private int firstCharacterIndex;
    private int selectionStart;
    private int selectionEnd;
    private int editableColor = 0xE0E0E0;
    private int uneditableColor = 0x707070;
    private String suggestion;
    private Consumer<String> changedListener;
    private Predicate<String> textPredicate = Objects::nonNull;
    private BiFunction<String, Integer, StringRenderable> renderTextProvider = (string, integer) -> StringRenderable.plain(string);

    public TextFieldWidget(TextRenderer arg, int i, int j, int k, int l, Text arg2) {
        this(arg, i, j, k, l, null, arg2);
    }

    public TextFieldWidget(TextRenderer arg, int i, int j, int k, int l, @Nullable TextFieldWidget arg2, Text arg3) {
        super(i, j, k, l, arg3);
        this.textRenderer = arg;
        if (arg2 != null) {
            this.setText(arg2.getText());
        }
    }

    public void setChangedListener(Consumer<String> consumer) {
        this.changedListener = consumer;
    }

    public void setRenderTextProvider(BiFunction<String, Integer, StringRenderable> biFunction) {
        this.renderTextProvider = biFunction;
    }

    public void tick() {
        ++this.focusedTicks;
    }

    @Override
    protected MutableText getNarrationMessage() {
        Text lv = this.getMessage();
        return new TranslatableText("gui.narrate.editBox", lv, this.text);
    }

    public void setText(String string) {
        if (!this.textPredicate.test(string)) {
            return;
        }
        this.text = string.length() > this.maxLength ? string.substring(0, this.maxLength) : string;
        this.setCursorToEnd();
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(string);
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
        int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
        return this.text.substring(i, j);
    }

    public void setTextPredicate(Predicate<String> predicate) {
        this.textPredicate = predicate;
    }

    public void write(String string) {
        String string3;
        String string2;
        int l;
        int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
        int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
        int k = this.maxLength - this.text.length() - (i - j);
        if (k < (l = (string2 = SharedConstants.stripInvalidChars(string)).length())) {
            string2 = string2.substring(0, k);
            l = k;
        }
        if (!this.textPredicate.test(string3 = new StringBuilder(this.text).replace(i, j, string2).toString())) {
            return;
        }
        this.text = string3;
        this.setSelectionStart(i + l);
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(this.text);
    }

    private void onChanged(String string) {
        if (this.changedListener != null) {
            this.changedListener.accept(string);
        }
        this.nextNarration = Util.getMeasuringTimeMs() + 500L;
    }

    private void erase(int i) {
        if (Screen.hasControlDown()) {
            this.eraseWords(i);
        } else {
            this.eraseCharacters(i);
        }
    }

    public void eraseWords(int i) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        this.eraseCharacters(this.getWordSkipPosition(i) - this.selectionStart);
    }

    public void eraseCharacters(int i) {
        int l;
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        int j = this.method_27537(i);
        int k = Math.min(j, this.selectionStart);
        if (k == (l = Math.max(j, this.selectionStart))) {
            return;
        }
        String string = new StringBuilder(this.text).delete(k, l).toString();
        if (!this.textPredicate.test(string)) {
            return;
        }
        this.text = string;
        this.setCursor(k);
    }

    public int getWordSkipPosition(int i) {
        return this.getWordSkipPosition(i, this.getCursor());
    }

    private int getWordSkipPosition(int i, int j) {
        return this.getWordSkipPosition(i, j, true);
    }

    private int getWordSkipPosition(int i, int j, boolean bl) {
        int k = j;
        boolean bl2 = i < 0;
        int l = Math.abs(i);
        for (int m = 0; m < l; ++m) {
            if (bl2) {
                while (bl && k > 0 && this.text.charAt(k - 1) == ' ') {
                    --k;
                }
                while (k > 0 && this.text.charAt(k - 1) != ' ') {
                    --k;
                }
                continue;
            }
            int n = this.text.length();
            if ((k = this.text.indexOf(32, k)) == -1) {
                k = n;
                continue;
            }
            while (bl && k < n && this.text.charAt(k) == ' ') {
                ++k;
            }
        }
        return k;
    }

    public void moveCursor(int i) {
        this.setCursor(this.method_27537(i));
    }

    private int method_27537(int i) {
        return Util.moveCursor(this.text, this.selectionStart, i);
    }

    public void setCursor(int i) {
        this.setSelectionStart(i);
        if (!this.selecting) {
            this.setSelectionEnd(this.selectionStart);
        }
        this.onChanged(this.text);
    }

    public void setSelectionStart(int i) {
        this.selectionStart = MathHelper.clamp(i, 0, this.text.length());
    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (!this.isActive()) {
            return false;
        }
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(i)) {
            this.setCursorToEnd();
            this.setSelectionEnd(0);
            return true;
        }
        if (Screen.isCopy(i)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(i)) {
            if (this.editable) {
                this.write(MinecraftClient.getInstance().keyboard.getClipboard());
            }
            return true;
        }
        if (Screen.isCut(i)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            if (this.editable) {
                this.write("");
            }
            return true;
        }
        switch (i) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1));
                } else {
                    this.moveCursor(1);
                }
                return true;
            }
            case 259: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(-1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 261: {
                if (this.editable) {
                    this.selecting = false;
                    this.erase(1);
                    this.selecting = Screen.hasShiftDown();
                }
                return true;
            }
            case 268: {
                this.setCursorToStart();
                return true;
            }
            case 269: {
                this.setCursorToEnd();
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (!this.isActive()) {
            return false;
        }
        if (SharedConstants.isValidChar(c)) {
            if (this.editable) {
                this.write(Character.toString(c));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean bl;
        if (!this.isVisible()) {
            return false;
        }
        boolean bl2 = bl = d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
        if (this.focusUnlocked) {
            this.setSelected(bl);
        }
        if (this.isFocused() && bl && i == 0) {
            int j = MathHelper.floor(d) - this.x;
            if (this.focused) {
                j -= 4;
            }
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
            this.setCursor(this.textRenderer.trimToWidth(string, j).length() + this.firstCharacterIndex);
            return true;
        }
        return false;
    }

    public void setSelected(boolean bl) {
        super.setFocused(bl);
    }

    @Override
    public void renderButton(MatrixStack arg, int i, int j, float f) {
        if (!this.isVisible()) {
            return;
        }
        if (this.hasBorder()) {
            int k = this.isFocused() ? -1 : -6250336;
            TextFieldWidget.fill(arg, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, k);
            TextFieldWidget.fill(arg, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }
        int l = this.editable ? this.editableColor : this.uneditableColor;
        int m = this.selectionStart - this.firstCharacterIndex;
        int n = this.selectionEnd - this.firstCharacterIndex;
        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        boolean bl = m >= 0 && m <= string.length();
        boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
        int o = this.focused ? this.x + 4 : this.x;
        int p = this.focused ? this.y + (this.height - 8) / 2 : this.y;
        int q = o;
        if (n > string.length()) {
            n = string.length();
        }
        if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, m) : string;
            q = this.textRenderer.drawWithShadow(arg, this.renderTextProvider.apply(string2, this.firstCharacterIndex), (float)q, (float)p, l);
        }
        boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
        int r = q;
        if (!bl) {
            r = m > 0 ? o + this.width : o;
        } else if (bl3) {
            --r;
            --q;
        }
        if (!string.isEmpty() && bl && m < string.length()) {
            this.textRenderer.drawWithShadow(arg, this.renderTextProvider.apply(string.substring(m), this.selectionStart), (float)q, (float)p, l);
        }
        if (!bl3 && this.suggestion != null) {
            this.textRenderer.drawWithShadow(arg, this.suggestion, (float)(r - 1), (float)p, -8355712);
        }
        if (bl2) {
            if (bl3) {
                this.textRenderer.getClass();
                DrawableHelper.fill(arg, r, p - 1, r + 1, p + 1 + 9, -3092272);
            } else {
                this.textRenderer.drawWithShadow(arg, "_", (float)r, (float)p, l);
            }
        }
        if (n != m) {
            int s = o + this.textRenderer.getWidth(string.substring(0, n));
            this.textRenderer.getClass();
            this.drawSelectionHighlight(r, p - 1, s - 1, p + 1 + 9);
        }
    }

    private void drawSelectionHighlight(int i, int j, int k, int l) {
        if (i < k) {
            int m = i;
            i = k;
            k = m;
        }
        if (j < l) {
            int n = j;
            j = l;
            l = n;
        }
        if (k > this.x + this.width) {
            k = this.x + this.width;
        }
        if (i > this.x + this.width) {
            i = this.x + this.width;
        }
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        lv2.begin(7, VertexFormats.POSITION);
        lv2.vertex(i, l, 0.0).next();
        lv2.vertex(k, l, 0.0).next();
        lv2.vertex(k, j, 0.0).next();
        lv2.vertex(i, j, 0.0).next();
        lv.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxLength(int i) {
        this.maxLength = i;
        if (this.text.length() > i) {
            this.text = this.text.substring(0, i);
            this.onChanged(this.text);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    private boolean hasBorder() {
        return this.focused;
    }

    public void setHasBorder(boolean bl) {
        this.focused = bl;
    }

    public void setEditableColor(int i) {
        this.editableColor = i;
    }

    public void setUneditableColor(int i) {
        this.uneditableColor = i;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.visible || !this.editable) {
            return false;
        }
        return super.changeFocus(bl);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return this.visible && d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
    }

    @Override
    protected void onFocusedChanged(boolean bl) {
        if (bl) {
            this.focusedTicks = 0;
        }
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean bl) {
        this.editable = bl;
    }

    public int getInnerWidth() {
        return this.hasBorder() ? this.width - 8 : this.width;
    }

    public void setSelectionEnd(int i) {
        int j = this.text.length();
        this.selectionEnd = MathHelper.clamp(i, 0, j);
        if (this.textRenderer != null) {
            if (this.firstCharacterIndex > j) {
                this.firstCharacterIndex = j;
            }
            int k = this.getInnerWidth();
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), k);
            int l = string.length() + this.firstCharacterIndex;
            if (this.selectionEnd == this.firstCharacterIndex) {
                this.firstCharacterIndex -= this.textRenderer.trimToWidth(this.text, k, true).length();
            }
            if (this.selectionEnd > l) {
                this.firstCharacterIndex += this.selectionEnd - l;
            } else if (this.selectionEnd <= this.firstCharacterIndex) {
                this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
            }
            this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, j);
        }
    }

    public void setFocusUnlocked(boolean bl) {
        this.focusUnlocked = bl;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }

    public void setSuggestion(@Nullable String string) {
        this.suggestion = string;
    }

    public int getCharacterX(int i) {
        if (i > this.text.length()) {
            return this.x;
        }
        return this.x + this.textRenderer.getWidth(this.text.substring(0, i));
    }

    public void setX(int i) {
        this.x = i;
    }
}

