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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
extends DrawableHelper
implements Drawable,
Element {
    private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
    private final List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private MinecraftClient client;
    private RecipeResultCollection resultCollection;
    private Recipe<?> lastClickedRecipe;
    private float time;
    private boolean furnace;

    public void showAlternativesForResult(MinecraftClient client, RecipeResultCollection results, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float u;
        float t;
        float s;
        float r;
        float h;
        this.client = client;
        this.resultCollection = results;
        if (client.player.currentScreenHandler instanceof AbstractFurnaceScreenHandler) {
            this.furnace = true;
        }
        boolean bl = client.player.getRecipeBook().isFilteringCraftable((AbstractRecipeScreenHandler)client.player.currentScreenHandler);
        List<Recipe<?>> list = results.getRecipes(true);
        List list2 = bl ? Collections.emptyList() : results.getRecipes(false);
        int m = list.size();
        int n = m + list2.size();
        int o = n <= 16 ? 4 : 5;
        int p = (int)Math.ceil((float)n / (float)o);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        int q = 25;
        float g = this.buttonX + Math.min(n, o) * 25;
        if (g > (h = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((g - h) / delta)));
        }
        if ((r = (float)(this.buttonY + p * 25)) > (s = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((r - s) / delta));
        }
        if ((t = (float)this.buttonY) < (u = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((t - u) / delta));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int v = 0; v < n; ++v) {
            boolean bl2 = v < m;
            Recipe lv = bl2 ? list.get(v) : (Recipe)list2.get(v - m);
            int w = this.buttonX + 4 + 25 * (v % o);
            int x = this.buttonY + 5 + 25 * (v / o);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(w, x, lv, bl2));
                continue;
            }
            this.alternativeButtons.add(new AlternativeButtonWidget(w, x, lv, bl2));
        }
        this.lastClickedRecipe = null;
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return false;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (AlternativeButtonWidget lv : this.alternativeButtons) {
            if (!lv.mouseClicked(mouseX, mouseY, button)) continue;
            this.lastClickedRecipe = lv.recipe;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.time += delta;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BG_TEX);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 170.0f);
        int k = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int l = Math.min(this.alternativeButtons.size(), k);
        int m = MathHelper.ceil((float)this.alternativeButtons.size() / (float)k);
        int n = 24;
        int o = 4;
        int p = 82;
        int q = 208;
        this.renderGrid(matrices, l, m, 24, 4, 82, 208);
        RenderSystem.disableBlend();
        for (AlternativeButtonWidget lv : this.alternativeButtons) {
            lv.render(matrices, mouseX, mouseY, delta);
        }
        RenderSystem.popMatrix();
    }

    private void renderGrid(MatrixStack arg, int i, int j, int k, int l, int m, int n) {
        this.drawTexture(arg, this.buttonX, this.buttonY, m, n, l, l);
        this.drawTexture(arg, this.buttonX + l * 2 + i * k, this.buttonY, m + k + l, n, l, l);
        this.drawTexture(arg, this.buttonX, this.buttonY + l * 2 + j * k, m, n + k + l, l, l);
        this.drawTexture(arg, this.buttonX + l * 2 + i * k, this.buttonY + l * 2 + j * k, m + k + l, n + k + l, l, l);
        for (int o = 0; o < i; ++o) {
            this.drawTexture(arg, this.buttonX + l + o * k, this.buttonY, m + l, n, k, l);
            this.drawTexture(arg, this.buttonX + l + (o + 1) * k, this.buttonY, m + l, n, l, l);
            for (int p = 0; p < j; ++p) {
                if (o == 0) {
                    this.drawTexture(arg, this.buttonX, this.buttonY + l + p * k, m, n + l, l, k);
                    this.drawTexture(arg, this.buttonX, this.buttonY + l + (p + 1) * k, m, n + l, l, l);
                }
                this.drawTexture(arg, this.buttonX + l + o * k, this.buttonY + l + p * k, m + l, n + l, k, k);
                this.drawTexture(arg, this.buttonX + l + (o + 1) * k, this.buttonY + l + p * k, m + l, n + l, l, k);
                this.drawTexture(arg, this.buttonX + l + o * k, this.buttonY + l + (p + 1) * k, m + l, n + l, k, l);
                this.drawTexture(arg, this.buttonX + l + (o + 1) * k - 1, this.buttonY + l + (p + 1) * k - 1, m + l, n + l, l + 1, l + 1);
                if (o != i - 1) continue;
                this.drawTexture(arg, this.buttonX + l * 2 + i * k, this.buttonY + l + p * k, m + k + l, n + l, l, k);
                this.drawTexture(arg, this.buttonX + l * 2 + i * k, this.buttonY + l + (p + 1) * k, m + k + l, n + l, l, l);
            }
            this.drawTexture(arg, this.buttonX + l + o * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, k, l);
            this.drawTexture(arg, this.buttonX + l + (o + 1) * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, l, l);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Environment(value=EnvType.CLIENT)
    class AlternativeButtonWidget
    extends AbstractButtonWidget
    implements RecipeGridAligner<Ingredient> {
        private final Recipe<?> recipe;
        private final boolean craftable;
        protected final List<InputSlot> slots;

        public AlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean craftable) {
            super(x, y, 200, 20, LiteralText.EMPTY);
            this.slots = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.craftable = craftable;
            this.alignRecipe(recipe);
        }

        protected void alignRecipe(Recipe<?> arg) {
            this.alignRecipeToGrid(3, 3, -1, arg, arg.getPreviewInputs().iterator(), 0);
        }

        @Override
        public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
            ItemStack[] lvs = inputs.next().getMatchingStacksClient();
            if (lvs.length != 0) {
                this.slots.add(new InputSlot(3 + gridY * 7, 3 + gridX * 7, lvs));
            }
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            int l;
            RenderSystem.enableAlphaTest();
            RecipeAlternativesWidget.this.client.getTextureManager().bindTexture(BG_TEX);
            int k = 152;
            if (!this.craftable) {
                k += 26;
            }
            int n = l = RecipeAlternativesWidget.this.furnace ? 130 : 78;
            if (this.isHovered()) {
                l += 26;
            }
            this.drawTexture(matrices, this.x, this.y, k, l, this.width, this.height);
            for (InputSlot lv : this.slots) {
                RenderSystem.pushMatrix();
                float g = 0.42f;
                int m = (int)((float)(this.x + lv.y) / 0.42f - 3.0f);
                int n2 = (int)((float)(this.y + lv.x) / 0.42f - 3.0f);
                RenderSystem.scalef(0.42f, 0.42f, 1.0f);
                RecipeAlternativesWidget.this.client.getItemRenderer().renderInGuiWithOverrides(lv.stacks[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0f) % lv.stacks.length], m, n2);
                RenderSystem.popMatrix();
            }
            RenderSystem.disableAlphaTest();
        }

        @Environment(value=EnvType.CLIENT)
        public class InputSlot {
            public final ItemStack[] stacks;
            public final int y;
            public final int x;

            public InputSlot(int y, int x, ItemStack[] stacks) {
                this.y = y;
                this.x = x;
                this.stacks = stacks;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class FurnaceAlternativeButtonWidget
    extends AlternativeButtonWidget {
        public FurnaceAlternativeButtonWidget(int i, int j, Recipe<?> arg2, boolean bl) {
            super(i, j, arg2, bl);
        }

        @Override
        protected void alignRecipe(Recipe<?> arg) {
            ItemStack[] lvs = arg.getPreviewInputs().get(0).getMatchingStacksClient();
            this.slots.add(new AlternativeButtonWidget.InputSlot(10, 10, lvs));
        }
    }
}

