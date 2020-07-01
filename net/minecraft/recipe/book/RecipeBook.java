/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5411;
import net.minecraft.class_5421;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.util.Identifier;

public class RecipeBook {
    protected final Set<Identifier> recipes = Sets.newHashSet();
    protected final Set<Identifier> toBeDisplayed = Sets.newHashSet();
    private final class_5411 field_25734 = new class_5411();

    public void copyFrom(RecipeBook arg) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.field_25734.method_30179(arg.field_25734);
        this.recipes.addAll(arg.recipes);
        this.toBeDisplayed.addAll(arg.toBeDisplayed);
    }

    public void add(Recipe<?> arg) {
        if (!arg.isIgnoredInRecipeBook()) {
            this.add(arg.getId());
        }
    }

    protected void add(Identifier arg) {
        this.recipes.add(arg);
    }

    public boolean contains(@Nullable Recipe<?> arg) {
        if (arg == null) {
            return false;
        }
        return this.recipes.contains(arg.getId());
    }

    public boolean contains(Identifier arg) {
        return this.recipes.contains(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Recipe<?> arg) {
        this.remove(arg.getId());
    }

    protected void remove(Identifier arg) {
        this.recipes.remove(arg);
        this.toBeDisplayed.remove(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDisplay(Recipe<?> arg) {
        return this.toBeDisplayed.contains(arg.getId());
    }

    public void onRecipeDisplayed(Recipe<?> arg) {
        this.toBeDisplayed.remove(arg.getId());
    }

    public void display(Recipe<?> arg) {
        this.display(arg.getId());
    }

    protected void display(Identifier arg) {
        this.toBeDisplayed.add(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen(class_5421 arg) {
        return this.field_25734.method_30180(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void setGuiOpen(class_5421 arg, boolean bl) {
        this.field_25734.method_30181(arg, bl);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(AbstractRecipeScreenHandler<?> arg) {
        return this.method_30176(arg.method_30264());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30176(class_5421 arg) {
        return this.field_25734.method_30187(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void method_30177(class_5421 arg, boolean bl) {
        this.field_25734.method_30188(arg, bl);
    }

    public void method_30174(class_5411 arg) {
        this.field_25734.method_30179(arg);
    }

    public class_5411 method_30173() {
        return this.field_25734.method_30178();
    }

    public void method_30175(class_5421 arg, boolean bl, boolean bl2) {
        this.field_25734.method_30181(arg, bl);
        this.field_25734.method_30188(arg, bl2);
    }
}

