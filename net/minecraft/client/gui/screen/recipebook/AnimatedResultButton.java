/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends AbstractButtonWidget {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private AbstractRecipeScreenHandler<?> craftingScreenHandler;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, LiteralText.EMPTY);
    }

    public void showResultCollection(RecipeResultCollection arg, RecipeBookResults arg2) {
        this.results = arg;
        this.craftingScreenHandler = (AbstractRecipeScreenHandler)arg2.getMinecraftClient().player.currentScreenHandler;
        this.recipeBook = arg2.getRecipeBook();
        List<Recipe<?>> list = arg.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));
        for (Recipe<?> lv : list) {
            if (!this.recipeBook.shouldDisplay(lv)) continue;
            arg2.onRecipesDisplayed(list);
            this.bounce = 15.0f;
            break;
        }
    }

    public RecipeResultCollection getResultCollection() {
        return this.results;
    }

    public void setPos(int i, int j) {
        this.x = i;
        this.y = j;
    }

    @Override
    public void renderButton(MatrixStack arg, int i, int j, float f) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.getTextureManager().bindTexture(BG_TEX);
        int k = 29;
        if (!this.results.hasCraftableRecipes()) {
            k += 25;
        }
        int l = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
            l += 25;
        }
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(g, g, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
            this.bounce -= f;
        }
        this.drawTexture(arg, this.x, this.y, k, l, this.width, this.height);
        List<Recipe<?>> list = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list.size();
        ItemStack lv2 = list.get(this.currentResultIndex).getOutput();
        int m = 4;
        if (this.results.hasSingleOutput() && this.getResults().size() > 1) {
            lv.getItemRenderer().renderInGuiWithOverrides(lv2, this.x + m + 1, this.y + m + 1);
            --m;
        }
        lv.getItemRenderer().renderInGui(lv2, this.x + m, this.y + m);
        if (bl) {
            RenderSystem.popMatrix();
        }
    }

    private List<Recipe<?>> getResults() {
        List<Recipe<?>> list = this.results.getRecipes(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
            list.addAll(this.results.getRecipes(false));
        }
        return list;
    }

    public boolean hasResults() {
        return this.getResults().size() == 1;
    }

    public Recipe<?> currentRecipe() {
        List<Recipe<?>> list = this.getResults();
        return list.get(this.currentResultIndex);
    }

    public List<StringRenderable> getTooltip(Screen arg) {
        ItemStack lv = this.getResults().get(this.currentResultIndex).getOutput();
        ArrayList list = Lists.newArrayList(arg.getTooltipFromItem(lv));
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)).size() > 1) {
            list.add(new TranslatableText("gui.recipebook.moreRecipes"));
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }
}

