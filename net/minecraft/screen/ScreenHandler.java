/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class ScreenHandler {
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    public final List<Slot> slots = Lists.newArrayList();
    private final List<Property> properties = Lists.newArrayList();
    @Nullable
    private final ScreenHandlerType<?> type;
    public final int syncId;
    @Environment(value=EnvType.CLIENT)
    private short actionId;
    private int quickCraftStage = -1;
    private int quickCraftButton;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();
    private final List<ScreenHandlerListener> listeners = Lists.newArrayList();
    private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

    protected ScreenHandler(@Nullable ScreenHandlerType<?> arg, int i) {
        this.type = arg;
        this.syncId = i;
    }

    protected static boolean canUse(ScreenHandlerContext arg, PlayerEntity arg2, Block arg32) {
        return arg.run((arg3, arg4) -> {
            if (!arg3.getBlockState((BlockPos)arg4).isOf(arg32)) {
                return false;
            }
            return arg2.squaredDistanceTo((double)arg4.getX() + 0.5, (double)arg4.getY() + 0.5, (double)arg4.getZ() + 0.5) <= 64.0;
        }, true);
    }

    public ScreenHandlerType<?> getType() {
        if (this.type == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.type;
    }

    protected static void checkSize(Inventory arg, int i) {
        int j = arg.size();
        if (j < i) {
            throw new IllegalArgumentException("Container size " + j + " is smaller than expected " + i);
        }
    }

    protected static void checkDataCount(PropertyDelegate arg, int i) {
        int j = arg.size();
        if (j < i) {
            throw new IllegalArgumentException("Container data count " + j + " is smaller than expected " + i);
        }
    }

    protected Slot addSlot(Slot arg) {
        arg.id = this.slots.size();
        this.slots.add(arg);
        this.trackedStacks.add(ItemStack.EMPTY);
        return arg;
    }

    protected Property addProperty(Property arg) {
        this.properties.add(arg);
        return arg;
    }

    protected void addProperties(PropertyDelegate arg) {
        for (int i = 0; i < arg.size(); ++i) {
            this.addProperty(Property.create(arg, i));
        }
    }

    public void addListener(ScreenHandlerListener arg) {
        if (this.listeners.contains(arg)) {
            return;
        }
        this.listeners.add(arg);
        arg.onHandlerRegistered(this, this.getStacks());
        this.sendContentUpdates();
    }

    @Environment(value=EnvType.CLIENT)
    public void removeListener(ScreenHandlerListener arg) {
        this.listeners.remove(arg);
    }

    public DefaultedList<ItemStack> getStacks() {
        DefaultedList<ItemStack> lv = DefaultedList.of();
        for (int i = 0; i < this.slots.size(); ++i) {
            lv.add(this.slots.get(i).getStack());
        }
        return lv;
    }

    public void sendContentUpdates() {
        for (int i = 0; i < this.slots.size(); ++i) {
            ItemStack lv = this.slots.get(i).getStack();
            ItemStack lv2 = this.trackedStacks.get(i);
            if (ItemStack.areEqual(lv2, lv)) continue;
            ItemStack lv3 = lv.copy();
            this.trackedStacks.set(i, lv3);
            for (ScreenHandlerListener lv4 : this.listeners) {
                lv4.onSlotUpdate(this, i, lv3);
            }
        }
        for (int j = 0; j < this.properties.size(); ++j) {
            Property lv5 = this.properties.get(j);
            if (!lv5.hasChanged()) continue;
            for (ScreenHandlerListener lv6 : this.listeners) {
                lv6.onPropertyUpdate(this, j, lv5.get());
            }
        }
    }

    public boolean onButtonClick(PlayerEntity arg, int i) {
        return false;
    }

    public Slot getSlot(int i) {
        return this.slots.get(i);
    }

    public ItemStack transferSlot(PlayerEntity arg, int i) {
        Slot lv = this.slots.get(i);
        if (lv != null) {
            return lv.getStack();
        }
        return ItemStack.EMPTY;
    }

    public ItemStack onSlotClick(int i, int j, SlotActionType arg, PlayerEntity arg2) {
        try {
            return this.method_30010(i, j, arg, arg2);
        }
        catch (Exception exception) {
            CrashReport lv = CrashReport.create(exception, "Container click");
            CrashReportSection lv2 = lv.addElement("Click info");
            lv2.add("Menu Type", () -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>");
            lv2.add("Menu Class", () -> this.getClass().getCanonicalName());
            lv2.add("Slot Count", this.slots.size());
            lv2.add("Slot", i);
            lv2.add("Button", j);
            lv2.add("Type", (Object)arg);
            throw new CrashException(lv);
        }
    }

    private ItemStack method_30010(int i, int j, SlotActionType arg, PlayerEntity arg2) {
        ItemStack lv = ItemStack.EMPTY;
        PlayerInventory lv2 = arg2.inventory;
        if (arg == SlotActionType.QUICK_CRAFT) {
            int k = this.quickCraftButton;
            this.quickCraftButton = ScreenHandler.unpackQuickCraftStage(j);
            if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (lv2.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = ScreenHandler.unpackQuickCraftButton(j);
                if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftStage, arg2)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot lv3 = this.slots.get(i);
                ItemStack lv4 = lv2.getCursorStack();
                if (lv3 != null && ScreenHandler.canInsertItemIntoSlot(lv3, lv4, true) && lv3.canInsert(lv4) && (this.quickCraftStage == 2 || lv4.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(lv3)) {
                    this.quickCraftSlots.add(lv3);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    ItemStack lv5 = lv2.getCursorStack().copy();
                    int l = lv2.getCursorStack().getCount();
                    for (Slot lv6 : this.quickCraftSlots) {
                        ItemStack lv7 = lv2.getCursorStack();
                        if (lv6 == null || !ScreenHandler.canInsertItemIntoSlot(lv6, lv7, true) || !lv6.canInsert(lv7) || this.quickCraftStage != 2 && lv7.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(lv6)) continue;
                        ItemStack lv8 = lv5.copy();
                        int m = lv6.hasStack() ? lv6.getStack().getCount() : 0;
                        ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, lv8, m);
                        int n = Math.min(lv8.getMaxCount(), lv6.getMaxStackAmount(lv8));
                        if (lv8.getCount() > n) {
                            lv8.setCount(n);
                        }
                        l -= lv8.getCount() - m;
                        lv6.setStack(lv8);
                    }
                    lv5.setCount(l);
                    lv2.setCursorStack(lv5);
                }
                this.endQuickCraft();
            } else {
                this.endQuickCraft();
            }
        } else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        } else if (!(arg != SlotActionType.PICKUP && arg != SlotActionType.QUICK_MOVE || j != 0 && j != 1)) {
            if (i == -999) {
                if (!lv2.getCursorStack().isEmpty()) {
                    if (j == 0) {
                        arg2.dropItem(lv2.getCursorStack(), true);
                        lv2.setCursorStack(ItemStack.EMPTY);
                    }
                    if (j == 1) {
                        arg2.dropItem(lv2.getCursorStack().split(1), true);
                    }
                }
            } else if (arg == SlotActionType.QUICK_MOVE) {
                if (i < 0) {
                    return ItemStack.EMPTY;
                }
                Slot lv9 = this.slots.get(i);
                if (lv9 == null || !lv9.canTakeItems(arg2)) {
                    return ItemStack.EMPTY;
                }
                ItemStack lv10 = this.transferSlot(arg2, i);
                while (!lv10.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(lv9.getStack(), lv10)) {
                    lv = lv10.copy();
                    lv10 = this.transferSlot(arg2, i);
                }
            } else {
                if (i < 0) {
                    return ItemStack.EMPTY;
                }
                Slot lv11 = this.slots.get(i);
                if (lv11 != null) {
                    ItemStack lv12 = lv11.getStack();
                    ItemStack lv13 = lv2.getCursorStack();
                    if (!lv12.isEmpty()) {
                        lv = lv12.copy();
                    }
                    if (lv12.isEmpty()) {
                        if (!lv13.isEmpty() && lv11.canInsert(lv13)) {
                            int o;
                            int n = o = j == 0 ? lv13.getCount() : 1;
                            if (o > lv11.getMaxStackAmount(lv13)) {
                                o = lv11.getMaxStackAmount(lv13);
                            }
                            lv11.setStack(lv13.split(o));
                        }
                    } else if (lv11.canTakeItems(arg2)) {
                        int r;
                        if (lv13.isEmpty()) {
                            if (lv12.isEmpty()) {
                                lv11.setStack(ItemStack.EMPTY);
                                lv2.setCursorStack(ItemStack.EMPTY);
                            } else {
                                int p = j == 0 ? lv12.getCount() : (lv12.getCount() + 1) / 2;
                                lv2.setCursorStack(lv11.takeStack(p));
                                if (lv12.isEmpty()) {
                                    lv11.setStack(ItemStack.EMPTY);
                                }
                                lv11.onTakeItem(arg2, lv2.getCursorStack());
                            }
                        } else if (lv11.canInsert(lv13)) {
                            if (ScreenHandler.canStacksCombine(lv12, lv13)) {
                                int q;
                                int n = q = j == 0 ? lv13.getCount() : 1;
                                if (q > lv11.getMaxStackAmount(lv13) - lv12.getCount()) {
                                    q = lv11.getMaxStackAmount(lv13) - lv12.getCount();
                                }
                                if (q > lv13.getMaxCount() - lv12.getCount()) {
                                    q = lv13.getMaxCount() - lv12.getCount();
                                }
                                lv13.decrement(q);
                                lv12.increment(q);
                            } else if (lv13.getCount() <= lv11.getMaxStackAmount(lv13)) {
                                lv11.setStack(lv13);
                                lv2.setCursorStack(lv12);
                            }
                        } else if (lv13.getMaxCount() > 1 && ScreenHandler.canStacksCombine(lv12, lv13) && !lv12.isEmpty() && (r = lv12.getCount()) + lv13.getCount() <= lv13.getMaxCount()) {
                            lv13.increment(r);
                            lv12 = lv11.takeStack(r);
                            if (lv12.isEmpty()) {
                                lv11.setStack(ItemStack.EMPTY);
                            }
                            lv11.onTakeItem(arg2, lv2.getCursorStack());
                        }
                    }
                    lv11.markDirty();
                }
            }
        } else if (arg == SlotActionType.SWAP) {
            Slot lv14 = this.slots.get(i);
            ItemStack lv15 = lv2.getStack(j);
            ItemStack lv16 = lv14.getStack();
            if (!lv15.isEmpty() || !lv16.isEmpty()) {
                if (lv15.isEmpty()) {
                    if (lv14.canTakeItems(arg2)) {
                        lv2.setStack(j, lv16);
                        lv14.onTake(lv16.getCount());
                        lv14.setStack(ItemStack.EMPTY);
                        lv14.onTakeItem(arg2, lv16);
                    }
                } else if (lv16.isEmpty()) {
                    if (lv14.canInsert(lv15)) {
                        int s = lv14.getMaxStackAmount(lv15);
                        if (lv15.getCount() > s) {
                            lv14.setStack(lv15.split(s));
                        } else {
                            lv14.setStack(lv15);
                            lv2.setStack(j, ItemStack.EMPTY);
                        }
                    }
                } else if (lv14.canTakeItems(arg2) && lv14.canInsert(lv15)) {
                    int t = lv14.getMaxStackAmount(lv15);
                    if (lv15.getCount() > t) {
                        lv14.setStack(lv15.split(t));
                        lv14.onTakeItem(arg2, lv16);
                        if (!lv2.insertStack(lv16)) {
                            arg2.dropItem(lv16, true);
                        }
                    } else {
                        lv14.setStack(lv15);
                        lv2.setStack(j, lv16);
                        lv14.onTakeItem(arg2, lv16);
                    }
                }
            }
        } else if (arg == SlotActionType.CLONE && arg2.abilities.creativeMode && lv2.getCursorStack().isEmpty() && i >= 0) {
            Slot lv17 = this.slots.get(i);
            if (lv17 != null && lv17.hasStack()) {
                ItemStack lv18 = lv17.getStack().copy();
                lv18.setCount(lv18.getMaxCount());
                lv2.setCursorStack(lv18);
            }
        } else if (arg == SlotActionType.THROW && lv2.getCursorStack().isEmpty() && i >= 0) {
            Slot lv19 = this.slots.get(i);
            if (lv19 != null && lv19.hasStack() && lv19.canTakeItems(arg2)) {
                ItemStack lv20 = lv19.takeStack(j == 0 ? 1 : lv19.getStack().getCount());
                lv19.onTakeItem(arg2, lv20);
                arg2.dropItem(lv20, true);
            }
        } else if (arg == SlotActionType.PICKUP_ALL && i >= 0) {
            Slot lv21 = this.slots.get(i);
            ItemStack lv22 = lv2.getCursorStack();
            if (!(lv22.isEmpty() || lv21 != null && lv21.hasStack() && lv21.canTakeItems(arg2))) {
                int u = j == 0 ? 0 : this.slots.size() - 1;
                int v = j == 0 ? 1 : -1;
                for (int w = 0; w < 2; ++w) {
                    for (int x = u; x >= 0 && x < this.slots.size() && lv22.getCount() < lv22.getMaxCount(); x += v) {
                        Slot lv23 = this.slots.get(x);
                        if (!lv23.hasStack() || !ScreenHandler.canInsertItemIntoSlot(lv23, lv22, true) || !lv23.canTakeItems(arg2) || !this.canInsertIntoSlot(lv22, lv23)) continue;
                        ItemStack lv24 = lv23.getStack();
                        if (w == 0 && lv24.getCount() == lv24.getMaxCount()) continue;
                        int y = Math.min(lv22.getMaxCount() - lv22.getCount(), lv24.getCount());
                        ItemStack lv25 = lv23.takeStack(y);
                        lv22.increment(y);
                        if (lv25.isEmpty()) {
                            lv23.setStack(ItemStack.EMPTY);
                        }
                        lv23.onTakeItem(arg2, lv25);
                    }
                }
            }
            this.sendContentUpdates();
        }
        return lv;
    }

    public static boolean canStacksCombine(ItemStack arg, ItemStack arg2) {
        return arg.getItem() == arg2.getItem() && ItemStack.areTagsEqual(arg, arg2);
    }

    public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
        return true;
    }

    public void close(PlayerEntity arg) {
        PlayerInventory lv = arg.inventory;
        if (!lv.getCursorStack().isEmpty()) {
            arg.dropItem(lv.getCursorStack(), false);
            lv.setCursorStack(ItemStack.EMPTY);
        }
    }

    protected void dropInventory(PlayerEntity arg, World arg2, Inventory arg3) {
        if (!arg.isAlive() || arg instanceof ServerPlayerEntity && ((ServerPlayerEntity)arg).isDisconnected()) {
            for (int i = 0; i < arg3.size(); ++i) {
                arg.dropItem(arg3.removeStack(i), false);
            }
            return;
        }
        for (int j = 0; j < arg3.size(); ++j) {
            arg.inventory.offerOrDrop(arg2, arg3.removeStack(j));
        }
    }

    public void onContentChanged(Inventory arg) {
        this.sendContentUpdates();
    }

    public void setStackInSlot(int i, ItemStack arg) {
        this.getSlot(i).setStack(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateSlotStacks(List<ItemStack> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.getSlot(i).setStack(list.get(i));
        }
    }

    public void setProperty(int i, int j) {
        this.properties.get(i).set(j);
    }

    @Environment(value=EnvType.CLIENT)
    public short getNextActionId(PlayerInventory arg) {
        this.actionId = (short)(this.actionId + 1);
        return this.actionId;
    }

    public boolean isNotRestricted(PlayerEntity arg) {
        return !this.restrictedPlayers.contains(arg);
    }

    public void setPlayerRestriction(PlayerEntity arg, boolean bl) {
        if (bl) {
            this.restrictedPlayers.remove(arg);
        } else {
            this.restrictedPlayers.add(arg);
        }
    }

    public abstract boolean canUse(PlayerEntity var1);

    protected boolean insertItem(ItemStack arg, int i, int j, boolean bl) {
        boolean bl2 = false;
        int k = i;
        if (bl) {
            k = j - 1;
        }
        if (arg.isStackable()) {
            while (!arg.isEmpty() && (bl ? k >= i : k < j)) {
                Slot lv = this.slots.get(k);
                ItemStack lv2 = lv.getStack();
                if (!lv2.isEmpty() && ScreenHandler.canStacksCombine(arg, lv2)) {
                    int l = lv2.getCount() + arg.getCount();
                    if (l <= arg.getMaxCount()) {
                        arg.setCount(0);
                        lv2.setCount(l);
                        lv.markDirty();
                        bl2 = true;
                    } else if (lv2.getCount() < arg.getMaxCount()) {
                        arg.decrement(arg.getMaxCount() - lv2.getCount());
                        lv2.setCount(arg.getMaxCount());
                        lv.markDirty();
                        bl2 = true;
                    }
                }
                if (bl) {
                    --k;
                    continue;
                }
                ++k;
            }
        }
        if (!arg.isEmpty()) {
            k = bl ? j - 1 : i;
            while (bl ? k >= i : k < j) {
                Slot lv3 = this.slots.get(k);
                ItemStack lv4 = lv3.getStack();
                if (lv4.isEmpty() && lv3.canInsert(arg)) {
                    if (arg.getCount() > lv3.getMaxStackAmount()) {
                        lv3.setStack(arg.split(lv3.getMaxStackAmount()));
                    } else {
                        lv3.setStack(arg.split(arg.getCount()));
                    }
                    lv3.markDirty();
                    bl2 = true;
                    break;
                }
                if (bl) {
                    --k;
                    continue;
                }
                ++k;
            }
        }
        return bl2;
    }

    public static int unpackQuickCraftButton(int i) {
        return i >> 2 & 3;
    }

    public static int unpackQuickCraftStage(int i) {
        return i & 3;
    }

    @Environment(value=EnvType.CLIENT)
    public static int packQuickCraftData(int i, int j) {
        return i & 3 | (j & 3) << 2;
    }

    public static boolean shouldQuickCraftContinue(int i, PlayerEntity arg) {
        if (i == 0) {
            return true;
        }
        if (i == 1) {
            return true;
        }
        return i == 2 && arg.abilities.creativeMode;
    }

    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot arg, ItemStack arg2, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = arg == null || !arg.hasStack();
        if (!bl2 && arg2.isItemEqualIgnoreDamage(arg.getStack()) && ItemStack.areTagsEqual(arg.getStack(), arg2)) {
            return arg.getStack().getCount() + (bl ? 0 : arg2.getCount()) <= arg2.getMaxCount();
        }
        return bl2;
    }

    public static void calculateStackSize(Set<Slot> set, int i, ItemStack arg, int j) {
        switch (i) {
            case 0: {
                arg.setCount(MathHelper.floor((float)arg.getCount() / (float)set.size()));
                break;
            }
            case 1: {
                arg.setCount(1);
                break;
            }
            case 2: {
                arg.setCount(arg.getItem().getMaxCount());
            }
        }
        arg.increment(j);
    }

    public boolean canInsertIntoSlot(Slot arg) {
        return true;
    }

    public static int calculateComparatorOutput(@Nullable BlockEntity arg) {
        if (arg instanceof Inventory) {
            return ScreenHandler.calculateComparatorOutput((Inventory)((Object)arg));
        }
        return 0;
    }

    public static int calculateComparatorOutput(@Nullable Inventory arg) {
        if (arg == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < arg.size(); ++j) {
            ItemStack lv = arg.getStack(j);
            if (lv.isEmpty()) continue;
            f += (float)lv.getCount() / (float)Math.min(arg.getMaxCountPerStack(), lv.getMaxCount());
            ++i;
        }
        return MathHelper.floor((f /= (float)arg.size()) * 14.0f) + (i > 0 ? 1 : 0);
    }
}

