/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class BookEditScreen
extends Screen {
    private final PlayerEntity player;
    private final ItemStack itemStack;
    private boolean dirty;
    private boolean signing;
    private int tickCounter;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private String title = "";
    private final SelectionManager field_24269 = new SelectionManager(this::getCurrentPageContent, this::setPageContent, this::method_27595, this::method_27584, string -> string.length() < 1024 && this.textRenderer.getStringBoundedHeight((String)string, 114) <= 128);
    private final SelectionManager field_24270 = new SelectionManager(() -> this.title, string -> {
        this.title = string;
    }, this::method_27595, this::method_27584, string -> string.length() < 16);
    private long lastClickTime;
    private int lastClickIndex = -1;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private ButtonWidget doneButton;
    private ButtonWidget signButton;
    private ButtonWidget finalizeButton;
    private ButtonWidget cancelButton;
    private final Hand hand;
    @Nullable
    private class_5233 field_24268 = class_5233.method_27599();

    public BookEditScreen(PlayerEntity arg, ItemStack arg2, Hand arg3) {
        super(NarratorManager.EMPTY);
        this.player = arg;
        this.itemStack = arg2;
        this.hand = arg3;
        CompoundTag lv = arg2.getTag();
        if (lv != null) {
            ListTag lv2 = lv.getList("pages", 8).copy();
            for (int i = 0; i < lv2.size(); ++i) {
                this.pages.add(lv2.getString(i));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }

    private void method_27584(String string) {
        if (this.client != null) {
            SelectionManager.setClipboard(this.client, string);
        }
    }

    private String method_27595() {
        return this.client != null ? SelectionManager.getClipboard(this.client) : "";
    }

    private int countPages() {
        return this.pages.size();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickCounter;
    }

    @Override
    protected void init() {
        this.method_27577();
        this.client.keyboard.enableRepeatEvents(true);
        this.signButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.signButton"), arg -> {
            this.signing = true;
            this.updateButtons();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.DONE, arg -> {
            this.client.openScreen(null);
            this.finalizeBook(false);
        }));
        this.finalizeButton = this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, new TranslatableText("book.finalizeButton"), arg -> {
            if (this.signing) {
                this.finalizeBook(true);
                this.client.openScreen(null);
            }
        }));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, ScreenTexts.CANCEL, arg -> {
            if (this.signing) {
                this.signing = false;
            }
            this.updateButtons();
        }));
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, arg -> this.openNextPage(), true));
        this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, arg -> this.openPreviousPage(), true));
        this.updateButtons();
    }

    private void openPreviousPage() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtons();
        this.method_27872();
    }

    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
        } else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
        }
        this.updateButtons();
        this.method_27872();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        this.previousPageButton.visible = !this.signing && this.currentPage > 0;
        this.nextPageButton.visible = !this.signing;
        this.doneButton.visible = !this.signing;
        this.signButton.visible = !this.signing;
        this.cancelButton.visible = this.signing;
        this.finalizeButton.visible = this.signing;
        this.finalizeButton.active = !this.title.trim().isEmpty();
    }

    private void removeEmptyPages() {
        ListIterator<String> listIterator = this.pages.listIterator(this.pages.size());
        while (listIterator.hasPrevious() && listIterator.previous().isEmpty()) {
            listIterator.remove();
        }
    }

    private void finalizeBook(boolean bl) {
        if (!this.dirty) {
            return;
        }
        this.removeEmptyPages();
        ListTag lv = new ListTag();
        this.pages.stream().map(StringTag::of).forEach(lv::add);
        if (!this.pages.isEmpty()) {
            this.itemStack.putSubTag("pages", lv);
        }
        if (bl) {
            this.itemStack.putSubTag("author", StringTag.of(this.player.getGameProfile().getName()));
            this.itemStack.putSubTag("title", StringTag.of(this.title.trim()));
        }
        this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(this.itemStack, bl, this.hand));
    }

    private void appendNewPage() {
        if (this.countPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.dirty = true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (this.signing) {
            return this.keyPressedSignMode(i, j, k);
        }
        boolean bl = this.method_27592(i, j, k);
        if (bl) {
            this.method_27577();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (super.charTyped(c, i)) {
            return true;
        }
        if (this.signing) {
            boolean bl = this.field_24270.insert(c);
            if (bl) {
                this.updateButtons();
                this.dirty = true;
                return true;
            }
            return false;
        }
        if (SharedConstants.isValidChar(c)) {
            this.field_24269.insert(Character.toString(c));
            this.method_27577();
            return true;
        }
        return false;
    }

    private boolean method_27592(int i, int j, int k) {
        if (Screen.isSelectAll(i)) {
            this.field_24269.selectAll();
            return true;
        }
        if (Screen.isCopy(i)) {
            this.field_24269.copy();
            return true;
        }
        if (Screen.isPaste(i)) {
            this.field_24269.paste();
            return true;
        }
        if (Screen.isCut(i)) {
            this.field_24269.cut();
            return true;
        }
        switch (i) {
            case 259: {
                this.field_24269.delete(-1);
                return true;
            }
            case 261: {
                this.field_24269.delete(1);
                return true;
            }
            case 257: 
            case 335: {
                this.field_24269.insert("\n");
                return true;
            }
            case 263: {
                this.field_24269.moveCursor(-1, Screen.hasShiftDown());
                return true;
            }
            case 262: {
                this.field_24269.moveCursor(1, Screen.hasShiftDown());
                return true;
            }
            case 265: {
                this.method_27597();
                return true;
            }
            case 264: {
                this.method_27598();
                return true;
            }
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
            case 268: {
                this.moveCursorToTop();
                return true;
            }
            case 269: {
                this.moveCursorToBottom();
                return true;
            }
        }
        return false;
    }

    private void method_27597() {
        this.method_27580(-1);
    }

    private void method_27598() {
        this.method_27580(1);
    }

    private void method_27580(int i) {
        int j = this.field_24269.getSelectionStart();
        int k = this.method_27576().method_27601(j, i);
        this.field_24269.method_27560(k, Screen.hasShiftDown());
    }

    private void moveCursorToTop() {
        int i = this.field_24269.getSelectionStart();
        int j = this.method_27576().method_27600(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private void moveCursorToBottom() {
        class_5233 lv = this.method_27576();
        int i = this.field_24269.getSelectionStart();
        int j = lv.method_27604(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private boolean keyPressedSignMode(int i, int j, int k) {
        switch (i) {
            case 259: {
                this.field_24270.delete(-1);
                this.updateButtons();
                this.dirty = true;
                return true;
            }
            case 257: 
            case 335: {
                if (!this.title.isEmpty()) {
                    this.finalizeBook(true);
                    this.client.openScreen(null);
                }
                return true;
            }
        }
        return false;
    }

    private String getCurrentPageContent() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return this.pages.get(this.currentPage);
        }
        return "";
    }

    private void setPageContent(String string) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, string);
            this.dirty = true;
            this.method_27577();
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.setFocused(null);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
        int k = (this.width - 192) / 2;
        int l = 2;
        this.drawTexture(arg, k, 2, 0, 0, 192, 192);
        if (this.signing) {
            String string = this.title;
            string = this.tickCounter / 6 % 2 == 0 ? string + "" + (Object)((Object)Formatting.BLACK) + "_" : string + "" + (Object)((Object)Formatting.GRAY) + "_";
            String string2 = I18n.translate("book.editTitle", new Object[0]);
            int m = this.getStringWidth(string2);
            this.textRenderer.draw(arg, string2, (float)(k + 36 + (114 - m) / 2), 34.0f, 0);
            int n = this.getStringWidth(string);
            this.textRenderer.draw(arg, string, (float)(k + 36 + (114 - n) / 2), 50.0f, 0);
            String string3 = I18n.translate("book.byAuthor", this.player.getName().getString());
            int o = this.getStringWidth(string3);
            this.textRenderer.draw(arg, (Object)((Object)Formatting.DARK_GRAY) + string3, (float)(k + 36 + (114 - o) / 2), 60.0f, 0);
            this.textRenderer.drawTrimmed(new TranslatableText("book.finalizeWarning"), k + 36, 82, 114, 0);
        } else {
            String string4 = I18n.translate("book.pageIndicator", this.currentPage + 1, this.countPages());
            int p = this.getStringWidth(string4);
            this.textRenderer.draw(arg, string4, (float)(k - p + 192 - 44), 18.0f, 0);
            class_5233 lv = this.method_27576();
            for (Position lv2 : lv.field_24276) {
                this.textRenderer.draw(arg, lv2.field_24280, (float)lv2.x, (float)lv2.y, -16777216);
            }
            this.method_27588(lv.field_24277);
            this.method_27581(arg, lv.field_24273, lv.field_24274);
        }
        super.render(arg, i, j, f);
    }

    private void method_27581(MatrixStack arg, class_5234 arg2, boolean bl) {
        if (this.tickCounter / 6 % 2 == 0) {
            arg2 = this.method_27590(arg2);
            if (!bl) {
                this.textRenderer.getClass();
                DrawableHelper.fill(arg, arg2.field_24281, arg2.field_24282 - 1, arg2.field_24281 + 1, arg2.field_24282 + 9, -16777216);
            } else {
                this.textRenderer.draw(arg, "_", (float)arg2.field_24281, (float)arg2.field_24282, 0);
            }
        }
    }

    private int getStringWidth(String string) {
        return this.textRenderer.getWidth(this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(string) : string);
    }

    private void method_27588(Rect2i[] args) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        lv2.begin(7, VertexFormats.POSITION);
        for (Rect2i lv3 : args) {
            int i = lv3.getX();
            int j = lv3.getY();
            int k = i + lv3.getWidth();
            int l = j + lv3.getHeight();
            lv2.vertex(i, l, 0.0).next();
            lv2.vertex(k, l, 0.0).next();
            lv2.vertex(k, j, 0.0).next();
            lv2.vertex(i, j, 0.0).next();
        }
        lv.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private class_5234 method_27582(class_5234 arg) {
        return new class_5234(arg.field_24281 - (this.width - 192) / 2 - 36, arg.field_24282 - 32);
    }

    private class_5234 method_27590(class_5234 arg) {
        return new class_5234(arg.field_24281 + (this.width - 192) / 2 + 36, arg.field_24282 + 32);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (super.mouseClicked(d, e, i)) {
            return true;
        }
        if (i == 0) {
            long l = Util.getMeasuringTimeMs();
            class_5233 lv = this.method_27576();
            int j = lv.method_27602(this.textRenderer, this.method_27582(new class_5234((int)d, (int)e)));
            if (j >= 0) {
                if (j == this.lastClickIndex && l - this.lastClickTime < 250L) {
                    if (!this.field_24269.method_27568()) {
                        this.method_27589(j);
                    } else {
                        this.field_24269.selectAll();
                    }
                } else {
                    this.field_24269.method_27560(j, Screen.hasShiftDown());
                }
                this.method_27577();
            }
            this.lastClickIndex = j;
            this.lastClickTime = l;
        }
        return true;
    }

    private void method_27589(int i) {
        String string = this.getCurrentPageContent();
        this.field_24269.method_27548(TextHandler.moveCursorByWords(string, -1, i, false), TextHandler.moveCursorByWords(string, 1, i, false));
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (super.mouseDragged(d, e, i, f, g)) {
            return true;
        }
        if (i == 0) {
            class_5233 lv = this.method_27576();
            int j = lv.method_27602(this.textRenderer, this.method_27582(new class_5234((int)d, (int)e)));
            this.field_24269.method_27560(j, true);
            this.method_27577();
        }
        return true;
    }

    private class_5233 method_27576() {
        if (this.field_24268 == null) {
            this.field_24268 = this.method_27578();
        }
        return this.field_24268;
    }

    private void method_27577() {
        this.field_24268 = null;
    }

    private void method_27872() {
        this.field_24269.moveCaretToEnd();
        this.method_27577();
    }

    private class_5233 method_27578() {
        class_5234 lv3;
        boolean bl;
        String string = this.getCurrentPageContent();
        if (string.isEmpty()) {
            return class_5233.field_24271;
        }
        String string2 = this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(string) : string;
        int i = this.field_24269.getSelectionStart();
        int j = this.field_24269.getSelectionEnd();
        IntArrayList intList = new IntArrayList();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableBoolean mutableBoolean = new MutableBoolean();
        TextHandler lv = this.textRenderer.getTextHandler();
        lv.wrapLines(string2, 114, Style.EMPTY, true, (arg_0, arg_1, arg_2) -> this.method_27586(mutableInt, string2, mutableBoolean, (IntList)intList, list, arg_0, arg_1, arg_2));
        int[] is = intList.toIntArray();
        boolean bl2 = bl = i == string2.length();
        if (bl && mutableBoolean.isTrue()) {
            this.textRenderer.getClass();
            class_5234 lv2 = new class_5234(0, list.size() * 9);
        } else {
            int k = BookEditScreen.method_27591(is, i);
            int l = this.textRenderer.getWidth(string2.substring(is[k], i));
            this.textRenderer.getClass();
            lv3 = new class_5234(l, k * 9);
        }
        ArrayList list2 = Lists.newArrayList();
        if (i != j) {
            int p;
            int m = Math.min(i, j);
            int n = Math.max(i, j);
            int o = BookEditScreen.method_27591(is, m);
            if (o == (p = BookEditScreen.method_27591(is, n))) {
                this.textRenderer.getClass();
                int q = o * 9;
                int r = is[o];
                list2.add(this.method_27585(string2, lv, m, n, q, r));
            } else {
                int s = o + 1 > is.length ? string2.length() : is[o + 1];
                this.textRenderer.getClass();
                list2.add(this.method_27585(string2, lv, m, s, o * 9, is[o]));
                for (int t = o + 1; t < p; ++t) {
                    this.textRenderer.getClass();
                    int u = t * 9;
                    String string3 = string2.substring(is[t], is[t + 1]);
                    int v = (int)lv.getWidth(string3);
                    this.textRenderer.getClass();
                    list2.add(this.method_27583(new class_5234(0, u), new class_5234(v, u + 9)));
                }
                this.textRenderer.getClass();
                list2.add(this.method_27585(string2, lv, is[p], n, p * 9, is[p]));
            }
        }
        return new class_5233(string2, lv3, bl, is, list.toArray(new Position[0]), list2.toArray(new Rect2i[0]));
    }

    private static int method_27591(int[] is, int i) {
        int j = Arrays.binarySearch(is, i);
        if (j < 0) {
            return -(j + 2);
        }
        return j;
    }

    private Rect2i method_27585(String string, TextHandler arg, int i, int j, int k, int l) {
        String string2 = string.substring(l, i);
        String string3 = string.substring(l, j);
        class_5234 lv = new class_5234((int)arg.getWidth(string2), k);
        this.textRenderer.getClass();
        class_5234 lv2 = new class_5234((int)arg.getWidth(string3), k + 9);
        return this.method_27583(lv, lv2);
    }

    private Rect2i method_27583(class_5234 arg, class_5234 arg2) {
        class_5234 lv = this.method_27590(arg);
        class_5234 lv2 = this.method_27590(arg2);
        int i = Math.min(lv.field_24281, lv2.field_24281);
        int j = Math.max(lv.field_24281, lv2.field_24281);
        int k = Math.min(lv.field_24282, lv2.field_24282);
        int l = Math.max(lv.field_24282, lv2.field_24282);
        return new Rect2i(i, k, j - i, l - k);
    }

    private /* synthetic */ void method_27586(MutableInt mutableInt, String string, MutableBoolean mutableBoolean, IntList intList, List list, Style arg, int i, int j) {
        int k = mutableInt.getAndIncrement();
        String string2 = string.substring(i, j);
        mutableBoolean.setValue(string2.endsWith("\n"));
        String string3 = StringUtils.stripEnd((String)string2, (String)" \n");
        this.textRenderer.getClass();
        int l = k * 9;
        class_5234 lv = this.method_27590(new class_5234(0, l));
        intList.add(i);
        list.add(new Position(arg, string3, lv.field_24281, lv.field_24282));
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5233 {
        private static final class_5233 field_24271 = new class_5233("", new class_5234(0, 0), true, new int[]{0}, new Position[]{new Position(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String field_24272;
        private final class_5234 field_24273;
        private final boolean field_24274;
        private final int[] field_24275;
        private final Position[] field_24276;
        private final Rect2i[] field_24277;

        public class_5233(String string, class_5234 arg, boolean bl, int[] is, Position[] args, Rect2i[] args2) {
            this.field_24272 = string;
            this.field_24273 = arg;
            this.field_24274 = bl;
            this.field_24275 = is;
            this.field_24276 = args;
            this.field_24277 = args2;
        }

        public int method_27602(TextRenderer arg, class_5234 arg2) {
            arg.getClass();
            int i = arg2.field_24282 / 9;
            if (i < 0) {
                return 0;
            }
            if (i >= this.field_24276.length) {
                return this.field_24272.length();
            }
            Position lv = this.field_24276[i];
            return this.field_24275[i] + arg.getTextHandler().getTrimmedLength(lv.field_24279, arg2.field_24281, lv.field_24278);
        }

        public int method_27601(int i, int j) {
            int p;
            int k = BookEditScreen.method_27591(this.field_24275, i);
            int l = k + j;
            if (0 <= l && l < this.field_24275.length) {
                int m = i - this.field_24275[k];
                int n = this.field_24276[l].field_24279.length();
                int o = this.field_24275[l] + Math.min(m, n);
            } else {
                p = i;
            }
            return p;
        }

        public int method_27600(int i) {
            int j = BookEditScreen.method_27591(this.field_24275, i);
            return this.field_24275[j];
        }

        public int method_27604(int i) {
            int j = BookEditScreen.method_27591(this.field_24275, i);
            return this.field_24275[j] + this.field_24276[j].field_24279.length();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Position {
        private final Style field_24278;
        private final String field_24279;
        private final Text field_24280;
        private final int x;
        private final int y;

        public Position(Style arg, String string, int i, int j) {
            this.field_24278 = arg;
            this.field_24279 = string;
            this.x = i;
            this.y = j;
            this.field_24280 = new LiteralText(string).setStyle(arg);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5234 {
        public final int field_24281;
        public final int field_24282;

        class_5234(int i, int j) {
            this.field_24281 = i;
            this.field_24282 = j;
        }
    }
}

