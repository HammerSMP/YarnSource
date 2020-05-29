/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class CartographyTableScreenHandler
extends ScreenHandler {
    private final ScreenHandlerContext context;
    private boolean currentlyTakingItem;
    private long lastTakeResultTime;
    public final Inventory inventory = new SimpleInventory(2){

        @Override
        public void markDirty() {
            CartographyTableScreenHandler.this.onContentChanged(this);
            super.markDirty();
        }
    };
    private final CraftingResultInventory resultSlot = new CraftingResultInventory(){

        @Override
        public void markDirty() {
            CartographyTableScreenHandler.this.onContentChanged(this);
            super.markDirty();
        }
    };

    public CartographyTableScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public CartographyTableScreenHandler(int i, PlayerInventory arg, final ScreenHandlerContext arg2) {
        super(ScreenHandlerType.CARTOGRAPHY_TABLE, i);
        this.context = arg2;
        this.addSlot(new Slot(this.inventory, 0, 15, 15){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 15, 52){

            @Override
            public boolean canInsert(ItemStack arg) {
                Item lv = arg.getItem();
                return lv == Items.PAPER || lv == Items.MAP || lv == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultSlot, 2, 145, 39){

            @Override
            public boolean canInsert(ItemStack arg) {
                return false;
            }

            @Override
            public ItemStack takeStack(int i) {
                ItemStack lv = super.takeStack(i);
                ItemStack lv2 = arg2.run((arg2, arg3) -> {
                    ItemStack lv;
                    if (!CartographyTableScreenHandler.this.currentlyTakingItem && CartographyTableScreenHandler.this.inventory.getStack(1).getItem() == Items.GLASS_PANE && (lv = FilledMapItem.copyMap(arg2, CartographyTableScreenHandler.this.inventory.getStack(0))) != null) {
                        lv.setCount(1);
                        return lv;
                    }
                    return lv;
                }).orElse(lv);
                CartographyTableScreenHandler.this.inventory.removeStack(0, 1);
                CartographyTableScreenHandler.this.inventory.removeStack(1, 1);
                return lv2;
            }

            @Override
            protected void onCrafted(ItemStack arg, int i) {
                this.takeStack(i);
                super.onCrafted(arg, i);
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity arg3, ItemStack arg22) {
                arg22.getItem().onCraft(arg22, arg3.world, arg3);
                arg2.run((arg, arg2) -> {
                    long l = arg.getTime();
                    if (CartographyTableScreenHandler.this.lastTakeResultTime != l) {
                        arg.playSound(null, (BlockPos)arg2, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        CartographyTableScreenHandler.this.lastTakeResultTime = l;
                    }
                });
                return super.onTakeItem(arg3, arg22);
            }
        });
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg, l, 8 + l * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return CartographyTableScreenHandler.canUse(this.context, arg, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void onContentChanged(Inventory arg) {
        ItemStack lv = this.inventory.getStack(0);
        ItemStack lv2 = this.inventory.getStack(1);
        ItemStack lv3 = this.resultSlot.getStack(2);
        if (!lv3.isEmpty() && (lv.isEmpty() || lv2.isEmpty())) {
            this.resultSlot.removeStack(2);
        } else if (!lv.isEmpty() && !lv2.isEmpty()) {
            this.updateResult(lv, lv2, lv3);
        }
    }

    private void updateResult(ItemStack arg, ItemStack arg2, ItemStack arg3) {
        this.context.run((arg4, arg5) -> {
            void lv6;
            Item lv = arg2.getItem();
            MapState lv2 = FilledMapItem.getMapState(arg, arg4);
            if (lv2 == null) {
                return;
            }
            if (lv == Items.PAPER && !lv2.locked && lv2.scale < 4) {
                ItemStack lv3 = arg.copy();
                lv3.setCount(1);
                lv3.getOrCreateTag().putInt("map_scale_direction", 1);
                this.sendContentUpdates();
            } else if (lv == Items.GLASS_PANE && !lv2.locked) {
                ItemStack lv4 = arg.copy();
                lv4.setCount(1);
                this.sendContentUpdates();
            } else if (lv == Items.MAP) {
                ItemStack lv5 = arg.copy();
                lv5.setCount(2);
                this.sendContentUpdates();
            } else {
                this.resultSlot.removeStack(2);
                this.sendContentUpdates();
                return;
            }
            if (!ItemStack.areEqual((ItemStack)lv6, arg3)) {
                this.resultSlot.setStack(2, (ItemStack)lv6);
                this.sendContentUpdates();
            }
        });
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
        return arg2.inventory != this.resultSlot && super.canInsertIntoSlot(arg, arg2);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3;
            ItemStack lv4 = lv3 = lv2.getStack();
            Item lv5 = lv4.getItem();
            lv = lv4.copy();
            if (i == 2) {
                if (this.inventory.getStack(1).getItem() == Items.GLASS_PANE) {
                    lv4 = this.context.run((arg2, arg3) -> {
                        ItemStack lv = FilledMapItem.copyMap(arg2, this.inventory.getStack(0));
                        if (lv != null) {
                            lv.setCount(1);
                            return lv;
                        }
                        return lv3;
                    }).orElse(lv4);
                }
                lv5.onCraft(lv4, arg.world, arg);
                if (!this.insertItem(lv4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv4, lv);
            } else if (i == 1 || i == 0 ? !this.insertItem(lv4, 3, 39, false) : (lv5 == Items.FILLED_MAP ? !this.insertItem(lv4, 0, 1, false) : (lv5 == Items.PAPER || lv5 == Items.MAP || lv5 == Items.GLASS_PANE ? !this.insertItem(lv4, 1, 2, false) : (i >= 3 && i < 30 ? !this.insertItem(lv4, 30, 39, false) : i >= 30 && i < 39 && !this.insertItem(lv4, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if (lv4.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            }
            lv2.markDirty();
            if (lv4.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            this.currentlyTakingItem = true;
            lv2.onTakeItem(arg, lv4);
            this.currentlyTakingItem = false;
            this.sendContentUpdates();
        }
        return lv;
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.resultSlot.removeStack(2);
        this.context.run((arg2, arg3) -> this.dropInventory(arg, arg.world, this.inventory));
    }
}

