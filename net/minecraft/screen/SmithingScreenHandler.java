/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingScreenHandler
extends ForgingScreenHandler {
    private final World field_25385;
    @Nullable
    private SmithingRecipe field_25386;
    private final List<SmithingRecipe> field_25668;

    public SmithingScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public SmithingScreenHandler(int i, PlayerInventory arg, ScreenHandlerContext arg2) {
        super(ScreenHandlerType.SMITHING, i, arg, arg2);
        this.field_25385 = arg.player.world;
        this.field_25668 = this.field_25385.getRecipeManager().listAllOfType(RecipeType.SMITHING);
    }

    @Override
    protected boolean canUse(BlockState arg) {
        return arg.isOf(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity arg, boolean bl) {
        return this.field_25386 != null && this.field_25386.matches(this.input, this.field_25385);
    }

    @Override
    protected ItemStack onTakeOutput(PlayerEntity arg3, ItemStack arg22) {
        this.method_29539(0);
        this.method_29539(1);
        this.context.run((arg, arg2) -> arg.syncWorldEvent(1044, (BlockPos)arg2, 0));
        return arg22;
    }

    private void method_29539(int i) {
        ItemStack lv = this.input.getStack(i);
        lv.decrement(1);
        this.input.setStack(i, lv);
    }

    @Override
    public void updateResult() {
        List<SmithingRecipe> list = this.field_25385.getRecipeManager().getAllMatches(RecipeType.SMITHING, this.input, this.field_25385);
        if (list.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
        } else {
            this.field_25386 = list.get(0);
            ItemStack lv = this.field_25386.craft(this.input);
            this.output.setStack(0, lv);
        }
    }

    @Override
    protected boolean method_30025(ItemStack arg) {
        return this.field_25668.stream().anyMatch(arg2 -> arg2.method_30029(arg));
    }
}

