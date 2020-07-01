/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4837;
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
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public abstract class class_5418
extends HostileEntity {
    protected static final TrackedData<Boolean> field_25758 = DataTracker.registerData(class_5418.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int field_25759 = 0;

    public class_5418(EntityType<? extends class_5418> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.setCanPickUpLoot(true);
        this.method_30239();
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
    }

    private void method_30239() {
        EntityNavigation lv = this.getNavigation();
        if (lv instanceof MobNavigation) {
            ((MobNavigation)lv).setCanPathThroughDoors(true);
        }
    }

    protected abstract boolean canHunt();

    public void method_30240(boolean bl) {
        this.getDataTracker().set(field_25758, bl);
    }

    protected boolean method_30234() {
        return this.getDataTracker().get(field_25758);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(field_25758, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.method_30234()) {
            arg.putBoolean("IsImmuneToZombification", true);
        }
        arg.putInt("TimeInOverworld", this.field_25759);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.method_30240(arg.getBoolean("IsImmuneToZombification"));
        this.field_25759 = arg.getInt("TimeInOverworld");
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        this.field_25759 = this.method_30235() ? ++this.field_25759 : 0;
        if (this.field_25759 > 300) {
            this.method_30238();
            this.zombify((ServerWorld)this.world);
        }
    }

    public boolean method_30235() {
        return !this.world.getDimension().isPiglinSafe() && !this.method_30234() && !this.isAiDisabled();
    }

    protected void zombify(ServerWorld arg) {
        ZombifiedPiglinEntity lv = this.method_29243(EntityType.ZOMBIFIED_PIGLIN);
        lv.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
    }

    public boolean method_30236() {
        return !this.isBaby();
    }

    @Environment(value=EnvType.CLIENT)
    public abstract class_4837 getActivity();

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    protected boolean method_30237() {
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

    protected abstract void method_30238();
}

