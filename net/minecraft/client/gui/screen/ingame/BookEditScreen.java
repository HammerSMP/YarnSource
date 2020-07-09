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
import net.minecraft.text.StringRenderable;
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
    private static final StringRenderable field_25893 = new TranslatableText("book.editTitle");
    private static final StringRenderable field_25894 = new TranslatableText("book.finalizeWarning");
    private static final StringRenderable field_25895 = StringRenderable.styled("_", Style.EMPTY.withColor(Formatting.BLACK));
    private static final StringRenderable field_25896 = StringRenderable.styled("_", Style.EMPTY.withColor(Formatting.GRAY));
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
    private PageContent pageContent = PageContent.method_27599();
    private StringRenderable field_25891 = StringRenderable.EMPTY;
    private final StringRenderable field_25892;

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
        this.field_25892 = new TranslatableText("book.byAuthor", arg.getName()).formatted(Formatting.DARK_GRAY);
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
        this.invalidatePageContent();
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
            this.invalidatePageContent();
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
            this.invalidatePageContent();
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
        int k = this.getPageContent().method_27601(j, i);
        this.field_24269.method_27560(k, Screen.hasShiftDown());
    }

    private void moveCursorToTop() {
        int i = this.field_24269.getSelectionStart();
        int j = this.getPageContent().method_27600(i);
        this.field_24269.method_27560(j, Screen.hasShiftDown());
    }

    private void moveCursorToBottom() {
        PageContent lv = this.getPageContent();
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
            this.invalidatePageContent();
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
            boolean bl = this.tickCounter / 6 % 2 == 0;
            StringRenderable lv = StringRenderable.concat(StringRenderable.plain(this.title), bl ? field_25895 : field_25896);
            int m = this.textRenderer.getWidth(field_25893);
            this.textRenderer.draw(arg, field_25893, (float)(k + 36 + (114 - m) / 2), 34.0f, 0);
            int n = this.textRenderer.getWidth(lv);
            this.textRenderer.draw(arg, lv, (float)(k + 36 + (114 - n) / 2), 50.0f, 0);
            int o = this.textRenderer.getWidth(this.field_25892);
            this.textRenderer.draw(arg, this.field_25892, (float)(k + 36 + (114 - o) / 2), 60.0f, 0);
            this.textRenderer.drawTrimmed(field_25894, k + 36, 82, 114, 0);
        } else {
            int p = this.textRenderer.getWidth(this.field_25891);
            this.textRenderer.draw(arg, this.field_25891, (float)(k - p + 192 - 44), 18.0f, 0);
            PageContent lv2 = this.getPageContent();
            for (Line lv3 : lv2.lines) {
                this.textRenderer.draw(arg, lv3.text, (float)lv3.x, (float)lv3.y, -16777216);
            }
            this.method_27588(lv2.field_24277);
            this.method_27581(arg, lv2.position, lv2.field_24274);
        }
        super.render(arg, i, j, f);
    }

    private void method_27581(MatrixStack arg, Position arg2, boolean bl) {
        if (this.tickCounter / 6 % 2 == 0) {
            arg2 = this.method_27590(arg2);
            if (!bl) {
                this.textRenderer.getClass();
                DrawableHelper.fill(arg, arg2.x, arg2.y - 1, arg2.x + 1, arg2.y + 9, -16777216);
            } else {
                this.textRenderer.draw(arg, "_", (float)arg2.x, (float)arg2.y, 0);
            }
        }
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

    private Position method_27582(Position arg) {
        return new Position(arg.x - (this.width - 192) / 2 - 36, arg.y - 32);
    }

    private Position method_27590(Position arg) {
        return new Position(arg.x + (this.width - 192) / 2 + 36, arg.y + 32);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (super.mouseClicked(d, e, i)) {
            return true;
        }
        if (i == 0) {
            long l = Util.getMeasuringTimeMs();
            PageContent lv = this.getPageContent();
            int j = lv.method_27602(this.textRenderer, this.method_27582(new Position((int)d, (int)e)));
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
                this.invalidatePageContent();
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
            PageContent lv = this.getPageContent();
            int j = lv.method_27602(this.textRenderer, this.method_27582(new Position((int)d, (int)e)));
            this.field_24269.method_27560(j, true);
            this.invalidatePageContent();
        }
        return true;
    }

    private PageContent getPageContent() {
        if (this.pageContent == null) {
            this.pageContent = this.createPageContent();
            this.field_25891 = new TranslatableText("book.pageIndicator", this.currentPage + 1, this.countPages());
        }
        return this.pageContent;
    }

    private void invalidatePageContent() {
        this.pageContent = null;
    }

    private void method_27872() {
        this.field_24269.moveCaretToEnd();
        this.invalidatePageContent();
    }

    private PageContent createPageContent() {
        Position lv3;
        boolean bl;
        String string = this.getCurrentPageContent();
        if (string.isEmpty()) {
            return PageContent.EMPTY;
        }
        int i = this.field_24269.getSelectionStart();
        int j = this.field_24269.getSelectionEnd();
        IntArrayList intList = new IntArrayList();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableBoolean mutableBoolean = new MutableBoolean();
        TextHandler lv = this.textRenderer.getTextHandler();
        lv.wrapLines(string, 114, Style.EMPTY, true, (arg_0, arg_1, arg_2) -> this.method_27586(mutableInt, string, mutableBoolean, (IntList)intList, list, arg_0, arg_1, arg_2));
        int[] is = intList.toIntArray();
        boolean bl2 = bl = i == string.length();
        if (bl && mutableBoolean.isTrue()) {
            this.textRenderer.getClass();
            Position lv2 = new Position(0, list.size() * 9);
        } else {
            int k = BookEditScreen.method_27591(is, i);
            int l = this.textRenderer.getWidth(string.substring(is[k], i));
            this.textRenderer.getClass();
            lv3 = new Position(l, k * 9);
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
                list2.add(this.method_27585(string, lv, m, n, q, r));
            } else {
                int s = o + 1 > is.length ? string.length() : is[o + 1];
                this.textRenderer.getClass();
                list2.add(this.method_27585(string, lv, m, s, o * 9, is[o]));
                for (int t = o + 1; t < p; ++t) {
                    this.textRenderer.getClass();
                    int u = t * 9;
                    String string2 = string.substring(is[t], is[t + 1]);
                    int v = (int)lv.getWidth(string2);
                    this.textRenderer.getClass();
                    list2.add(this.method_27583(new Position(0, u), new Position(v, u + 9)));
                }
                this.textRenderer.getClass();
                list2.add(this.method_27585(string, lv, is[p], n, p * 9, is[p]));
            }
        }
        return new PageContent(string, lv3, bl, is, list.toArray(new Line[0]), list2.toArray(new Rect2i[0]));
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
        Position lv = new Position((int)arg.getWidth(string2), k);
        this.textRenderer.getClass();
        Position lv2 = new Position((int)arg.getWidth(string3), k + 9);
        return this.method_27583(lv, lv2);
    }

    private Rect2i method_27583(Position arg, Position arg2) {
        Position lv = this.method_27590(arg);
        Position lv2 = this.method_27590(arg2);
        int i = Math.min(lv.x, lv2.x);
        int j = Math.max(lv.x, lv2.x);
        int k = Math.min(lv.y, lv2.y);
        int l = Math.max(lv.y, lv2.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    private /* synthetic */ void method_27586(MutableInt mutableInt, String string, MutableBoolean mutableBoolean, IntList intList, List list, Style arg, int i, int j) {
        int k = mutableInt.getAndIncrement();
        String string2 = string.substring(i, j);
        mutableBoolean.setValue(string2.endsWith("\n"));
        String string3 = StringUtils.stripEnd((String)string2, (String)" \n");
        this.textRenderer.getClass();
        int l = k * 9;
        Position lv = this.method_27590(new Position(0, l));
        intList.add(i);
        list.add(new Line(arg, string3, lv.x, lv.y));
    }

    @Environment(value=EnvType.CLIENT)
    static class PageContent {
        private static final PageContent EMPTY = new PageContent("", new Position(0, 0), true, new int[]{0}, new Line[]{new Line(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String pageContent;
        private final Position position;
        private final boolean field_24274;
        private final int[] field_24275;
        private final Line[] lines;
        private final Rect2i[] field_24277;

        public PageContent(String string, Position arg, boolean bl, int[] is, Line[] args, Rect2i[] args2) {
            this.pageContent = string;
            this.position = arg;
            this.field_24274 = bl;
            this.field_24275 = is;
            this.lines = args;
            this.field_24277 = args2;
        }

        public int method_27602(TextRenderer arg, Position arg2) {
            arg.getClass();
            int i = arg2.y / 9;
            if (i < 0) {
                return 0;
            }
            if (i >= this.lines.length) {
                return this.pageContent.length();
            }
            Line lv = this.lines[i];
            return this.field_24275[i] + arg.getTextHandler().getTrimmedLength(lv.content, arg2.x, lv.style);
        }

        public int method_27601(int i, int j) {
            int p;
            int k = BookEditScreen.method_27591(this.field_24275, i);
            int l = k + j;
            if (0 <= l && l < this.field_24275.length) {
                int m = i - this.field_24275[k];
                int n = this.lines[l].content.length();
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
            return this.field_24275[j] + this.lines[j].content.length();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Line {
        private final Style style;
        private final String content;
        private final Text text;
        private final int x;
        private final int y;

        public Line(Style arg, String string, int i, int j) {
            this.style = arg;
            this.content = string;
            this.x = i;
            this.y = j;
            this.text = new LiteralText(string).setStyle(arg);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Position {
        public final int x;
        public final int y;

        Position(int i, int j) {
            this.x = i;
            this.y = j;
        }
    }
}

