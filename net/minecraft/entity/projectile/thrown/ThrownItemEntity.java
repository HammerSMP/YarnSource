/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.projectile.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public abstract class ThrownItemEntity
extends ThrownEntity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(ThrownItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public ThrownItemEntity(EntityType<? extends ThrownItemEntity> arg, World arg2) {
        super((EntityType<? extends ThrownEntity>)arg, arg2);
    }

    public ThrownItemEntity(EntityType<? extends ThrownItemEntity> arg, double d, double e, double f, World arg2) {
        super(arg, d, e, f, arg2);
    }

    public ThrownItemEntity(EntityType<? extends ThrownItemEntity> arg, LivingEntity arg2, World arg3) {
        super(arg, arg2, arg3);
    }

    public void setItem(ItemStack arg2) {
        if (arg2.getItem() != this.getDefaultItem() || arg2.hasTag()) {
            this.getDataTracker().set(ITEM, Util.make(arg2.copy(), arg -> arg.setCount(1)));
        }
    }

    protected abstract Item getDefaultItem();

    protected ItemStack getItem() {
        return this.getDataTracker().get(ITEM);
    }

    @Override
    public ItemStack getStack() {
        ItemStack lv = this.getItem();
        return lv.isEmpty() ? new ItemStack(this.getDefaultItem()) : lv;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        ItemStack lv = this.getItem();
        if (!lv.isEmpty()) {
            arg.put("Item", lv.toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        ItemStack lv = ItemStack.fromTag(arg.getCompound("Item"));
        this.setItem(lv);
    }
}

