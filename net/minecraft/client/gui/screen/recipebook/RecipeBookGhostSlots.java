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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RecipeBookGhostSlots {
    private Recipe<?> recipe;
    private final List<GhostInputSlot> slots = Lists.newArrayList();
    private float time;

    public void reset() {
        this.recipe = null;
        this.slots.clear();
        this.time = 0.0f;
    }

    public void addSlot(Ingredient arg, int i, int j) {
        this.slots.add(new GhostInputSlot(arg, i, j));
    }

    public GhostInputSlot getSlot(int i) {
        return this.slots.get(i);
    }

    public int getSlotCount() {
        return this.slots.size();
    }

    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe<?> arg) {
        this.recipe = arg;
    }

    public void draw(MatrixStack arg, MinecraftClient arg2, int i, int j, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        for (int k = 0; k < this.slots.size(); ++k) {
            GhostInputSlot lv = this.slots.get(k);
            int l = lv.getX() + i;
            int m = lv.getY() + j;
            if (k == 0 && bl) {
                DrawableHelper.fill(arg, l - 4, m - 4, l + 20, m + 20, 0x30FF0000);
            } else {
                DrawableHelper.fill(arg, l, m, l + 16, m + 16, 0x30FF0000);
            }
            ItemStack lv2 = lv.getCurrentItemStack();
            ItemRenderer lv3 = arg2.getItemRenderer();
            lv3.renderInGui(lv2, l, m);
            RenderSystem.depthFunc(516);
            DrawableHelper.fill(arg, l, m, l + 16, m + 16, 0x30FFFFFF);
            RenderSystem.depthFunc(515);
            if (k != 0) continue;
            lv3.renderGuiItemOverlay(arg2.textRenderer, lv2, l, m);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class GhostInputSlot {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public GhostInputSlot(Ingredient arg2, int i, int j) {
            this.ingredient = arg2;
            this.x = i;
            this.y = j;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getCurrentItemStack() {
            ItemStack[] lvs = this.ingredient.getMatchingStacksClient();
            return lvs[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0f) % lvs.length];
        }
    }
}

