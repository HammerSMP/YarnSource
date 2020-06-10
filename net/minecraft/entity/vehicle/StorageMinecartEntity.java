/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class StorageMinecartEntity
extends AbstractMinecartEntity
implements Inventory,
NamedScreenHandlerFactory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    private boolean field_7733 = true;
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;

    protected StorageMinecartEntity(EntityType<?> arg, World arg2) {
        super(arg, arg2);
    }

    protected StorageMinecartEntity(EntityType<?> arg, double d, double e, double f, World arg2) {
        super(arg, arg2, d, e, f);
    }

    @Override
    public void dropItems(DamageSource arg) {
        super.dropItems(arg);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemScatterer.spawn(this.world, this, (Inventory)this);
        }
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
    public ItemStack getStack(int i) {
        this.generateLoot(null);
        return this.inventory.get(i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        this.generateLoot(null);
        return Inventories.splitStack(this.inventory, i, j);
    }

    @Override
    public ItemStack removeStack(int i) {
        this.generateLoot(null);
        ItemStack lv = this.inventory.get(i);
        if (lv.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.inventory.set(i, ItemStack.EMPTY);
        return lv;
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.generateLoot(null);
        this.inventory.set(i, arg);
        if (!arg.isEmpty() && arg.getCount() > this.getMaxCountPerStack()) {
            arg.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        if (i >= 0 && i < this.size()) {
            this.setStack(i, arg);
            return true;
        }
        return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        if (this.removed) {
            return false;
        }
        return !(arg.squaredDistanceTo(this) > 64.0);
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerWorld arg) {
        this.field_7733 = false;
        return super.changeDimension(arg);
    }

    @Override
    public void remove() {
        if (!this.world.isClient && this.field_7733) {
            ItemScatterer.spawn(this.world, this, (Inventory)this);
        }
        super.remove();
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.lootTableId != null) {
            arg.putString("LootTable", this.lootTableId.toString());
            if (this.lootSeed != 0L) {
                arg.putLong("LootTableSeed", this.lootSeed);
            }
        } else {
            Inventories.toTag(arg, this.inventory);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (arg.contains("LootTable", 8)) {
            this.lootTableId = new Identifier(arg.getString("LootTable"));
            this.lootSeed = arg.getLong("LootTableSeed");
        } else {
            Inventories.fromTag(arg, this.inventory);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity arg, Hand arg2) {
        arg.openHandledScreen(this);
        return ActionResult.success(this.world.isClient);
    }

    @Override
    protected void applySlowdown() {
        float f = 0.98f;
        if (this.lootTableId == null) {
            int i = 15 - ScreenHandler.calculateComparatorOutput(this);
            f += (float)i * 0.001f;
        }
        this.setVelocity(this.getVelocity().multiply(f, 0.0, f));
    }

    public void generateLoot(@Nullable PlayerEntity arg) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootTable lv = this.world.getServer().getLootManager().getTable(this.lootTableId);
            if (arg instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity)arg, this.lootTableId);
            }
            this.lootTableId = null;
            LootContext.Builder lv2 = new LootContext.Builder((ServerWorld)this.world).parameter(LootContextParameters.POSITION, this.getBlockPos()).random(this.lootSeed);
            if (arg != null) {
                lv2.luck(arg.getLuck()).parameter(LootContextParameters.THIS_ENTITY, arg);
            }
            lv.supplyInventory(this, lv2.build(LootContextTypes.CHEST));
        }
    }

    @Override
    public void clear() {
        this.generateLoot(null);
        this.inventory.clear();
    }

    public void setLootTable(Identifier arg, long l) {
        this.lootTableId = arg;
        this.lootSeed = l;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory arg, PlayerEntity arg2) {
        if (this.lootTableId == null || !arg2.isSpectator()) {
            this.generateLoot(arg.player);
            return this.getScreenHandler(i, arg);
        }
        return null;
    }

    protected abstract ScreenHandler getScreenHandler(int var1, PlayerInventory var2);
}

