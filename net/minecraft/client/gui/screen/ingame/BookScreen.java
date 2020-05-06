/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final Contents EMPTY_PROVIDER = new Contents(){

        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public Text getPageUnchecked(int i) {
            return LiteralText.EMPTY;
        }
    };
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private Contents contents;
    private int pageIndex;
    private List<Text> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final boolean pageTurnSound;

    public BookScreen(Contents arg) {
        this(arg, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents arg, boolean bl) {
        super(NarratorManager.EMPTY);
        this.contents = arg;
        this.pageTurnSound = bl;
    }

    public void setPageProvider(Contents arg) {
        this.contents = arg;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, arg.getPageCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int i) {
        int j = MathHelper.clamp(i, 0, this.contents.getPageCount() - 1);
        if (j != this.pageIndex) {
            this.pageIndex = j;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int i) {
        return this.setPage(i);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, arg -> this.client.openScreen(null)));
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, arg -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, arg -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        switch (i) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
        int k = (this.width - 192) / 2;
        int l = 2;
        this.drawTexture(arg, k, 2, 0, 0, 192, 192);
        String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        if (this.cachedPageIndex != this.pageIndex) {
            Text lv = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.getTextHandler().wrapLines(lv, 114, Style.EMPTY);
        }
        this.cachedPageIndex = this.pageIndex;
        int m = this.getStringWidth(string);
        this.textRenderer.draw(arg, string, (float)(k - m + 192 - 44), 18.0f, 0);
        this.textRenderer.getClass();
        int n = Math.min(128 / 9, this.cachedPage.size());
        for (int o = 0; o < n; ++o) {
            Text lv2 = this.cachedPage.get(o);
            this.textRenderer.getClass();
            this.textRenderer.draw(arg, lv2, (float)(k + 36), (float)(32 + o * 9), 0);
        }
        Text lv3 = this.getTextAt(i, j);
        if (lv3 != null) {
            this.renderTextHoverEffect(arg, lv3, i, j);
        }
        super.render(arg, i, j, f);
    }

    private int getStringWidth(String string) {
        return this.textRenderer.getWidth(this.textRenderer.isRightToLeft() ? this.textRenderer.mirror(string) : string);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        Text lv;
        if (i == 0 && (lv = this.getTextAt(d, e)) != null && this.handleTextClick(lv)) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean handleTextClick(Text arg) {
        ClickEvent lv = arg.getStyle().getClickEvent();
        if (lv == null) {
            return false;
        }
        if (lv.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = lv.getValue();
            try {
                int i = Integer.parseInt(string) - 1;
                return this.jumpToPage(i);
            }
            catch (Exception exception) {
                return false;
            }
        }
        boolean bl = super.handleTextClick(arg);
        if (bl && lv.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.client.openScreen(null);
        }
        return bl;
    }

    @Nullable
    public Text getTextAt(double d, double e) {
        if (this.cachedPage == null) {
            return null;
        }
        int i = MathHelper.floor(d - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(e - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        this.textRenderer.getClass();
        int k = Math.min(128 / 9, this.cachedPage.size());
        if (i <= 114) {
            this.client.textRenderer.getClass();
            if (j < 9 * k + k) {
                this.client.textRenderer.getClass();
                int l = j / 9;
                if (l >= 0 && l < this.cachedPage.size()) {
                    Text lv = this.cachedPage.get(l);
                    return this.client.textRenderer.getTextHandler().trimToWidth(lv, i);
                }
                return null;
            }
        }
        return null;
    }

    public static List<String> readPages(CompoundTag arg) {
        ListTag lv = arg.getList("pages", 8).copy();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < lv.size(); ++i) {
            builder.add((Object)lv.getString(i));
        }
        return builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class WritableBookContents
    implements Contents {
        private final List<String> pages;

        public WritableBookContents(ItemStack arg) {
            this.pages = WritableBookContents.getPages(arg);
        }

        private static List<String> getPages(ItemStack arg) {
            CompoundTag lv = arg.getTag();
            return lv != null ? BookScreen.readPages(lv) : ImmutableList.of();
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public Text getPageUnchecked(int i) {
            return new LiteralText(this.pages.get(i));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WrittenBookContents
    implements Contents {
        private final List<String> pages;

        public WrittenBookContents(ItemStack arg) {
            this.pages = WrittenBookContents.getPages(arg);
        }

        private static List<String> getPages(ItemStack arg) {
            CompoundTag lv = arg.getTag();
            if (lv != null && WrittenBookItem.isValid(lv)) {
                return BookScreen.readPages(lv);
            }
            return ImmutableList.of((Object)Text.Serializer.toJson(new TranslatableText("book.invalid.tag").formatted(Formatting.DARK_RED)));
        }

        @Override
        public int getPageCount() {
            return this.pages.size();
        }

        @Override
        public Text getPageUnchecked(int i) {
            String string = this.pages.get(i);
            try {
                MutableText lv = Text.Serializer.fromJson(string);
                if (lv != null) {
                    return lv;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return new LiteralText(string);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Contents {
        public int getPageCount();

        public Text getPageUnchecked(int var1);

        default public Text getPage(int i) {
            if (i >= 0 && i < this.getPageCount()) {
                return this.getPageUnchecked(i);
            }
            return LiteralText.EMPTY;
        }

        public static Contents create(ItemStack arg) {
            Item lv = arg.getItem();
            if (lv == Items.WRITTEN_BOOK) {
                return new WrittenBookContents(arg);
            }
            if (lv == Items.WRITABLE_BOOK) {
                return new WritableBookContents(arg);
            }
            return EMPTY_PROVIDER;
        }
    }
}

