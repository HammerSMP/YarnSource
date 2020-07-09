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

    public RecipeToast(Recipe<?> arg) {
        this.recipes.add(arg);
    }

    @Override
    public Toast.Visibility draw(MatrixStack arg, ToastManager arg2, long l) {
        if (this.justUpdated) {
            this.startTime = l;
            this.justUpdated = false;
        }
        if (this.recipes.isEmpty()) {
            return Toast.Visibility.HIDE;
        }
        arg2.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        arg2.drawTexture(arg, 0, 0, 0, 32, this.getWidth(), this.getHeight());
        arg2.getGame().textRenderer.draw(arg, I18n.translate("recipe.toast.title", new Object[0]), 30.0f, 7.0f, -11534256);
        arg2.getGame().textRenderer.draw(arg, I18n.translate("recipe.toast.description", new Object[0]), 30.0f, 18.0f, -16777216);
        Recipe<?> lv = this.recipes.get((int)(l / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
        ItemStack lv2 = lv.getRecipeKindIcon();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.6f, 0.6f, 1.0f);
        arg2.getGame().getItemRenderer().renderInGui(lv2, 3, 3);
        RenderSystem.popMatrix();
        arg2.getGame().getItemRenderer().renderInGui(lv.getOutput(), 8, 8);
        return l - this.startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public void addRecipes(Recipe<?> arg) {
        if (this.recipes.add(arg)) {
            this.justUpdated = true;
        }
    }

    public static void show(ToastManager arg, Recipe<?> arg2) {
        RecipeToast lv = arg.getToast(RecipeToast.class, TYPE);
        if (lv == null) {
            arg.add(new RecipeToast(arg2));
        } else {
            lv.addRecipes(arg2);
        }
    }
}

