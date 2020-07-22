/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;

@Environment(value=EnvType.CLIENT)
public class RecipeBookResults {
    private final List<AnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity((int)20);
    private AnimatedResultButton hoveredResultButton;
    private final RecipeAlternativesWidget alternatesWidget = new RecipeAlternativesWidget();
    private MinecraftClient client;
    private final List<RecipeDisplayListener> recipeDisplayListeners = Lists.newArrayList();
    private List<RecipeResultCollection> resultCollections;
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private int pageCount;
    private int currentPage;
    private RecipeBook recipeBook;
    private Recipe<?> lastClickedRecipe;
    private RecipeResultCollection resultCollection;

    public RecipeBookResults() {
        for (int i = 0; i < 20; ++i) {
            this.resultButtons.add(new AnimatedResultButton());
        }
    }

    public void initialize(MinecraftClient arg, int parentLeft, int parentTop) {
        this.client = arg;
        this.recipeBook = arg.player.getRecipeBook();
        for (int k = 0; k < this.resultButtons.size(); ++k) {
            this.resultButtons.get(k).setPos(parentLeft + 11 + 25 * (k % 5), parentTop + 31 + 25 * (k / 5));
        }
        this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, RecipeBookWidget.TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, RecipeBookWidget.TEXTURE);
    }

    public void setGui(RecipeBookWidget arg) {
        this.recipeDisplayListeners.remove(arg);
        this.recipeDisplayListeners.add(arg);
    }

    public void setResults(List<RecipeResultCollection> list, boolean resetCurrentPage) {
        this.resultCollections = list;
        this.pageCount = (int)Math.ceil((double)list.size() / 20.0);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }
        this.refreshResultButtons();
    }

    private void refreshResultButtons() {
        int i = 20 * this.currentPage;
        for (int j = 0; j < this.resultButtons.size(); ++j) {
            AnimatedResultButton lv = this.resultButtons.get(j);
            if (i + j < this.resultCollections.size()) {
                RecipeResultCollection lv2 = this.resultCollections.get(i + j);
                lv.showResultCollection(lv2, this);
                lv.visible = true;
                continue;
            }
            lv.visible = false;
        }
        this.hideShowPageButtons();
    }

    private void hideShowPageButtons() {
        this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
        this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
    }

    public void draw(MatrixStack arg, int i, int j, int k, int l, float f) {
        if (this.pageCount > 1) {
            String string = this.currentPage + 1 + "/" + this.pageCount;
            int m = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(arg, string, (float)(i - m / 2 + 73), (float)(j + 141), -1);
        }
        this.hoveredResultButton = null;
        for (AnimatedResultButton lv : this.resultButtons) {
            lv.render(arg, k, l, f);
            if (!lv.visible || !lv.isHovered()) continue;
            this.hoveredResultButton = lv;
        }
        this.prevPageButton.render(arg, k, l, f);
        this.nextPageButton.render(arg, k, l, f);
        this.alternatesWidget.render(arg, k, l, f);
    }

    public void drawTooltip(MatrixStack arg, int i, int j) {
        if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
            this.client.currentScreen.renderTooltip(arg, this.hoveredResultButton.getTooltip(this.client.currentScreen), i, j);
        }
    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeResultCollection getLastClickedResults() {
        return this.resultCollection;
    }

    public void hideAlternates() {
        this.alternatesWidget.setVisible(false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.alternatesWidget.isVisible()) {
            if (this.alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
                this.resultCollection = this.alternatesWidget.getResults();
            } else {
                this.alternatesWidget.setVisible(false);
            }
            return true;
        }
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        for (AnimatedResultButton lv : this.resultButtons) {
            if (!lv.mouseClicked(mouseX, mouseY, button)) continue;
            if (button == 0) {
                this.lastClickedRecipe = lv.currentRecipe();
                this.resultCollection = lv.getResultCollection();
            } else if (button == 1 && !this.alternatesWidget.isVisible() && !lv.hasResults()) {
                this.alternatesWidget.showAlternativesForResult(this.client, lv.getResultCollection(), lv.x, lv.y, areaLeft + areaWidth / 2, areaTop + 13 + areaHeight / 2, lv.getWidth());
            }
            return true;
        }
        return false;
    }

    public void onRecipesDisplayed(List<Recipe<?>> list) {
        for (RecipeDisplayListener lv : this.recipeDisplayListeners) {
            lv.onRecipesDisplayed(list);
        }
    }

    public MinecraftClient getMinecraftClient() {
        return this.client;
    }

    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}

