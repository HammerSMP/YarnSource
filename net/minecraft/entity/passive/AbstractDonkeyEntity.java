/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class AbstractDonkeyEntity
extends HorseBaseEntity {
    private static final TrackedData<Boolean> CHEST = DataTracker.registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected AbstractDonkeyEntity(EntityType<? extends AbstractDonkeyEntity> arg, World arg2) {
        super((EntityType<? extends HorseBaseEntity>)arg, arg2);
        this.playExtraHorseSounds = false;
    }

    @Override
    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getChildHealthBonus());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHEST, false);
    }

    public static DefaultAttributeContainer.Builder createAbstractDonkeyAttributes() {
        return AbstractDonkeyEntity.createBaseHorseAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175f).add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.5);
    }

    public boolean hasChest() {
        return this.dataTracker.get(CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.dataTracker.set(CHEST, hasChest);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 17;
        }
        return super.getInventorySize();
    }

    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.25;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasChest()) {
            if (!this.world.isClient) {
                this.dropItem(Blocks.CHEST);
            }
            this.setHasChest(false);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            ListTag lv = new ListTag();
            for (int i = 2; i < this.items.size(); ++i) {
                ItemStack lv2 = this.items.getStack(i);
                if (lv2.isEmpty()) continue;
                CompoundTag lv3 = new CompoundTag();
                lv3.putByte("Slot", (byte)i);
                lv2.toTag(lv3);
                lv.add(lv3);
            }
            tag.put("Items", lv);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setHasChest(tag.getBoolean("ChestedHorse"));
        if (this.hasChest()) {
            ListTag lv = tag.getList("Items", 10);
            this.onChestedStatusChanged();
            for (int i = 0; i < lv.size(); ++i) {
                CompoundTag lv2 = lv.getCompound(i);
                int j = lv2.getByte("Slot") & 0xFF;
                if (j < 2 || j >= this.items.size()) continue;
                this.items.setStack(j, ItemStack.fromTag(lv2));
            }
        }
        this.updateSaddle();
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (slot == 499) {
            if (this.hasChest() && item.isEmpty()) {
                this.setHasChest(false);
                this.onChestedStatusChanged();
                return true;
            }
            if (!this.hasChest() && item.getItem() == Blocks.CHEST.asItem()) {
                this.setHasChest(true);
                this.onChestedStatusChanged();
                return true;
            }
        }
        return super.equip(slot, item);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack lv = player.getStackInHand(hand);
        if (!this.isBaby()) {
            if (this.isTame() && player.shouldCancelInteraction()) {
                this.openInventory(player);
                return ActionResult.success(this.world.isClient);
            }
            if (this.hasPassengers()) {
                return super.interactMob(player, hand);
            }
        }
        if (!lv.isEmpty()) {
            if (this.isBreedingItem(lv)) {
                return this.method_30009(player, lv);
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return ActionResult.success(this.world.isClient);
            }
            if (!this.hasChest() && lv.getItem() == Blocks.CHEST.asItem()) {
                this.setHasChest(true);
                this.playAddChestSound();
                if (!player.abilities.creativeMode) {
                    lv.decrement(1);
                }
                this.onChestedStatusChanged();
                return ActionResult.success(this.world.isClient);
            }
            if (!this.isBaby() && !this.isSaddled() && lv.getItem() == Items.SADDLE) {
                this.openInventory(player);
                return ActionResult.success(this.world.isClient);
            }
        }
        if (this.isBaby()) {
            return super.interactMob(player, hand);
        }
        this.putPlayerOnBack(player);
        return ActionResult.success(this.world.isClient);
    }

    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    public int getInventoryColumns() {
        return 5;
    }
}

