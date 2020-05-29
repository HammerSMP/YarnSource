/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.mob;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.logging.log4j.Logger;

public class ZombieVillagerEntity
extends ZombieEntity
implements VillagerDataContainer {
    private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    private int conversionTimer;
    private UUID converter;
    private Tag gossipData;
    private CompoundTag offerData;
    private int xp;

    public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> arg, World arg2) {
        super((EntityType<? extends ZombieEntity>)arg, arg2);
        this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CONVERTING, false);
        this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(arg2 -> arg.put("VillagerData", (Tag)arg2));
        if (this.offerData != null) {
            arg.put("Offers", this.offerData);
        }
        if (this.gossipData != null) {
            arg.put("Gossips", this.gossipData);
        }
        arg.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null) {
            arg.putUuid("ConversionPlayer", this.converter);
        }
        arg.putInt("Xp", this.xp);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("VillagerData", 10)) {
            DataResult dataResult = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)arg.get("VillagerData")));
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(this::setVillagerData);
        }
        if (arg.contains("Offers", 10)) {
            this.offerData = arg.getCompound("Offers");
        }
        if (arg.contains("Gossips", 10)) {
            this.gossipData = arg.getList("Gossips", 10);
        }
        if (arg.contains("ConversionTime", 99) && arg.getInt("ConversionTime") > -1) {
            this.setConverting(arg.containsUuid("ConversionPlayer") ? arg.getUuid("ConversionPlayer") : null, arg.getInt("ConversionTime"));
        }
        if (arg.contains("Xp", 3)) {
            this.xp = arg.getInt("Xp");
        }
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && this.isConverting()) {
            int i = this.getConversionRate();
            this.conversionTimer -= i;
            if (this.conversionTimer <= 0) {
                this.finishConversion((ServerWorld)this.world);
            }
        }
        super.tick();
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() == Items.GOLDEN_APPLE && this.hasStatusEffect(StatusEffects.WEAKNESS)) {
            if (!arg.abilities.creativeMode) {
                lv.decrement(1);
            }
            if (!this.world.isClient) {
                this.setConverting(arg.getUuid(), this.random.nextInt(2401) + 3600);
                arg.swingHand(arg2, true);
            }
            return true;
        }
        return super.interactMob(arg, arg2);
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isConverting() && this.xp == 0;
    }

    public boolean isConverting() {
        return this.getDataTracker().get(CONVERTING);
    }

    private void setConverting(@Nullable UUID uUID, int i) {
        this.converter = uUID;
        this.conversionTimer = i;
        this.getDataTracker().set(CONVERTING, true);
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, i, Math.min(this.world.getDifficulty().getId() - 1, 0)));
        this.world.sendEntityStatus(this, (byte)16);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 16) {
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            return;
        }
        super.handleStatus(b);
    }

    private void finishConversion(ServerWorld arg) {
        PlayerEntity lv4;
        VillagerEntity lv = EntityType.VILLAGER.create(arg);
        for (EquipmentSlot lv2 : EquipmentSlot.values()) {
            ItemStack lv3 = this.getEquippedStack(lv2);
            if (lv3.isEmpty()) continue;
            if (EnchantmentHelper.hasBindingCurse(lv3)) {
                lv.equip(lv2.getEntitySlotId() + 300, lv3);
                continue;
            }
            double d = this.getDropChance(lv2);
            if (!(d > 1.0)) continue;
            this.dropStack(lv3);
        }
        lv.copyPositionAndRotation(this);
        lv.setVillagerData(this.getVillagerData());
        if (this.gossipData != null) {
            lv.setGossipDataFromTag(this.gossipData);
        }
        if (this.offerData != null) {
            lv.setOffers(new TraderOfferList(this.offerData));
        }
        lv.setExperience(this.xp);
        lv.initialize(arg, arg.getLocalDifficulty(lv.getBlockPos()), SpawnReason.CONVERSION, null, null);
        if (this.isBaby()) {
            lv.setBreedingAge(-24000);
        }
        this.remove();
        lv.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            lv.setCustomName(this.getCustomName());
            lv.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistent()) {
            lv.setPersistent();
        }
        lv.setInvulnerable(this.isInvulnerable());
        arg.spawnEntity(lv);
        if (this.converter != null && (lv4 = arg.getPlayerByUuid(this.converter)) instanceof ServerPlayerEntity) {
            Criteria.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)lv4, this, lv);
            arg.handleInteraction(EntityInteraction.ZOMBIE_VILLAGER_CURED, lv4, lv);
        }
        lv.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
        if (!this.isSilent()) {
            arg.syncWorldEvent(null, 1027, this.getBlockPos(), 0);
        }
    }

    private int getConversionRate() {
        int i = 1;
        if (this.random.nextFloat() < 0.01f) {
            int j = 0;
            BlockPos.Mutable lv = new BlockPos.Mutable();
            for (int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
                for (int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
                    for (int m = (int)this.getZ() - 4; m < (int)this.getZ() + 4 && j < 14; ++m) {
                        Block lv2 = this.world.getBlockState(lv.set(k, l, m)).getBlock();
                        if (lv2 != Blocks.IRON_BARS && !(lv2 instanceof BedBlock)) continue;
                        if (this.random.nextFloat() < 0.3f) {
                            ++i;
                        }
                        ++j;
                    }
                }
            }
        }
        return i;
    }

    @Override
    protected float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 2.0f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setOfferData(CompoundTag arg) {
        this.offerData = arg;
    }

    public void setGossipData(Tag arg) {
        this.gossipData = arg;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(arg.getBiome(this.getBlockPos()))));
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public void setVillagerData(VillagerData arg) {
        VillagerData lv = this.getVillagerData();
        if (lv.getProfession() != arg.getProfession()) {
            this.offerData = null;
        }
        this.dataTracker.set(VILLAGER_DATA, arg);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.get(VILLAGER_DATA);
    }

    public void setXp(int i) {
        this.xp = i;
    }
}

