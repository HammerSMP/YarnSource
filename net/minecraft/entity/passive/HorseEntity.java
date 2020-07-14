/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_5425;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class HorseEntity
extends HorseBaseEntity {
    private static final UUID HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
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
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
        if (!this.items.getStack(1).isEmpty()) {
            tag.put("ArmorItem", this.items.getStack(1).toTag(new CompoundTag()));
        }
    }

    public ItemStack getArmorType() {
        return this.getEquippedStack(EquipmentSlot.CHEST);
    }

    private void equipArmor(ItemStack stack) {
        this.equipStack(EquipmentSlot.CHEST, stack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ItemStack lv;
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.contains("ArmorItem", 10) && !(lv = ItemStack.fromTag(tag.getCompound("ArmorItem"))).isEmpty() && this.canEquip(lv)) {
            this.items.setStack(1, lv);
        }
        this.updateSaddle();
    }

    private void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    private int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    private void setVariant(HorseColor color, HorseMarking marking) {
        this.setVariant(color.getIndex() & 0xFF | marking.getIndex() << 8 & 0xFF00);
    }

    public HorseColor getColor() {
        return HorseColor.byIndex(this.getVariant() & 0xFF);
    }

    public HorseMarking getMarking() {
        return HorseMarking.byIndex((this.getVariant() & 0xFF00) >> 8);
    }

    @Override
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        super.updateSaddle();
        this.setArmorTypeFromStack(this.items.getStack(1));
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    private void setArmorTypeFromStack(ItemStack stack) {
        this.equipArmor(stack);
        if (!this.world.isClient) {
            int i;
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(HORSE_ARMOR_BONUS_ID);
            if (this.canEquip(stack) && (i = ((HorseArmorItem)stack.getItem()).getBonus()) != 0) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_ID, "Horse armor bonus", (double)i, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        ItemStack lv = this.getArmorType();
        super.onInventoryChanged(sender);
        ItemStack lv2 = this.getArmorType();
        if (this.age > 20 && this.canEquip(lv2) && lv != lv2) {
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5f, 1.0f);
        }
    }

    @Override
    protected void playWalkSound(BlockSoundGroup group) {
        super.playWalkSound(group);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, group.getVolume() * 0.6f, group.getPitch());
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
    protected SoundEvent getEatSound() {
        return SoundEvents.ENTITY_HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
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
            boolean bl;
            if (this.isBreedingItem(lv)) {
                return this.method_30009(player, lv);
            }
            ActionResult lv2 = lv.useOnEntity(player, this, hand);
            if (lv2.isAccepted()) {
                return lv2;
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return ActionResult.success(this.world.isClient);
            }
            boolean bl2 = bl = !this.isBaby() && !this.isSaddled() && lv.getItem() == Items.SADDLE;
            if (this.canEquip(lv) || bl) {
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

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        }
        if (other instanceof DonkeyEntity || other instanceof HorseEntity) {
            return this.canBreed() && ((HorseBaseEntity)other).canBreed();
        }
        return false;
    }

    @Override
    public PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        HorseBaseEntity lv3;
        if (arg2 instanceof DonkeyEntity) {
            HorseBaseEntity lv = EntityType.MULE.create(arg);
        } else {
            HorseMarking lv9;
            HorseColor lv6;
            HorseEntity lv2 = (HorseEntity)arg2;
            lv3 = EntityType.HORSE.create(arg);
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
        this.setChildAttributes(arg2, lv3);
        return lv3;
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack item) {
        return item.getItem() instanceof HorseArmorItem;
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        HorseColor lv2;
        if (entityData instanceof HorseData) {
            HorseColor lv = ((HorseData)entityData).color;
        } else {
            lv2 = Util.getRandom(HorseColor.values(), this.random);
            entityData = new HorseData(lv2);
        }
        this.setVariant(lv2, Util.getRandom(HorseMarking.values(), this.random));
        return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
    }

    public static class HorseData
    extends PassiveEntity.PassiveData {
        public final HorseColor color;

        public HorseData(HorseColor color) {
            super(true);
            this.color = color;
        }
    }
}

