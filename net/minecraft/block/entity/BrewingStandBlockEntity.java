/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
Tickable {
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    private int fuel;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return BrewingStandBlockEntity.this.brewTime;
                }
                case 1: {
                    return BrewingStandBlockEntity.this.fuel;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BrewingStandBlockEntity.this.brewTime = value;
                    break;
                }
                case 1: {
                    BrewingStandBlockEntity.this.fuel = value;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public BrewingStandBlockEntity() {
        super(BlockEntityType.BREWING_STAND);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.brewing");
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack lv : this.inventory) {
            if (lv.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        boolean[] bls;
        ItemStack lv = this.inventory.get(4);
        if (this.fuel <= 0 && lv.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            lv.decrement(1);
            this.markDirty();
        }
        boolean bl = this.canCraft();
        boolean bl2 = this.brewTime > 0;
        ItemStack lv2 = this.inventory.get(3);
        if (bl2) {
            boolean bl3;
            --this.brewTime;
            boolean bl4 = bl3 = this.brewTime == 0;
            if (bl3 && bl) {
                this.craft();
                this.markDirty();
            } else if (!bl) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.itemBrewing != lv2.getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (bl && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.itemBrewing = lv2.getItem();
            this.markDirty();
        }
        if (!this.world.isClient && !Arrays.equals(bls = this.getSlotsEmpty(), this.slotsEmptyLastTick)) {
            this.slotsEmptyLastTick = bls;
            BlockState lv3 = this.world.getBlockState(this.getPos());
            if (!(lv3.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
                lv3 = (BlockState)lv3.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], bls[i]);
            }
            this.world.setBlockState(this.pos, lv3, 2);
        }
    }

    public boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];
        for (int i = 0; i < 3; ++i) {
            if (this.inventory.get(i).isEmpty()) continue;
            bls[i] = true;
        }
        return bls;
    }

    private boolean canCraft() {
        ItemStack lv = this.inventory.get(3);
        if (lv.isEmpty()) {
            return false;
        }
        if (!BrewingRecipeRegistry.isValidIngredient(lv)) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack lv2 = this.inventory.get(i);
            if (lv2.isEmpty() || !BrewingRecipeRegistry.hasRecipe(lv2, lv)) continue;
            return true;
        }
        return false;
    }

    private void craft() {
        ItemStack lv = this.inventory.get(3);
        for (int i = 0; i < 3; ++i) {
            this.inventory.set(i, BrewingRecipeRegistry.craft(lv, this.inventory.get(i)));
        }
        lv.decrement(1);
        BlockPos lv2 = this.getPos();
        if (lv.getItem().hasRecipeRemainder()) {
            ItemStack lv3 = new ItemStack(lv.getItem().getRecipeRemainder());
            if (lv.isEmpty()) {
                lv = lv3;
            } else if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, (double)lv2.getX(), (double)lv2.getY(), (double)lv2.getZ(), lv3);
            }
        }
        this.inventory.set(3, lv);
        this.world.syncWorldEvent(1035, lv2, 0);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.brewTime = tag.getShort("BrewTime");
        this.fuel = tag.getByte("Fuel");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("BrewTime", (short)this.brewTime);
        Inventories.toTag(tag, this.inventory);
        tag.putByte("Fuel", (byte)this.fuel);
        return tag;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 3) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        }
        Item lv = stack.getItem();
        if (slot == 4) {
            return lv == Items.BLAZE_POWDER;
        }
        return (lv == Items.POTION || lv == Items.SPLASH_POTION || lv == Items.LINGERING_POTION || lv == Items.GLASS_BOTTLE) && this.getStack(slot).isEmpty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 3) {
            return stack.getItem() == Items.GLASS_BOTTLE;
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrewingStandScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}

