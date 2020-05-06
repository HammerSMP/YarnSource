/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public abstract class AbstractFireballEntity
extends ExplosiveProjectileEntity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(AbstractFireballEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> arg, World arg2) {
        super((EntityType<? extends ExplosiveProjectileEntity>)arg, arg2);
    }

    public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> arg, double d, double e, double f, double g, double h, double i, World arg2) {
        super(arg, d, e, f, g, h, i, arg2);
    }

    public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> arg, LivingEntity arg2, double d, double e, double f, World arg3) {
        super(arg, arg2, d, e, f, arg3);
    }

    public void setItem(ItemStack arg2) {
        if (arg2.getItem() != Items.FIRE_CHARGE || arg2.hasTag()) {
            this.getDataTracker().set(ITEM, Util.make(arg2.copy(), arg -> arg.setCount(1)));
        }
    }

    protected ItemStack getItem() {
        return this.getDataTracker().get(ITEM);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStack() {
        ItemStack lv = this.getItem();
        return lv.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : lv;
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

