/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class HorseEntity
extends HorseBaseEntity {
    private static final UUID HORSE_ARMOR_BONUS_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public HorseEntity(EntityType<? extends HorseEntity> arg, World arg2) {
        super((EntityType<? extends HorseBaseEntity>)arg, arg2);
    }

    @Override
    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getChildHealthBonus());
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.getChildMovementSpeedBonus());
        this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Variant", this.getVariant());
        if (!this.items.getStack(1).isEmpty()) {
            arg.put("ArmorItem", this.items.getStack(1).toTag(new CompoundTag()));
        }
    }

    public ItemStack getArmorType() {
        return this.getEquippedStack(EquipmentSlot.CHEST);
    }

    private void equipArmor(ItemStack arg) {
        this.equipStack(EquipmentSlot.CHEST, arg);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        ItemStack lv;
        super.readCustomDataFromTag(arg);
        this.setVariant(arg.getInt("Variant"));
        if (arg.contains("ArmorItem", 10) && !(lv = ItemStack.fromTag(arg.getCompound("ArmorItem"))).isEmpty() && this.canEquip(lv)) {
            this.items.setStack(1, lv);
        }
        this.updateSaddle();
    }

    private void setVariant(int i) {
        this.dataTracker.set(VARIANT, i);
    }

    private int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    private void setVariant(HorseColor arg, HorseMarking arg2) {
        this.setVariant(arg.getIndex() & 0xFF | arg2.getIndex() << 8 & 0xFF00);
    }

    public HorseColor getColor() {
        return HorseColor.byIndex(this.getVariant() & 0xFF);
    }

    public HorseMarking getMarking() {
        return HorseMarking.byIndex((this.getVariant() & 0xFF00) >> 8);
    }

    @Override
    protected void updateSaddle() {
        super.updateSaddle();
        this.setArmorTypeFromStack(this.items.getStack(1));
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    private void setArmorTypeFromStack(ItemStack arg) {
        this.equipArmor(arg);
        if (!this.world.isClient) {
            int i;
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(HORSE_ARMOR_BONUS_UUID);
            if (this.canEquip(arg) && (i = ((HorseArmorItem)arg.getItem()).getBonus()) != 0) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_UUID, "Horse armor bonus", (double)i, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void onInventoryChanged(Inventory arg) {
        ItemStack lv = this.getArmorType();
        super.onInventoryChanged(arg);
        ItemStack lv2 = this.getArmorType();
        if (this.age > 20 && this.canEquip(lv2) && lv != lv2) {
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5f, 1.0f);
        }
    }

    @Override
    protected void playWalkSound(BlockSoundGroup arg) {
        super.playWalkSound(arg);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, arg.getVolume() * 0.6f, arg.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent method_28368() {
        return SoundEvents.ENTITY_HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        super.getHurtSound(arg);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        boolean bl;
        ItemStack lv = arg.getStackInHand(arg2);
        boolean bl2 = bl = !lv.isEmpty();
        if (bl && lv.getItem() instanceof SpawnEggItem) {
            return super.interactMob(arg, arg2);
        }
        if (!this.isBaby()) {
            if (this.isTame() && arg.shouldCancelInteraction()) {
                this.openInventory(arg);
                return true;
            }
            if (this.hasPassengers()) {
                return super.interactMob(arg, arg2);
            }
        }
        if (bl) {
            boolean bl22;
            if (this.receiveFood(arg, lv)) {
                if (!arg.abilities.creativeMode) {
                    lv.decrement(1);
                }
                return true;
            }
            if (lv.useOnEntity(arg, this, arg2)) {
                return true;
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return true;
            }
            boolean bl3 = bl22 = !this.isBaby() && !this.isSaddled() && lv.getItem() == Items.SADDLE;
            if (this.canEquip(lv) || bl22) {
                this.openInventory(arg);
                return true;
            }
        }
        if (this.isBaby()) {
            return super.interactMob(arg, arg2);
        }
        this.putPlayerOnBack(arg);
        return true;
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        if (arg == this) {
            return false;
        }
        if (arg instanceof DonkeyEntity || arg instanceof HorseEntity) {
            return this.canBreed() && ((HorseBaseEntity)arg).canBreed();
        }
        return false;
    }

    @Override
    public PassiveEntity createChild(PassiveEntity arg) {
        HorseBaseEntity lv3;
        if (arg instanceof DonkeyEntity) {
            HorseBaseEntity lv = EntityType.MULE.create(this.world);
        } else {
            HorseMarking lv9;
            HorseColor lv6;
            HorseEntity lv2 = (HorseEntity)arg;
            lv3 = EntityType.HORSE.create(this.world);
            int i = this.random.nextInt(9);
            if (i < 4) {
                HorseColor lv4 = this.getColor();
            } else if (i < 8) {
                HorseColor lv5 = lv2.getColor();
            } else {
                lv6 = Util.getRandom(HorseColor.values(), this.random);
            }
            int j = this.random.nextInt(5);
            if (j < 2) {
                HorseMarking lv7 = this.getMarking();
            } else if (j < 4) {
                HorseMarking lv8 = lv2.getMarking();
            } else {
                lv9 = Util.getRandom(HorseMarking.values(), this.random);
            }
            ((HorseEntity)lv3).setVariant(lv6, lv9);
        }
        this.setChildAttributes(arg, lv3);
        return lv3;
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack arg) {
        return arg.getItem() instanceof HorseArmorItem;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        HorseColor lv2;
        if (arg4 instanceof HorseData) {
            HorseColor lv = ((HorseData)arg4).color;
        } else {
            lv2 = Util.getRandom(HorseColor.values(), this.random);
            arg4 = new HorseData(lv2);
        }
        this.setVariant(lv2, Util.getRandom(HorseMarking.values(), this.random));
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public static class HorseData
    extends PassiveEntity.PassiveData {
        public final HorseColor color;

        public HorseData(HorseColor arg) {
            this.color = arg;
        }
    }
}

