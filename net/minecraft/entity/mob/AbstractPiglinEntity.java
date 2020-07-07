/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public abstract class AbstractPiglinEntity
extends HostileEntity {
    protected static final TrackedData<Boolean> IMMUNE_TO_ZOMBIFICATION = DataTracker.registerData(AbstractPiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int timeInOverworld = 0;

    public AbstractPiglinEntity(EntityType<? extends AbstractPiglinEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.setCanPickUpLoot(true);
        this.setCanPathThroughDoors();
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
    }

    private void setCanPathThroughDoors() {
        EntityNavigation lv = this.getNavigation();
        if (lv instanceof MobNavigation) {
            ((MobNavigation)lv).setCanPathThroughDoors(true);
        }
    }

    protected abstract boolean canHunt();

    public void setImmuneToZombification(boolean bl) {
        this.getDataTracker().set(IMMUNE_TO_ZOMBIFICATION, bl);
    }

    protected boolean isImmuneToZombification() {
        return this.getDataTracker().get(IMMUNE_TO_ZOMBIFICATION);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IMMUNE_TO_ZOMBIFICATION, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.isImmuneToZombification()) {
            arg.putBoolean("IsImmuneToZombification", true);
        }
        arg.putInt("TimeInOverworld", this.timeInOverworld);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setImmuneToZombification(arg.getBoolean("IsImmuneToZombification"));
        this.timeInOverworld = arg.getInt("TimeInOverworld");
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        this.timeInOverworld = this.shouldZombify() ? ++this.timeInOverworld : 0;
        if (this.timeInOverworld > 300) {
            this.playZombificationSound();
            this.zombify((ServerWorld)this.world);
        }
    }

    public boolean shouldZombify() {
        return !this.world.getDimension().isPiglinSafe() && !this.isImmuneToZombification() && !this.isAiDisabled();
    }

    protected void zombify(ServerWorld arg) {
        ZombifiedPiglinEntity lv = this.method_29243(EntityType.ZOMBIFIED_PIGLIN);
        lv.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Environment(value=EnvType.CLIENT)
    public abstract PiglinActivity getActivity();

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    protected boolean isHoldingTool() {
        return this.getMainHandStack().getItem() instanceof ToolItem;
    }

    @Override
    public void playAmbientSound() {
        if (PiglinBrain.hasIdleActivity(this)) {
            super.playAmbientSound();
        }
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    protected abstract void playZombificationSound();
}

