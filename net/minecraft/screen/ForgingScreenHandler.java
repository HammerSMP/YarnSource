/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForgingScreenHandler
extends ScreenHandler {
    protected final Inventory output = new CraftingResultInventory();
    protected final Inventory input = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            ForgingScreenHandler.this.onContentChanged(this);
        }
    };
    protected final ScreenHandlerContext context;
    protected final PlayerEntity player;

    protected abstract boolean canTakeOutput(PlayerEntity var1, boolean var2);

    protected abstract ItemStack onTakeOutput(PlayerEntity var1, ItemStack var2);

    protected abstract boolean canUse(BlockState var1);

    public ForgingScreenHandler(@Nullable ScreenHandlerType<?> arg, int i, PlayerInventory arg2, ScreenHandlerContext arg3) {
        super(arg, i);
        this.context = arg3;
        this.player = arg2.player;
        this.addSlot(new Slot(this.input, 0, 27, 47));
        this.addSlot(new Slot(this.input, 1, 76, 47));
        this.addSlot(new Slot(this.output, 2, 134, 47){

            @Override
            public boolean canInsert(ItemStack arg) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity arg) {
                return ForgingScreenHandler.this.canTakeOutput(arg, this.hasStack());
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
                return ForgingScreenHandler.this.onTakeOutput(arg, arg2);
            }
        });
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg2, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg2, l, 8 + l * 18, 142));
        }
    }

    public abstract void updateResult();

    @Override
    public void onContentChanged(Inventory arg) {
        super.onContentChanged(arg);
        if (arg == this.input) {
            this.updateResult();
        }
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.context.run((arg2, arg3) -> this.dropInventory(arg, (World)arg2, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.context.run((arg2, arg3) -> {
            if (!this.canUse(arg2.getBlockState((BlockPos)arg3))) {
                return false;
            }
            return arg.squaredDistanceTo((double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5) <= 64.0;
        }, true);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == 2) {
                if (!this.insertItem(lv3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (i == 0 || i == 1 ? !this.insertItem(lv3, 3, 39, false) : i >= 3 && i < 39 && !this.insertItem(lv3, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            lv2.onTakeItem(arg, lv3);
        }
        return lv;
    }
}
