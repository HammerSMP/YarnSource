/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    private final RecipeBookGroup category;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookGroup category) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
    }

    public void checkForNewRecipes(MinecraftClient client) {
        ClientRecipeBook lv = client.player.getRecipeBook();
        List<RecipeResultCollection> list = lv.getResultsForGroup(this.category);
        if (!(client.player.currentScreenHandler instanceof AbstractRecipeScreenHandler)) {
            return;
        }
        for (RecipeResultCollection lv2 : list) {
            for (Recipe<?> lv3 : lv2.getResults(lv.isFilteringCraftable((AbstractRecipeScreenHandler)client.player.currentScreenHandler))) {
                if (!lv.shouldDisplay(lv3)) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.bounce > 0.0f) {
            float g = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(1.0f, g, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.getTextureManager().bindTexture(this.texture);
        RenderSystem.disableDepthTest();
        int k = this.u;
        int l = this.v;
        if (this.toggled) {
            k += this.pressedUOffset;
        }
        if (this.isHovered()) {
            l += this.hoverVOffset;
        }
        int m = this.x;
        if (this.toggled) {
            m -= 2;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexture(matrices, m, this.y, k, l, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcons(lv.getItemRenderer());
        if (this.bounce > 0.0f) {
            RenderSystem.popMatrix();
            this.bounce -= delta;
        }
    }

    private void renderIcons(ItemRenderer itemRenderer) {
        int i;
        List<ItemStack> list = this.category.getIcons();
        int n = i = this.toggled ? -2 : 0;
        if (list.size() == 1) {
            itemRenderer.renderInGui(list.get(0), this.x + 9 + i, this.y + 5);
        } else if (list.size() == 2) {
            itemRenderer.renderInGui(list.get(0), this.x + 3 + i, this.y + 5);
            itemRenderer.renderInGui(list.get(1), this.x + 14 + i, this.y + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.category;
    }

    public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
        List<RecipeResultCollection> list = recipeBook.getResultsForGroup(this.category);
        this.visible = false;
        if (list != null) {
            for (RecipeResultCollection lv : list) {
                if (!lv.isInitialized() || !lv.hasFittingRecipes()) continue;
                this.visible = true;
                break;
            }
        }
        return this.visible;
    }
}

