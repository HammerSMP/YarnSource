/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.ItemTags;

public class BeaconScreenHandler
extends ScreenHandler {
    private final Inventory payment = new SimpleInventory(1){

        @Override
        public boolean isValid(int i, ItemStack arg) {
            return arg.getItem().isIn(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    private final PaymentSlot paymentSlot;
    private final ScreenHandlerContext context;
    private final PropertyDelegate propertyDelegate;

    public BeaconScreenHandler(int i, Inventory arg) {
        this(i, arg, new ArrayPropertyDelegate(3), ScreenHandlerContext.EMPTY);
    }

    public BeaconScreenHandler(int i, Inventory arg, PropertyDelegate arg2, ScreenHandlerContext arg3) {
        super(ScreenHandlerType.BEACON, i);
        BeaconScreenHandler.checkDataCount(arg2, 3);
        this.propertyDelegate = arg2;
        this.context = arg3;
        this.paymentSlot = new PaymentSlot(this.payment, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addProperties(arg2);
        int j = 36;
        int k = 137;
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg, m + l * 9 + 9, 36 + m * 18, 137 + l * 18));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(arg, n, 36 + n * 18, 195));
        }
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        if (arg.world.isClient) {
            return;
        }
        ItemStack lv = this.paymentSlot.takeStack(this.paymentSlot.getMaxStackAmount());
        if (!lv.isEmpty()) {
            arg.dropItem(lv, false);
        }
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return BeaconScreenHandler.canUse(this.context, arg, Blocks.BEACON);
    }

    @Override
    public void setProperty(int i, int j) {
        super.setProperty(i, j);
        this.sendContentUpdates();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == 0) {
                if (!this.insertItem(lv3, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (!this.paymentSlot.hasStack() && this.paymentSlot.canInsert(lv3) && lv3.getCount() == 1 ? !this.insertItem(lv3, 0, 1, false) : (i >= 1 && i < 28 ? !this.insertItem(lv3, 28, 37, false) : (i >= 28 && i < 37 ? !this.insertItem(lv3, 1, 28, false) : !this.insertItem(lv3, 1, 37, false)))) {
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

    @Environment(value=EnvType.CLIENT)
    public int getProperties() {
        return this.propertyDelegate.get(0);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getPrimaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(1));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getSecondaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(2));
    }

    public void setEffects(int i, int j) {
        if (this.paymentSlot.hasStack()) {
            this.propertyDelegate.set(1, i);
            this.propertyDelegate.set(2, j);
            this.paymentSlot.takeStack(1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasPayment() {
        return !this.payment.getStack(0).isEmpty();
    }

    class PaymentSlot
    extends Slot {
        public PaymentSlot(Inventory arg2, int i, int j, int k) {
            super(arg2, i, j, k);
        }

        @Override
        public boolean canInsert(ItemStack arg) {
            return arg.getItem().isIn(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackAmount() {
            return 1;
        }
    }
}

