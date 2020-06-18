/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ElderGuardianEntity
extends GuardianEntity {
    public static final float SCALE = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

    public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> arg, World arg2) {
        super((EntityType<? extends GuardianEntity>)arg, arg2);
        this.setPersistent();
        if (this.wanderGoal != null) {
            this.wanderGoal.setChance(400);
        }
    }

    public static DefaultAttributeContainer.Builder createElderGuardianAttributes() {
        return GuardianEntity.createGuardianAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0);
    }

    @Override
    public int getWarmupTime() {
        return 60;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        int i = 1200;
        if ((this.age + this.getEntityId()) % 1200 == 0) {
            StatusEffect lv = StatusEffects.MINING_FATIGUE;
            List<ServerPlayerEntity> list = ((ServerWorld)this.world).getPlayers(arg -> this.squaredDistanceTo((Entity)arg) < 2500.0 && arg.interactionManager.isSurvivalLike());
            int j = 2;
            int k = 6000;
            int l = 1200;
            for (ServerPlayerEntity lv2 : list) {
                if (lv2.hasStatusEffect(lv) && lv2.getStatusEffect(lv).getAmplifier() >= 2 && lv2.getStatusEffect(lv).getDuration() >= 1200) continue;
                lv2.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, this.isSilent() ? 0.0f : 1.0f));
                lv2.addStatusEffect(new StatusEffectInstance(lv, 6000, 2));
            }
        }
        if (!this.hasPositionTarget()) {
            this.setPositionTarget(this.getBlockPos(), 16);
        }
    }
}

