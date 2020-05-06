/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.DisableableFollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RaidGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WitchEntity
extends RaiderEntity
implements RangedAttackMob {
    private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final EntityAttributeModifier DRINKING_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, EntityAttributeModifier.Operation.ADDITION);
    private static final TrackedData<Boolean> DRINKING = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int drinkTimeLeft;
    private RaidGoal<RaiderEntity> raidGoal;
    private DisableableFollowTargetGoal<PlayerEntity> attackPlayerGoal;

    public WitchEntity(EntityType<? extends WitchEntity> arg, World arg2) {
        super((EntityType<? extends RaiderEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.raidGoal = new RaidGoal<RaiderEntity>(this, RaiderEntity.class, true, arg -> arg != null && this.hasActiveRaid() && arg.getType() != EntityType.WITCH);
        this.attackPlayerGoal = new DisableableFollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, null);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class));
        this.targetSelector.add(2, this.raidGoal);
        this.targetSelector.add(3, this.attackPlayerGoal);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(DRINKING, false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITCH_DEATH;
    }

    public void setDrinking(boolean bl) {
        this.getDataTracker().set(DRINKING, bl);
    }

    public boolean isDrinking() {
        return this.getDataTracker().get(DRINKING);
    }

    public static DefaultAttributeContainer.Builder createWitchAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 26.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    public void tickMovement() {
        if (!this.world.isClient && this.isAlive()) {
            this.raidGoal.decreaseCooldown();
            if (this.raidGoal.getCooldown() <= 0) {
                this.attackPlayerGoal.setEnabled(true);
            } else {
                this.attackPlayerGoal.setEnabled(false);
            }
            if (this.isDrinking()) {
                if (this.drinkTimeLeft-- <= 0) {
                    List<StatusEffectInstance> list;
                    this.setDrinking(false);
                    ItemStack lv = this.getMainHandStack();
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (lv.getItem() == Items.POTION && (list = PotionUtil.getPotionEffects(lv)) != null) {
                        for (StatusEffectInstance lv2 : list) {
                            this.addStatusEffect(new StatusEffectInstance(lv2));
                        }
                    }
                    this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(DRINKING_SPEED_PENALTY_MODIFIER);
                }
            } else {
                Potion lv3 = null;
                if (this.random.nextFloat() < 0.15f && this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                    lv3 = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15f && (this.isOnFire() || this.getRecentDamageSource() != null && this.getRecentDamageSource().isFire()) && !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    lv3 = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05f && this.getHealth() < this.getMaximumHealth()) {
                    lv3 = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5f && this.getTarget() != null && !this.hasStatusEffect(StatusEffects.SPEED) && this.getTarget().squaredDistanceTo(this) > 121.0) {
                    lv3 = Potions.SWIFTNESS;
                }
                if (lv3 != null) {
                    this.equipStack(EquipmentSlot.MAINHAND, PotionUtil.setPotion(new ItemStack(Items.POTION), lv3));
                    this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime();
                    this.setDrinking(true);
                    if (!this.isSilent()) {
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
                    }
                    EntityAttributeInstance lv4 = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                    lv4.removeModifier(DRINKING_SPEED_PENALTY_MODIFIER);
                    lv4.addTemporaryModifier(DRINKING_SPEED_PENALTY_MODIFIER);
                }
            }
            if (this.random.nextFloat() < 7.5E-4f) {
                this.world.sendEntityStatus(this, (byte)15);
            }
        }
        super.tickMovement();
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_WITCH_CELEBRATE;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.world.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13f, this.getBoundingBox().y2 + 0.5 + this.random.nextGaussian() * (double)0.13f, this.getZ() + this.random.nextGaussian() * (double)0.13f, 0.0, 0.0, 0.0);
            }
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    protected float applyEnchantmentsToDamage(DamageSource arg, float f) {
        f = super.applyEnchantmentsToDamage(arg, f);
        if (arg.getAttacker() == this) {
            f = 0.0f;
        }
        if (arg.getMagic()) {
            f = (float)((double)f * 0.15);
        }
        return f;
    }

    @Override
    public void attack(LivingEntity arg, float f) {
        if (this.isDrinking()) {
            return;
        }
        Vec3d lv = arg.getVelocity();
        double d = arg.getX() + lv.x - this.getX();
        double e = arg.getEyeY() - (double)1.1f - this.getY();
        double g = arg.getZ() + lv.z - this.getZ();
        float h = MathHelper.sqrt(d * d + g * g);
        Potion lv2 = Potions.HARMING;
        if (arg instanceof RaiderEntity) {
            lv2 = arg.getHealth() <= 4.0f ? Potions.HEALING : Potions.REGENERATION;
            this.setTarget(null);
        } else if (h >= 8.0f && !arg.hasStatusEffect(StatusEffects.SLOWNESS)) {
            lv2 = Potions.SLOWNESS;
        } else if (arg.getHealth() >= 8.0f && !arg.hasStatusEffect(StatusEffects.POISON)) {
            lv2 = Potions.POISON;
        } else if (h <= 3.0f && !arg.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25f) {
            lv2 = Potions.WEAKNESS;
        }
        PotionEntity lv3 = new PotionEntity(this.world, this);
        lv3.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), lv2));
        lv3.pitch -= -20.0f;
        lv3.setVelocity(d, e + (double)(h * 0.2f), g, 0.75f, 8.0f);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
        this.world.spawnEntity(lv3);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 1.62f;
    }

    @Override
    public void addBonusForWave(int i, boolean bl) {
    }

    @Override
    public boolean canLead() {
        return false;
    }
}

