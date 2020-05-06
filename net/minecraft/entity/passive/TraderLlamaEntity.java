/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class TraderLlamaEntity
extends LlamaEntity {
    private int despawnDelay = 47999;

    public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> arg, World arg2) {
        super((EntityType<? extends LlamaEntity>)arg, arg2);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isTrader() {
        return true;
    }

    @Override
    protected LlamaEntity createChild() {
        return EntityType.TRADER_LLAMA.create(this.world);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("DespawnDelay", 99)) {
            this.despawnDelay = arg.getInt("DespawnDelay");
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.targetSelector.add(1, new DefendTraderGoal(this));
    }

    @Override
    protected void putPlayerOnBack(PlayerEntity arg) {
        Entity lv = this.getHoldingEntity();
        if (lv instanceof WanderingTraderEntity) {
            return;
        }
        super.putPlayerOnBack(arg);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            this.method_20501();
        }
    }

    private void method_20501() {
        if (!this.method_20502()) {
            return;
        }
        int n = this.despawnDelay = this.heldByTrader() ? ((WanderingTraderEntity)this.getHoldingEntity()).getDespawnDelay() - 1 : this.despawnDelay - 1;
        if (this.despawnDelay <= 0) {
            this.detachLeash(true, false);
            this.remove();
        }
    }

    private boolean method_20502() {
        return !this.isTame() && !this.leashedByPlayer() && !this.hasPlayerRider();
    }

    private boolean heldByTrader() {
        return this.getHoldingEntity() instanceof WanderingTraderEntity;
    }

    private boolean leashedByPlayer() {
        return this.isLeashed() && !this.heldByTrader();
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg3 == SpawnType.EVENT) {
            this.setBreedingAge(0);
        }
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)arg4).setBabyAllowed(false);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public class DefendTraderGoal
    extends TrackTargetGoal {
        private final LlamaEntity llama;
        private LivingEntity offender;
        private int traderLastAttackedTime;

        public DefendTraderGoal(LlamaEntity arg2) {
            super(arg2, false);
            this.llama = arg2;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!this.llama.isLeashed()) {
                return false;
            }
            Entity lv = this.llama.getHoldingEntity();
            if (!(lv instanceof WanderingTraderEntity)) {
                return false;
            }
            WanderingTraderEntity lv2 = (WanderingTraderEntity)lv;
            this.offender = lv2.getAttacker();
            int i = lv2.getLastAttackedTime();
            return i != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
        }

        @Override
        public void start() {
            this.mob.setTarget(this.offender);
            Entity lv = this.llama.getHoldingEntity();
            if (lv instanceof WanderingTraderEntity) {
                this.traderLastAttackedTime = ((WanderingTraderEntity)lv).getLastAttackedTime();
            }
            super.start();
        }
    }
}

