/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.recipe;

import java.util.Iterator;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.math.MathHelper;

public interface RecipeGridAligner<T> {
    default public void alignRecipeToGrid(int gridWidth, int gridHeight, int gridOutputSlot, Recipe<?> recipe, Iterator<T> inputs, int amount) {
        int m = gridWidth;
        int n = gridHeight;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe lv = (ShapedRecipe)recipe;
            m = lv.getWidth();
            n = lv.getHeight();
        }
        int o = 0;
        block0: for (int p = 0; p < gridHeight; ++p) {
            if (o == gridOutputSlot) {
                ++o;
            }
            boolean bl = (float)n < (float)gridHeight / 2.0f;
            int q = MathHelper.floor((float)gridHeight / 2.0f - (float)n / 2.0f);
            if (bl && q > p) {
                o += gridWidth;
                ++p;
            }
            for (int r = 0; r < gridWidth; ++r) {
                boolean bl2;
                if (!inputs.hasNext()) {
                    return;
                }
                bl = (float)m < (float)gridWidth / 2.0f;
                q = MathHelper.floor((float)gridWidth / 2.0f - (float)m / 2.0f);
                int s = m;
                boolean bl3 = bl2 = r < m;
                if (bl) {
                    s = q + m;
                    boolean bl4 = bl2 = q <= r && r < q + m;
                }
                if (bl2) {
                    this.acceptAlignedInput(inputs, o, amount, p, r);
                } else if (s == r) {
                    o += gridWidth - r;
                    continue block0;
                }
                ++o;
            }
        }
    }

    public void acceptAlignedInput(Iterator<T> var1, int var2, int var3, int var4, int var5);
}

