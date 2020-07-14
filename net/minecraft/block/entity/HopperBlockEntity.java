/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class HopperBlockEntity
extends LootableContainerBlockEntity
implements Hopper,
Tickable {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long lastTickTime;

    public HopperBlockEntity() {
        super(BlockEntityType.HOPPER);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
        this.transferCooldown = tag.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        tag.putInt("TransferCooldown", this.transferCooldown);
        return tag;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.hopper");
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        --this.transferCooldown;
        this.lastTickTime = this.world.getTime();
        if (!this.needsCooldown()) {
            this.setCooldown(0);
            this.insertAndExtract(() -> HopperBlockEntity.extract(this));
        }
    }

    private boolean insertAndExtract(Supplier<Boolean> extractMethod) {
        if (this.world == null || this.world.isClient) {
            return false;
        }
        if (!this.needsCooldown() && this.getCachedState().get(HopperBlock.ENABLED).booleanValue()) {
            boolean bl = false;
            if (!this.isEmpty()) {
                bl = this.insert();
            }
            if (!this.isFull()) {
                bl |= extractMethod.get().booleanValue();
            }
            if (bl) {
                this.setCooldown(8);
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean isFull() {
        for (ItemStack lv : this.inventory) {
            if (!lv.isEmpty() && lv.getCount() == lv.getMaxCount()) continue;
            return false;
        }
        return true;
    }

    private boolean insert() {
        Inventory lv = this.getOutputInventory();
        if (lv == null) {
            return false;
        }
        Direction lv2 = this.getCachedState().get(HopperBlock.FACING).getOpposite();
        if (this.isInventoryFull(lv, lv2)) {
            return false;
        }
        for (int i = 0; i < this.size(); ++i) {
            if (this.getStack(i).isEmpty()) continue;
            ItemStack lv3 = this.getStack(i).copy();
            ItemStack lv4 = HopperBlockEntity.transfer(this, lv, this.removeStack(i, 1), lv2);
            if (lv4.isEmpty()) {
                lv.markDirty();
                return true;
            }
            this.setStack(i, lv3);
        }
        return false;
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory)inventory).getAvailableSlots(side));
        }
        return IntStream.range(0, inventory.size());
    }

    private boolean isInventoryFull(Inventory inv, Direction arg2) {
        return HopperBlockEntity.getAvailableSlots(inv, arg2).allMatch(i -> {
            ItemStack lv = inv.getStack(i);
            return lv.getCount() >= lv.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return HopperBlockEntity.getAvailableSlots(inv, facing).allMatch(i -> inv.getStack(i).isEmpty());
    }

    public static boolean extract(Hopper hopper) {
        Inventory lv = HopperBlockEntity.getInputInventory(hopper);
        if (lv != null) {
            Direction lv2 = Direction.DOWN;
            if (HopperBlockEntity.isInventoryEmpty(lv, lv2)) {
                return false;
            }
            return HopperBlockEntity.getAvailableSlots(lv, lv2).anyMatch(i -> HopperBlockEntity.extract(hopper, lv, i, lv2));
        }
        for (ItemEntity lv3 : HopperBlockEntity.getInputItemEntities(hopper)) {
            if (!HopperBlockEntity.extract(hopper, lv3)) continue;
            return true;
        }
        return false;
    }

    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        ItemStack lv = inventory.getStack(slot);
        if (!lv.isEmpty() && HopperBlockEntity.canExtract(inventory, lv, slot, side)) {
            ItemStack lv2 = lv.copy();
            ItemStack lv3 = HopperBlockEntity.transfer(inventory, hopper, inventory.removeStack(slot, 1), null);
            if (lv3.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setStack(slot, lv2);
        }
        return false;
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack lv = itemEntity.getStack().copy();
        ItemStack lv2 = HopperBlockEntity.transfer(null, inventory, lv, null);
        if (lv2.isEmpty()) {
            bl = true;
            itemEntity.remove();
        } else {
            itemEntity.setStack(lv2);
        }
        return bl;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        if (to instanceof SidedInventory && side != null) {
            SidedInventory lv = (SidedInventory)to;
            int[] is = lv.getAvailableSlots(side);
            for (int i = 0; i < is.length && !stack.isEmpty(); ++i) {
                stack = HopperBlockEntity.transfer(from, to, stack, is[i], side);
            }
        } else {
            int j = to.size();
            for (int k = 0; k < j && !stack.isEmpty(); ++k) {
                stack = HopperBlockEntity.transfer(from, to, stack, k, side);
            }
        }
        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
    }

    private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction arg4) {
        ItemStack lv = to.getStack(slot);
        if (HopperBlockEntity.canInsert(to, stack, slot, arg4)) {
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if (lv.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (HopperBlockEntity.canMergeItems(lv, stack)) {
                int j = stack.getMaxCount() - lv.getCount();
                int k = Math.min(stack.getCount(), j);
                stack.decrement(k);
                lv.increment(k);
                boolean bl3 = bl = k > 0;
            }
            if (bl) {
                HopperBlockEntity lv2;
                if (bl2 && to instanceof HopperBlockEntity && !(lv2 = (HopperBlockEntity)to).isDisabled()) {
                    int l = 0;
                    if (from instanceof HopperBlockEntity) {
                        HopperBlockEntity lv3 = (HopperBlockEntity)from;
                        if (lv2.lastTickTime >= lv3.lastTickTime) {
                            l = 1;
                        }
                    }
                    lv2.setCooldown(8 - l);
                }
                to.markDirty();
            }
        }
        return stack;
    }

    @Nullable
    private Inventory getOutputInventory() {
        Direction lv = this.getCachedState().get(HopperBlock.FACING);
        return HopperBlockEntity.getInventoryAt(this.getWorld(), this.pos.offset(lv));
    }

    @Nullable
    public static Inventory getInputInventory(Hopper hopper) {
        return HopperBlockEntity.getInventoryAt(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(Hopper arg) {
        return arg.getInputAreaShape().getBoundingBoxes().stream().flatMap(arg2 -> arg.getWorld().getEntities(ItemEntity.class, arg2.offset(arg.getHopperX() - 0.5, arg.getHopperY() - 0.5, arg.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static Inventory getInventoryAt(World arg, BlockPos arg2) {
        return HopperBlockEntity.getInventoryAt(arg, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, double x, double y, double z) {
        List<Entity> list;
        BlockEntity lv5;
        Inventory lv = null;
        BlockPos lv2 = new BlockPos(x, y, z);
        BlockState lv3 = world.getBlockState(lv2);
        Block lv4 = lv3.getBlock();
        if (lv4 instanceof InventoryProvider) {
            lv = ((InventoryProvider)((Object)lv4)).getInventory(lv3, world, lv2);
        } else if (lv4.hasBlockEntity() && (lv5 = world.getBlockEntity(lv2)) instanceof Inventory && (lv = (Inventory)((Object)lv5)) instanceof ChestBlockEntity && lv4 instanceof ChestBlock) {
            lv = ChestBlock.getInventory((ChestBlock)lv4, lv3, world, lv2, true);
        }
        if (lv == null && !(list = world.getEntities((Entity)null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
            lv = (Inventory)((Object)list.get(world.random.nextInt(list.size())));
        }
        return lv;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem()) {
            return false;
        }
        if (first.getDamage() != second.getDamage()) {
            return false;
        }
        if (first.getCount() > first.getMaxCount()) {
            return false;
        }
        return ItemStack.areTagsEqual(first, second);
    }

    @Override
    public double getHopperX() {
        return (double)this.pos.getX() + 0.5;
    }

    @Override
    public double getHopperY() {
        return (double)this.pos.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return (double)this.pos.getZ() + 0.5;
    }

    private void setCooldown(int cooldown) {
        this.transferCooldown = cooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > 8;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    public void onEntityCollided(Entity arg) {
        if (arg instanceof ItemEntity) {
            BlockPos lv = this.getPos();
            if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(arg.getBoundingBox().offset(-lv.getX(), -lv.getY(), -lv.getZ())), this.getInputAreaShape(), BooleanBiFunction.AND)) {
                this.insertAndExtract(() -> HopperBlockEntity.extract(this, (ItemEntity)arg));
            }
        }
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }
}

