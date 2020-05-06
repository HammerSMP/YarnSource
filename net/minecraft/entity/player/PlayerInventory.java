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

    public PlayerInventory(PlayerEntity arg) {
        this.player = arg;
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

    private boolean canStackAddMore(ItemStack arg, ItemStack arg2) {
        return !arg.isEmpty() && this.areItemsEqual(arg, arg2) && arg.isStackable() && arg.getCount() < arg.getMaxCount() && arg.getCount() < this.getMaxCountPerStack();
    }

    private boolean areItemsEqual(ItemStack arg, ItemStack arg2) {
        return arg.getItem() == arg2.getItem() && ItemStack.areTagsEqual(arg, arg2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    public void addPickBlock(ItemStack arg) {
        int i = this.getSlotWithStack(arg);
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
            this.main.set(this.selectedSlot, arg);
        } else {
            this.swapSlotWithHotbar(i);
        }
    }

    public void swapSlotWithHotbar(int i) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        ItemStack lv = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(i));
        this.main.set(i, lv);
    }

    public static boolean isValidHotbarIndex(int i) {
        return i >= 0 && i < 9;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSlotWithStack(ItemStack arg) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty() || !this.areItemsEqual(arg, this.main.get(i))) continue;
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
    public void scrollInHotbar(double d) {
        if (d > 0.0) {
            d = 1.0;
        }
        if (d < 0.0) {
            d = -1.0;
        }
        this.selectedSlot = (int)((double)this.selectedSlot - d);
        while (this.selectedSlot < 0) {
            this.selectedSlot += 9;
        }
        while (this.selectedSlot >= 9) {
            this.selectedSlot -= 9;
        }
    }

    public int method_7369(Predicate<ItemStack> predicate, int i) {
        int j = 0;
        for (int k = 0; k < this.size(); ++k) {
            ItemStack lv = this.getStack(k);
            if (lv.isEmpty() || !predicate.test(lv)) continue;
            int l = i <= 0 ? lv.getCount() : Math.min(i - j, lv.getCount());
            j += l;
            if (i == 0) continue;
            lv.decrement(l);
            if (lv.isEmpty()) {
                this.setStack(k, ItemStack.EMPTY);
            }
            if (i <= 0 || j < i) continue;
            return j;
        }
        if (!this.cursorStack.isEmpty() && predicate.test(this.cursorStack)) {
            int m = i <= 0 ? this.cursorStack.getCount() : Math.min(i - j, this.cursorStack.getCount());
            j += m;
            if (i != 0) {
                this.cursorStack.decrement(m);
                if (this.cursorStack.isEmpty()) {
                    this.cursorStack = ItemStack.EMPTY;
                }
                if (i > 0 && j >= i) {
                    return j;
                }
            }
        }
        return j;
    }

    private int addStack(ItemStack arg) {
        int i = this.getOccupiedSlotWithRoomForStack(arg);
        if (i == -1) {
            i = this.getEmptySlot();
        }
        if (i == -1) {
            return arg.getCount();
        }
        return this.addStack(i, arg);
    }

    private int addStack(int i, ItemStack arg) {
        int k;
        Item lv = arg.getItem();
        int j = arg.getCount();
        ItemStack lv2 = this.getStack(i);
        if (lv2.isEmpty()) {
            lv2 = new ItemStack(lv, 0);
            if (arg.hasTag()) {
                lv2.setTag(arg.getTag().copy());
            }
            this.setStack(i, lv2);
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

    public int getOccupiedSlotWithRoomForStack(ItemStack arg) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), arg)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getStack(40), arg)) {
            return 40;
        }
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.canStackAddMore(this.main.get(i), arg)) continue;
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

    public boolean insertStack(ItemStack arg) {
        return this.insertStack(-1, arg);
    }

    public boolean insertStack(int i, ItemStack arg) {
        if (arg.isEmpty()) {
            return false;
        }
        try {
            if (!arg.isDamaged()) {
                int j;
                do {
                    j = arg.getCount();
                    if (i == -1) {
                        arg.setCount(this.addStack(arg));
                        continue;
                    }
                    arg.setCount(this.addStack(i, arg));
                } while (!arg.isEmpty() && arg.getCount() < j);
                if (arg.getCount() == j && this.player.abilities.creativeMode) {
                    arg.setCount(0);
                    return true;
                }
                return arg.getCount() < j;
            }
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i >= 0) {
                this.main.set(i, arg.copy());
                this.main.get(i).setCooldown(5);
                arg.setCount(0);
                return true;
            }
            if (this.player.abilities.creativeMode) {
                arg.setCount(0);
                return true;
            }
            return false;
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection lv2 = lv.addElement("Item being added");
            lv2.add("Item ID", Item.getRawId(arg.getItem()));
            lv2.add("Item data", arg.getDamage());
            lv2.add("Item name", () -> arg.getName().getString());
            throw new CrashException(lv);
        }
    }

    public void offerOrDrop(World arg, ItemStack arg2) {
        if (arg.isClient) {
            return;
        }
        while (!arg2.isEmpty()) {
            int i = this.getOccupiedSlotWithRoomForStack(arg2);
            if (i == -1) {
                i = this.getEmptySlot();
            }
            if (i == -1) {
                this.player.dropItem(arg2, false);
                break;
            }
            int j = arg2.getMaxCount() - this.getStack(i).getCount();
            if (!this.insertStack(i, arg2.split(j))) continue;
            ((ServerPlayerEntity)this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, i, this.getStack(i)));
        }
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            if (i < lv.size()) {
                list = lv;
                break;
            }
            i -= lv.size();
        }
        if (list != null && !((ItemStack)list.get(i)).isEmpty()) {
            return Inventories.splitStack(list, i, j);
        }
        return ItemStack.EMPTY;
    }

    public void removeOne(ItemStack arg) {
        block0: for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            for (int i = 0; i < lv.size(); ++i) {
                if (lv.get(i) != arg) continue;
                lv.set(i, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeStack(int i) {
        DefaultedList<ItemStack> lv = null;
        for (DefaultedList<ItemStack> lv2 : this.combinedInventory) {
            if (i < lv2.size()) {
                lv = lv2;
                break;
            }
            i -= lv2.size();
        }
        if (lv != null && !((ItemStack)lv.get(i)).isEmpty()) {
            ItemStack lv3 = lv.get(i);
            lv.set(i, ItemStack.EMPTY);
            return lv3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        DefaultedList<ItemStack> lv = null;
        for (DefaultedList<ItemStack> lv2 : this.combinedInventory) {
            if (i < lv2.size()) {
                lv = lv2;
                break;
            }
            i -= lv2.size();
        }
        if (lv != null) {
            lv.set(i, arg);
        }
    }

    public float getBlockBreakingSpeed(BlockState arg) {
        return this.main.get(this.selectedSlot).getMiningSpeedMultiplier(arg);
    }

    public ListTag serialize(ListTag arg) {
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) continue;
            CompoundTag lv = new CompoundTag();
            lv.putByte("Slot", (byte)i);
            this.main.get(i).toTag(lv);
            arg.add(lv);
        }
        for (int j = 0; j < this.armor.size(); ++j) {
            if (this.armor.get(j).isEmpty()) continue;
            CompoundTag lv2 = new CompoundTag();
            lv2.putByte("Slot", (byte)(j + 100));
            this.armor.get(j).toTag(lv2);
            arg.add(lv2);
        }
        for (int k = 0; k < this.offHand.size(); ++k) {
            if (this.offHand.get(k).isEmpty()) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putByte("Slot", (byte)(k + 150));
            this.offHand.get(k).toTag(lv3);
            arg.add(lv3);
        }
        return arg;
    }

    public void deserialize(ListTag arg) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < arg.size(); ++i) {
            CompoundTag lv = arg.getCompound(i);
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
    public ItemStack getStack(int i) {
        DefaultedList<ItemStack> list = null;
        for (DefaultedList<ItemStack> lv : this.combinedInventory) {
            if (i < lv.size()) {
                list = lv;
                break;
            }
            i -= lv.size();
        }
        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(i);
    }

    @Override
    public Text getName() {
        return new TranslatableText("container.inventory");
    }

    public boolean isUsingEffectiveTool(BlockState arg) {
        return this.getStack(this.selectedSlot).isEffectiveOn(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getArmorStack(int i) {
        return this.armor.get(i);
    }

    public void damageArmor(DamageSource arg2, float f) {
        if (f <= 0.0f) {
            return;
        }
        if ((f /= 4.0f) < 1.0f) {
            f = 1.0f;
        }
        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack lv = this.armor.get(i);
            if (arg2.isFire() && lv.getItem().isFireproof() || !(lv.getItem() instanceof ArmorItem)) continue;
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

    public void setCursorStack(ItemStack arg) {
        this.cursorStack = arg;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        if (this.player.removed) {
            return false;
        }
        return !(arg.squaredDistanceTo(this.player) > 64.0);
    }

    public boolean contains(ItemStack arg) {
        for (List list : this.combinedInventory) {
            for (ItemStack lv : list) {
                if (lv.isEmpty() || !lv.isItemEqualIgnoreDamage(arg)) continue;
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean contains(Tag<Item> arg) {
        for (List list : this.combinedInventory) {
            for (ItemStack lv : list) {
                if (lv.isEmpty() || !arg.contains(lv.getItem())) continue;
                return true;
            }
        }
        return false;
    }

    public void clone(PlayerInventory arg) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, arg.getStack(i));
        }
        this.selectedSlot = arg.selectedSlot;
    }

    @Override
    public void clear() {
        for (List list : this.combinedInventory) {
            list.clear();
        }
    }

    public void populateRecipeFinder(RecipeFinder arg) {
        for (ItemStack lv : this.main) {
            arg.addNormalItem(lv);
        }
    }
}

