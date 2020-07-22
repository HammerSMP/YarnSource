/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory
implements Inventory,
Nameable {
    public final DefaultedList<ItemStack> main = DefaultedList.ofSize(36, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
    public int selectedSlot;
    public final PlayerEntity player;
    private ItemStack cursorStack = ItemStack.EMPTY;
    private int changeCount;

    public PlayerInventory(PlayerEntity player) {
        this.player = player;
    }

    public ItemStack getMainHandStack() {
        if (PlayerInventory.isValidHotbarIndex(this.selectedSlot)) {
            return this.main.get(this.selectedSlot);
        }
        return ItemStack.EMPTY;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    public void addPickBlock(ItemStack stack) {
        int i = this.getSlotWithStack(stack);
        if (PlayerInventory.isValidHotbarIndex(i)) {
            this.selectedSlot = i;
            return;
        }
        if (i == -1) {
            int j;
            this.selectedSlot = this.getSwappableHotbarSlot();
            if (!this.main.get(this.selectedSlot).isEmpty() && (j = this.getEmptySlot()) != -1) {
                this.main.set(j, this.main.get(this.selectedSlot));
            }
            this.main.set(this.selectedSlot, stack);
        } else {
            this.swapSlotWithHotbar(i);
        }
    }

    public void swapSlotWithHotbar(int hotbarSlot) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        ItemStack lv = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(hotbarSlot));
        this.main.set(hotbarSlot, lv);
    }

    public static boolean isValidHotbarIndex(int slot) {
        return slot >= 0 && slot < 9;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(stack, this.main.get(i))) continue;
            return i;
        }
        return -1;
    }

    public int method_7371(ItemStack arg) {
        for (int i = 0; i < this.main.size(); ++i) {
            ItemStack lv = this.main.get(i);
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(arg, this.main.get(i)) || this.main.get(i).isDamaged() || lv.hasEnchantments() || lv.hasCustomName()) continue;
            return i;
        }
        return -1;
    }

    public int getSwappableHotbarSlot() {
        for (int i = 0; i < 9; ++i) {
            int j = (this.selectedSlot + i) % 9;
            if (!this.main.get(j).isEmpty()) continue;
            return j;
        }
        for (int k = 0; k < 9; ++k) {
            int l = (this.selectedSlot + k) % 9;
            if (this.main.get(l).hasEnchantments()) continue;
            return l;
        }
        return this.selectedSlot;
    }

    @Environment(value=EnvType.CLIENT)
    public void scrollInHotbar(double scrollAmount) {
        if (scrollAmount > 0.0) {
            scrollAmount = 1.0;
        }
        if (scrollAmount < 0.0) {
            scrollAmount = -1.0;
        }
        this.selectedSlot = (int)((double)this.selectedSlot - scrollAmount);
        while (this.selectedSlot < 0) {
            this.selectedSlot += 9;
        }
        while (this.selectedSlot >= 9) {
            this.selectedSlot -= 9;
        }
    }

    public int method_29280(Predicate<ItemStack> predicate, int i, Inventory arg) {
        int j = 0;
        boolean bl = i == 0;
        j += Inventories.method_29234(this, predicate, i - j, bl);
        j += Inventories.method_29234(arg, predicate, i - j, bl);
        j += Inventories.method_29235(this.cursorStack, predicate, i - j, bl);
        if (this.cursorStack.isEmpty()) {
            this.cursorStack = ItemStack.EMPTY;
        }
        return j;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }
        if (i == -1) {
            return stack.getCount();
        }
        return this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        int k;
        Item lv = stack.getItem();
        int j = stack.getCount();
        ItemStack lv2 = this.getStack(slot);
        if (lv2.isEmpty()) {
            lv2 = new ItemStack(lv, 0);
            if (stack.hasTag()) {
                lv2.setTag(stack.getTag().copy());
            }
            this.setStack(slot, lv2);
        }
        if ((k = j) > lv2.getMaxCount() - lv2.getCount()) {
            k = lv2.getMaxCount() - lv2.getCount();
        }
        if (k > this.getMaxCountPerStack() - lv2.getCount()) {
            k = this.getMaxCountPerStack() - lv2.getCount();
        }
        if (k == 0) {
            return j;
        }
        lv2.increment(k);
        lv2.setCooldown(5);
        return j -= k;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), stack)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        }
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.canStackAddMore(this.main.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    public void updateItems() {
        for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            for (int i = 0; i < lv.size(); ++i) {
                if (lv.get(i).isEmpty()) continue;
                lv.get(i).inventoryTick(this.player.world, this.player, i, this.selectedSlot == i);
            }
        }
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            if (!stack.isDamaged()) {
                int j;
                do {
                    j = stack.getCount();
                    if (slot == -1) {
                        stack.setCount(this.addStack(stack));
                        continue;
                    }
                    stack.setCount(this.addStack(slot, stack));
                } while (!stack.isEmpty() && stack.getCount() < j);
                if (stack.getCount() == j && this.player.abilities.creativeMode) {
                    stack.setCount(0);
                    return true;
                }
                return stack.getCount() < j;
            }
            if (slot == -1) {
                slot = this.getEmptySlot();
            }
            if (slot >= 0) {
                this.main.set(slot, stack.copy());
                this.main.get(slot).setCooldown(5);
                stack.setCount(0);
                return true;
            }
            if (this.player.abilities.creativeMode) {
                stack.setCount(0);
                return true;
            }
            return false;
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection lv2 = lv.addElement("Item being added");
            lv2.add("Item ID", Item.getRawId(stack.getItem()));
            lv2.add("Item data", stack.getDamage());
            lv2.add("Item name", () -> stack.getName().getString());
            throw new CrashException(lv);
        }
    }

    public void offerOrDrop(World world, ItemStack stack) {
        if (world.isClient) {
            return;
        }
        while (!stack.isEmpty()) {
            int i = this.getOccupiedSlotWithRoomForStack(stack);
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i == -1) {
                this.player.dropItem(stack, false);
                break;
            }
            int j = stack.getMaxCount() - this.getStack(i).getCount();
            if (!this.insertStack(i, stack.split(j))) continue;
            ((ServerPlayerEntity)this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, i, this.getStack(i)));
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            if (slot < lv.size()) {
                list = lv;
                break;
            }
            slot -= lv.size();
        }
        if (list != null && !((ItemStack)list.get(slot)).isEmpty()) {
            return Inventories.splitStack(list, slot, amount);
        }
        return ItemStack.EMPTY;
    }

    public void removeOne(ItemStack stack) {
        block0: for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            for (int i = 0; i < lv.size(); ++i) {
                if (lv.get(i) != stack) continue;
                lv.set(i, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        DefaultedList<ItemStack> lv = null;
        for (DefaultedList<ItemStack> lv2 : this.combinedInventory) {
            if (slot < lv2.size()) {
                lv = lv2;
                break;
            }
            slot -= lv2.size();
        }
        if (lv != null && !((ItemStack)lv.get(slot)).isEmpty()) {
            ItemStack lv3 = lv.get(slot);
            lv.set(slot, ItemStack.EMPTY);
            return lv3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        DefaultedList<ItemStack> lv = null;
        for (DefaultedList<ItemStack> lv2 : this.combinedInventory) {
            if (slot < lv2.size()) {
                lv = lv2;
                break;
            }
            slot -= lv2.size();
        }
        if (lv != null) {
            lv.set(slot, stack);
        }
    }

    public float getBlockBreakingSpeed(BlockState block) {
        return this.main.get(this.selectedSlot).getMiningSpeedMultiplier(block);
    }

    public ListTag serialize(ListTag tag) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) continue;
            CompoundTag lv = new CompoundTag();
            lv.putByte("Slot", (byte)i);
            this.main.get(i).toTag(lv);
            tag.add(lv);
        }
        for (int j = 0; j < this.armor.size(); ++j) {
            if (this.armor.get(j).isEmpty()) continue;
            CompoundTag lv2 = new CompoundTag();
            lv2.putByte("Slot", (byte)(j + 100));
            this.armor.get(j).toTag(lv2);
            tag.add(lv2);
        }
        for (int k = 0; k < this.offHand.size(); ++k) {
            if (this.offHand.get(k).isEmpty()) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putByte("Slot", (byte)(k + 150));
            this.offHand.get(k).toTag(lv3);
            tag.add(lv3);
        }
        return tag;
    }

    public void deserialize(ListTag tag) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag lv = tag.getCompound(i);
            int j = lv.getByte("Slot") & 0xFF;
            ItemStack lv2 = ItemStack.fromTag(lv);
            if (lv2.isEmpty()) continue;
            if (j >= 0 && j < this.main.size()) {
                this.main.set(j, lv2);
                continue;
            }
            if (j >= 100 && j < this.armor.size() + 100) {
                this.armor.set(j - 100, lv2);
                continue;
            }
            if (j < 150 || j >= this.offHand.size() + 150) continue;
            this.offHand.set(j - 150, lv2);
        }
    }

    @Override
    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack lv : this.main) {
            if (lv.isEmpty()) continue;
            return false;
        }
        for (ItemStack lv2 : this.armor) {
            if (lv2.isEmpty()) continue;
            return false;
        }
        for (ItemStack lv3 : this.offHand) {
            if (lv3.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            if (slot < lv.size()) {
                list = lv;
                break;
            }
            slot -= lv.size();
        }
        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(slot);
    }

    @Override
    public Text getName() {
        return new TranslatableText("container.inventory");
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }

    public void damageArmor(DamageSource damageSource, float f) {
        if (f <= 0.0f) {
            return;
        }
        if ((f /= 4.0f) < 1.0f) {
            f = 1.0f;
        }
        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack lv = this.armor.get(i);
            if (damageSource.isFire() && lv.getItem().isFireproof() || !(lv.getItem() instanceof ArmorItem)) continue;
            int j = i;
            lv.damage((int)f, this.player, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, j)));
        }
    }

    public void dropAll() {
        for (List list : this.combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack lv = (ItemStack)list.get(i);
                if (lv.isEmpty()) continue;
                this.player.dropItem(lv, true, false);
                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.changeCount;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChangeCount() {
        return this.changeCount;
    }

    public void setCursorStack(ItemStack stack) {
        this.cursorStack = stack;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.player.removed) {
            return false;
        }
        return !(player.squaredDistanceTo(this.player) > 64.0);
    }

    public boolean contains(ItemStack stack) {
        for (List list : this.combinedInventory) {
            for (ItemStack lv : list) {
                if (lv.isEmpty() || !lv.isItemEqualIgnoreDamage(stack)) continue;
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean contains(Tag<Item> tag) {
        for (List list : this.combinedInventory) {
            for (ItemStack lv : list) {
                if (lv.isEmpty() || !tag.contains(lv.getItem())) continue;
                return true;
            }
        }
        return false;
    }

    public void clone(PlayerInventory other) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, other.getStack(i));
        }
        this.selectedSlot = other.selectedSlot;
    }

    @Override
    public void clear() {
        for (List list : this.combinedInventory) {
            list.clear();
        }
    }

    public void populateRecipeFinder(RecipeFinder finder) {
        for (ItemStack lv : this.main) {
            finder.addNormalItem(lv);
        }
    }
}

