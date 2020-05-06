/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.screen;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public class SmithingScreenHandler
extends ForgingScreenHandler {
    private static final Map<Item, Item> RECIPES = ImmutableMap.builder().put((Object)Items.DIAMOND_CHESTPLATE, (Object)Items.NETHERITE_CHESTPLATE).put((Object)Items.DIAMOND_LEGGINGS, (Object)Items.NETHERITE_LEGGINGS).put((Object)Items.DIAMOND_HELMET, (Object)Items.NETHERITE_HELMET).put((Object)Items.DIAMOND_BOOTS, (Object)Items.NETHERITE_BOOTS).put((Object)Items.DIAMOND_SWORD, (Object)Items.NETHERITE_SWORD).put((Object)Items.DIAMOND_AXE, (Object)Items.NETHERITE_AXE).put((Object)Items.DIAMOND_PICKAXE, (Object)Items.NETHERITE_PICKAXE).put((Object)Items.DIAMOND_HOE, (Object)Items.NETHERITE_HOE).put((Object)Items.DIAMOND_SHOVEL, (Object)Items.NETHERITE_SHOVEL).build();

    public SmithingScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public SmithingScreenHandler(int i, PlayerInventory arg, ScreenHandlerContext arg2) {
        super(ScreenHandlerType.SMITHING, i, arg, arg2);
    }

    @Override
    protected boolean canUse(BlockState arg) {
        return arg.isOf(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity arg, boolean bl) {
        return RECIPES.containsKey(this.input.getStack(0).getItem()) && this.input.getStack(1).getItem() == Items.NETHERITE_INGOT;
    }

    @Override
    protected ItemStack onTakeOutput(PlayerEntity arg3, ItemStack arg22) {
        this.input.setStack(0, ItemStack.EMPTY);
        ItemStack lv = this.input.getStack(1);
        lv.decrement(1);
        this.input.setStack(1, lv);
        this.context.run((arg, arg2) -> arg.syncWorldEvent(1044, (BlockPos)arg2, 0));
        return arg22;
    }

    @Override
    public void updateResult() {
        ItemStack lv = this.input.getStack(0);
        ItemStack lv2 = this.input.getStack(1);
        Item lv3 = RECIPES.get(lv.getItem());
        if (lv2.getItem() == Items.NETHERITE_INGOT && lv3 != null) {
            ItemStack lv4 = new ItemStack(lv3);
            CompoundTag lv5 = lv.getTag();
            lv4.setTag(lv5 != null ? lv5.copy() : null);
            this.output.setStack(0, lv4);
        } else {
            this.output.setStack(0, ItemStack.EMPTY);
        }
    }
}

