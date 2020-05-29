/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class LoomScreenHandler
extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final Property selectedPattern = Property.create();
    private Runnable inventoryChangeListener = () -> {};
    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot outputSlot;
    private long lastTakeResultTime;
    private final Inventory input = new SimpleInventory(3){

        @Override
        public void markDirty() {
            super.markDirty();
            LoomScreenHandler.this.onContentChanged(this);
            LoomScreenHandler.this.inventoryChangeListener.run();
        }
    };
    private final Inventory output = new SimpleInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            LoomScreenHandler.this.inventoryChangeListener.run();
        }
    };

    public LoomScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public LoomScreenHandler(int i, PlayerInventory arg, final ScreenHandlerContext arg2) {
        super(ScreenHandlerType.LOOM, i);
        this.context = arg2;
        this.bannerSlot = this.addSlot(new Slot(this.input, 0, 13, 26){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.input, 1, 33, 26){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.input, 2, 23, 45){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() instanceof BannerPatternItem;
            }
        });
        this.outputSlot = this.addSlot(new Slot(this.output, 0, 143, 58){

            @Override
            public boolean canInsert(ItemStack arg) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity arg3, ItemStack arg22) {
                LoomScreenHandler.this.bannerSlot.takeStack(1);
                LoomScreenHandler.this.dyeSlot.takeStack(1);
                if (!LoomScreenHandler.this.bannerSlot.hasStack() || !LoomScreenHandler.this.dyeSlot.hasStack()) {
                    LoomScreenHandler.this.selectedPattern.set(0);
                }
                arg2.run((arg, arg2) -> {
                    long l = arg.getTime();
                    if (LoomScreenHandler.this.lastTakeResultTime != l) {
                        arg.playSound(null, (BlockPos)arg2, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        LoomScreenHandler.this.lastTakeResultTime = l;
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
        this.addProperty(this.selectedPattern);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return LoomScreenHandler.canUse(this.context, arg, Blocks.LOOM);
    }

    @Override
    public boolean onButtonClick(PlayerEntity arg, int i) {
        if (i > 0 && i <= BannerPattern.LOOM_APPLICABLE_COUNT) {
            this.selectedPattern.set(i);
            this.updateOutputSlot();
            return true;
        }
        return false;
    }

    @Override
    public void onContentChanged(Inventory arg) {
        ItemStack lv = this.bannerSlot.getStack();
        ItemStack lv2 = this.dyeSlot.getStack();
        ItemStack lv3 = this.patternSlot.getStack();
        ItemStack lv4 = this.outputSlot.getStack();
        if (!lv4.isEmpty() && (lv.isEmpty() || lv2.isEmpty() || this.selectedPattern.get() <= 0 || this.selectedPattern.get() >= BannerPattern.COUNT - BannerPattern.field_24417 && lv3.isEmpty())) {
            this.outputSlot.setStack(ItemStack.EMPTY);
            this.selectedPattern.set(0);
        } else if (!lv3.isEmpty() && lv3.getItem() instanceof BannerPatternItem) {
            boolean bl;
            CompoundTag lv5 = lv.getOrCreateSubTag("BlockEntityTag");
            boolean bl2 = bl = lv5.contains("Patterns", 9) && !lv.isEmpty() && lv5.getList("Patterns", 10).size() >= 6;
            if (bl) {
                this.selectedPattern.set(0);
            } else {
                this.selectedPattern.set(((BannerPatternItem)lv3.getItem()).getPattern().ordinal());
            }
        }
        this.updateOutputSlot();
        this.sendContentUpdates();
    }

    @Environment(value=EnvType.CLIENT)
    public void setInventoryChangeListener(Runnable runnable) {
        this.inventoryChangeListener = runnable;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == this.outputSlot.id) {
                if (!this.insertItem(lv3, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (i == this.dyeSlot.id || i == this.bannerSlot.id || i == this.patternSlot.id ? !this.insertItem(lv3, 4, 40, false) : (lv3.getItem() instanceof BannerItem ? !this.insertItem(lv3, this.bannerSlot.id, this.bannerSlot.id + 1, false) : (lv3.getItem() instanceof DyeItem ? !this.insertItem(lv3, this.dyeSlot.id, this.dyeSlot.id + 1, false) : (lv3.getItem() instanceof BannerPatternItem ? !this.insertItem(lv3, this.patternSlot.id, this.patternSlot.id + 1, false) : (i >= 4 && i < 31 ? !this.insertItem(lv3, 31, 40, false) : i >= 31 && i < 40 && !this.insertItem(lv3, 4, 31, false)))))) {
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

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.context.run((arg2, arg3) -> this.dropInventory(arg, arg.world, this.input));
    }

    private void updateOutputSlot() {
        if (this.selectedPattern.get() > 0) {
            ItemStack lv = this.bannerSlot.getStack();
            ItemStack lv2 = this.dyeSlot.getStack();
            ItemStack lv3 = ItemStack.EMPTY;
            if (!lv.isEmpty() && !lv2.isEmpty()) {
                ListTag lv8;
                lv3 = lv.copy();
                lv3.setCount(1);
                BannerPattern lv4 = BannerPattern.values()[this.selectedPattern.get()];
                DyeColor lv5 = ((DyeItem)lv2.getItem()).getColor();
                CompoundTag lv6 = lv3.getOrCreateSubTag("BlockEntityTag");
                if (lv6.contains("Patterns", 9)) {
                    ListTag lv7 = lv6.getList("Patterns", 10);
                } else {
                    lv8 = new ListTag();
                    lv6.put("Patterns", lv8);
                }
                CompoundTag lv9 = new CompoundTag();
                lv9.putString("Pattern", lv4.getId());
                lv9.putInt("Color", lv5.getId());
                lv8.add(lv9);
            }
            if (!ItemStack.areEqual(lv3, this.outputSlot.getStack())) {
                this.outputSlot.setStack(lv3);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public Slot getOutputSlot() {
        return this.outputSlot;
    }
}

