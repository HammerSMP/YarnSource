/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class LootableContainerBlockEntity
extends LockableContainerBlockEntity {
    @Nullable
    protected Identifier lootTableId;
    protected long lootTableSeed;

    protected LootableContainerBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    public static void setLootTable(BlockView arg, Random random, BlockPos arg2, Identifier arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof LootableContainerBlockEntity) {
            ((LootableContainerBlockEntity)lv).setLootTable(arg3, random.nextLong());
        }
    }

    protected boolean deserializeLootTable(CompoundTag arg) {
        if (arg.contains("LootTable", 8)) {
            this.lootTableId = new Identifier(arg.getString("LootTable"));
            this.lootTableSeed = arg.getLong("LootTableSeed");
            return true;
        }
        return false;
    }

    protected boolean serializeLootTable(CompoundTag arg) {
        if (this.lootTableId == null) {
            return false;
        }
        arg.putString("LootTable", this.lootTableId.toString());
        if (this.lootTableSeed != 0L) {
            arg.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }

    public void checkLootInteraction(@Nullable PlayerEntity arg) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootTable lv = this.world.getServer().getLootManager().getTable(this.lootTableId);
            if (arg instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity)arg, this.lootTableId);
            }
            this.lootTableId = null;
            LootContext.Builder lv2 = new LootContext.Builder((ServerWorld)this.world).parameter(LootContextParameters.POSITION, new BlockPos(this.pos)).random(this.lootTableSeed);
            if (arg != null) {
                lv2.luck(arg.getLuck()).parameter(LootContextParameters.THIS_ENTITY, arg);
            }
            lv.supplyInventory(this, lv2.build(LootContextTypes.CHEST));
        }
    }

    public void setLootTable(Identifier arg, long l) {
        this.lootTableId = arg;
        this.lootTableSeed = l;
    }

    @Override
    public boolean isEmpty() {
        this.checkLootInteraction(null);
        return this.getInvStackList().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int i) {
        this.checkLootInteraction(null);
        return this.getInvStackList().get(i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        this.checkLootInteraction(null);
        ItemStack lv = Inventories.splitStack(this.getInvStackList(), i, j);
        if (!lv.isEmpty()) {
            this.markDirty();
        }
        return lv;
    }

    @Override
    public ItemStack removeStack(int i) {
        this.checkLootInteraction(null);
        return Inventories.removeStack(this.getInvStackList(), i);
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(i, arg);
        if (arg.getCount() > this.getMaxCountPerStack()) {
            arg.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(arg.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public void clear() {
        this.getInvStackList().clear();
    }

    protected abstract DefaultedList<ItemStack> getInvStackList();

    protected abstract void setInvStackList(DefaultedList<ItemStack> var1);

    @Override
    public boolean checkUnlocked(PlayerEntity arg) {
        return super.checkUnlocked(arg) && (this.lootTableId == null || !arg.isSpectator());
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory arg, PlayerEntity arg2) {
        if (this.checkUnlocked(arg2)) {
            this.checkLootInteraction(arg.player);
            return this.createScreenHandler(i, arg);
        }
        return null;
    }
}

