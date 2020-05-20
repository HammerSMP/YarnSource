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
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(arg2)) {
            Inventories.fromTag(arg2, this.inventory);
        }
        this.transferCooldown = arg2.getInt("TransferCooldown");
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (!this.serializeLootTable(arg)) {
            Inventories.toTag(arg, this.inventory);
        }
        arg.putInt("TransferCooldown", this.transferCooldown);
        return arg;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), i, j);
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(i, arg);
        if (arg.getCount() > this.getMaxCountPerStack()) {
            arg.setCount(this.getMaxCountPerStack());
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

    private boolean insertAndExtract(Supplier<Boolean> supplier) {
        if (this.world == null || this.world.isClient) {
            return false;
        }
        if (!this.needsCooldown() && this.getCachedState().get(HopperBlock.ENABLED).booleanValue()) {
            boolean bl = false;
            if (!this.isEmpty()) {
                bl = this.insert();
            }
            if (!this.isFull()) {
                bl |= supplier.get().booleanValue();
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

    private static IntStream getAvailableSlots(Inventory arg, Direction arg2) {
        if (arg instanceof SidedInventory) {
            return IntStream.of(((SidedInventory)arg).getAvailableSlots(arg2));
        }
        return IntStream.range(0, arg.size());
    }

    private boolean isInventoryFull(Inventory arg, Direction arg2) {
        return HopperBlockEntity.getAvailableSlots(arg, arg2).allMatch(i -> {
            ItemStack lv = arg.getStack(i);
            return lv.getCount() >= lv.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory arg, Direction arg2) {
        return HopperBlockEntity.getAvailableSlots(arg, arg2).allMatch(i -> arg.getStack(i).isEmpty());
    }

    public static boolean extract(Hopper arg) {
        Inventory lv = HopperBlockEntity.getInputInventory(arg);
        if (lv != null) {
            Direction lv2 = Direction.DOWN;
            if (HopperBlockEntity.isInventoryEmpty(lv, lv2)) {
                return false;
            }
            return HopperBlockEntity.getAvailableSlots(lv, lv2).anyMatch(i -> HopperBlockEntity.extract(arg, lv, i, lv2));
        }
        for (ItemEntity lv3 : HopperBlockEntity.getInputItemEntities(arg)) {
            if (!HopperBlockEntity.extract(arg, lv3)) continue;
            return true;
        }
        return false;
    }

    private static boolean extract(Hopper arg, Inventory arg2, int i, Direction arg3) {
        ItemStack lv = arg2.getStack(i);
        if (!lv.isEmpty() && HopperBlockEntity.canExtract(arg2, lv, i, arg3)) {
            ItemStack lv2 = lv.copy();
            ItemStack lv3 = HopperBlockEntity.transfer(arg2, arg, arg2.removeStack(i, 1), null);
            if (lv3.isEmpty()) {
                arg2.markDirty();
                return true;
            }
            arg2.setStack(i, lv2);
        }
        return false;
    }

    public static boolean extract(Inventory arg, ItemEntity arg2) {
        boolean bl = false;
        ItemStack lv = arg2.getStack().copy();
        ItemStack lv2 = HopperBlockEntity.transfer(null, arg, lv, null);
        if (lv2.isEmpty()) {
            bl = true;
            arg2.remove();
        } else {
            arg2.setStack(lv2);
        }
        return bl;
    }

    public static ItemStack transfer(@Nullable Inventory arg, Inventory arg2, ItemStack arg3, @Nullable Direction arg4) {
        if (arg2 instanceof SidedInventory && arg4 != null) {
            SidedInventory lv = (SidedInventory)arg2;
            int[] is = lv.getAvailableSlots(arg4);
            for (int i = 0; i < is.length && !arg3.isEmpty(); ++i) {
                arg3 = HopperBlockEntity.transfer(arg, arg2, arg3, is[i], arg4);
            }
        } else {
            int j = arg2.size();
            for (int k = 0; k < j && !arg3.isEmpty(); ++k) {
                arg3 = HopperBlockEntity.transfer(arg, arg2, arg3, k, arg4);
            }
        }
        return arg3;
    }

    private static boolean canInsert(Inventory arg, ItemStack arg2, int i, @Nullable Direction arg3) {
        if (!arg.isValid(i, arg2)) {
            return false;
        }
        return !(arg instanceof SidedInventory) || ((SidedInventory)arg).canInsert(i, arg2, arg3);
    }

    private static boolean canExtract(Inventory arg, ItemStack arg2, int i, Direction arg3) {
        return !(arg instanceof SidedInventory) || ((SidedInventory)arg).canExtract(i, arg2, arg3);
    }

    private static ItemStack transfer(@Nullable Inventory arg, Inventory arg2, ItemStack arg3, int i, @Nullable Direction arg4) {
        ItemStack lv = arg2.getStack(i);
        if (HopperBlockEntity.canInsert(arg2, arg3, i, arg4)) {
            boolean bl = false;
            boolean bl2 = arg2.isEmpty();
            if (lv.isEmpty()) {
                arg2.setStack(i, arg3);
                arg3 = ItemStack.EMPTY;
                bl = true;
            } else if (HopperBlockEntity.canMergeItems(lv, arg3)) {
                int j = arg3.getMaxCount() - lv.getCount();
                int k = Math.min(arg3.getCount(), j);
                arg3.decrement(k);
                lv.increment(k);
                boolean bl3 = bl = k > 0;
            }
            if (bl) {
                HopperBlockEntity lv2;
                if (bl2 && arg2 instanceof HopperBlockEntity && !(lv2 = (HopperBlockEntity)arg2).isDisabled()) {
                    int l = 0;
                    if (arg instanceof HopperBlockEntity) {
                        HopperBlockEntity lv3 = (HopperBlockEntity)arg;
                        if (lv2.lastTickTime >= lv3.lastTickTime) {
                            l = 1;
                        }
                    }
                    lv2.setCooldown(8 - l);
                }
                arg2.markDirty();
            }
        }
        return arg3;
    }

    @Nullable
    private Inventory getOutputInventory() {
        Direction lv = this.getCachedState().get(HopperBlock.FACING);
        return HopperBlockEntity.getInventoryAt(this.getWorld(), this.pos.offset(lv));
    }

    @Nullable
    public static Inventory getInputInventory(Hopper arg) {
        return HopperBlockEntity.getInventoryAt(arg.getWorld(), arg.getHopperX(), arg.getHopperY() + 1.0, arg.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(Hopper arg) {
        return arg.getInputAreaShape().getBoundingBoxes().stream().flatMap(arg2 -> arg.getWorld().getEntities(ItemEntity.class, arg2.offset(arg.getHopperX() - 0.5, arg.getHopperY() - 0.5, arg.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static Inventory getInventoryAt(World arg, BlockPos arg2) {
        return HopperBlockEntity.getInventoryAt(arg, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5);
    }

    @Nullable
    public static Inventory getInventoryAt(World arg, double d, double e, double f) {
        List<Entity> list;
        BlockEntity lv5;
        Inventory lv = null;
        BlockPos lv2 = new BlockPos(d, e, f);
        BlockState lv3 = arg.getBlockState(lv2);
        Block lv4 = lv3.getBlock();
        if (lv4 instanceof InventoryProvider) {
            lv = ((InventoryProvider)((Object)lv4)).getInventory(lv3, arg, lv2);
        } else if (lv4.hasBlockEntity() && (lv5 = arg.getBlockEntity(lv2)) instanceof Inventory && (lv = (Inventory)((Object)lv5)) instanceof ChestBlockEntity && lv4 instanceof ChestBlock) {
            lv = ChestBlock.getInventory((ChestBlock)lv4, lv3, arg, lv2, true);
        }
        if (lv == null && !(list = arg.getEntities((Entity)null, new Box(d - 0.5, e - 0.5, f - 0.5, d + 0.5, e + 0.5, f + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
            lv = (Inventory)((Object)list.get(arg.random.nextInt(list.size())));
        }
        return lv;
    }

    private static boolean canMergeItems(ItemStack arg, ItemStack arg2) {
        if (arg.getItem() != arg2.getItem()) {
            return false;
        }
        if (arg.getDamage() != arg2.getDamage()) {
            return false;
        }
        if (arg.getCount() > arg.getMaxCount()) {
            return false;
        }
        return ItemStack.areTagsEqual(arg, arg2);
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

    private void setCooldown(int i) {
        this.transferCooldown = i;
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
    protected void setInvStackList(DefaultedList<ItemStack> arg) {
        this.inventory = arg;
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
    protected ScreenHandler createScreenHandler(int i, PlayerInventory arg) {
        return new HopperScreenHandler(i, arg, this);
    }
}

