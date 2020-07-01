/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.class_4837;
import net.minecraft.AbstractPiglinEntity;
import net.minecraft.PiglinBruteBrain;
import net.minecraft.class_5425;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PiglinBrute
extends AbstractPiglinEntity {
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBrute>>> field_25761 = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.INTERACTABLE_DOORS, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> field_25760 = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, (Object[])new MemoryModuleType[]{MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME});

    public PiglinBrute(EntityType<? extends PiglinBrute> arg, World arg2) {
        super((EntityType<? extends AbstractPiglinEntity>)arg, arg2);
        this.experiencePoints = 20;
    }

    public static DefaultAttributeContainer.Builder method_30242() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0);
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        PiglinBruteBrain.method_30250(this);
        this.initEquipment(arg2);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
    }

    protected Brain.Profile<PiglinBrute> createBrainProfile() {
        return Brain.createProfile(field_25760, field_25761);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return PiglinBruteBrain.method_30252(this, this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<PiglinBrute> getBrain() {
        return super.getBrain();
    }

    @Override
    public boolean canHunt() {
        return false;
    }

    @Override
    public boolean canGather(ItemStack arg) {
        if (arg.getItem() == Items.GOLDEN_AXE) {
            return super.canGather(arg);
        }
        return false;
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("piglinBruteBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        PiglinBruteBrain.method_30256(this);
        PiglinBruteBrain.method_30258(this);
        super.mobTick();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public class_4837 getActivity() {
        if (this.isAttacking() && this.method_30237()) {
            return class_4837.ATTACKING_WITH_MELEE_WEAPON;
        }
        return class_4837.DEFAULT;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        boolean bl = super.damage(arg, f);
        if (this.world.isClient) {
            return false;
        }
        if (bl && arg.getAttacker() instanceof LivingEntity) {
            PiglinBruteBrain.method_30251(this, (LivingEntity)arg.getAttacker());
        }
        return bl;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIGLIN_BRUTE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PIGLIN_BRUTE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_BRUTE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_STEP, 0.15f, 1.0f);
    }

    protected void method_30243() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_ANGRY, 1.0f, this.getSoundPitch());
    }

    @Override
    protected void method_30238() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0f, this.getSoundPitch());
    }
}

