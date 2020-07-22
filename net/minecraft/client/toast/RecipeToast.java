/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    private final List<Recipe<?>> recipes = Lists.newArrayList();
    private long startTime;
    private boolean justUpdated;

    public RecipeToast(Recipe<?> recipes) {
        this.recipes.add(recipes);
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        if (this.recipes.isEmpty()) {
            return Toast.Visibility.HIDE;
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 32, this.getWidth(), this.getHeight());
        manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.title", new Object[0]), 30.0f, 7.0f, -11534256);
        manager.getGame().textRenderer.draw(matrices, I18n.translate("recipe.toast.description", new Object[0]), 30.0f, 18.0f, -16777216);
        Recipe<?> lv = this.recipes.get((int)(startTime / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
        ItemStack lv2 = lv.getRecipeKindIcon();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.6f, 0.6f, 1.0f);
        manager.getGame().getItemRenderer().renderInGui(lv2, 3, 3);
        RenderSystem.popMatrix();
        manager.getGame().getItemRenderer().renderInGui(lv.getOutput(), 8, 8);
        return startTime - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public void addRecipes(Recipe<?> recipes) {
        if (this.recipes.add(recipes)) {
            this.justUpdated = true;
        }
    }

    public static void show(ToastManager manager, Recipe<?> recipes) {
        RecipeToast lv = manager.getToast(RecipeToast.class, TYPE);
        if (lv == null) {
            manager.add(new RecipeToast(recipes));
        } else {
            lv.addRecipes(recipes);
        }
    }
}

