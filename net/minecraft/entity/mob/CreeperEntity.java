/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.mob;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=SkinOverlayOwner.class)})
public class CreeperEntity
extends HostileEntity
implements SkinOverlayOwner {
    private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime = 30;
    private int explosionRadius = 3;
    private int headsDropped;

    public CreeperEntity(EntityType<? extends CreeperEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CreeperIgniteGoal(this));
        this.goalSelector.add(3, new FleeEntityGoal<OcelotEntity>(this, OcelotEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<CatEntity>(this, CatEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createCreeperAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0f);
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        boolean bl = super.handleFallDamage(f, g);
        this.currentFuseTime = (int)((float)this.currentFuseTime + f * 1.5f);
        if (this.currentFuseTime > this.fuseTime - 5) {
            this.currentFuseTime = this.fuseTime - 5;
        }
        return bl;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE_SPEED, -1);
        this.dataTracker.startTracking(CHARGED, false);
        this.dataTracker.startTracking(IGNITED, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.dataTracker.get(CHARGED).booleanValue()) {
            arg.putBoolean("powered", true);
        }
        arg.putShort("Fuse", (short)this.fuseTime);
        arg.putByte("ExplosionRadius", (byte)this.explosionRadius);
        arg.putBoolean("ignited", this.getIgnited());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.dataTracker.set(CHARGED, arg.getBoolean("powered"));
        if (arg.contains("Fuse", 99)) {
            this.fuseTime = arg.getShort("Fuse");
        }
        if (arg.contains("ExplosionRadius", 99)) {
            this.explosionRadius = arg.getByte("ExplosionRadius");
        }
        if (arg.getBoolean("ignited")) {
            this.setIgnited();
        }
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            int i;
            this.lastFuseTime = this.currentFuseTime;
            if (this.getIgnited()) {
                this.setFuseSpeed(1);
            }
            if ((i = this.getFuseSpeed()) > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
            }
            this.currentFuseTime += i;
            if (this.currentFuseTime < 0) {
                this.currentFuseTime = 0;
            }
            if (this.currentFuseTime >= this.fuseTime) {
                this.currentFuseTime = this.fuseTime;
                this.explode();
            }
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREEPER_DEATH;
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        CreeperEntity lv2;
        super.dropEquipment(arg, i, bl);
        Entity lv = arg.getAttacker();
        if (lv != this && lv instanceof CreeperEntity && (lv2 = (CreeperEntity)lv).shouldDropHead()) {
            lv2.onHeadDropped();
            this.dropItem(Items.CREEPER_HEAD);
        }
    }

    @Override
    public boolean tryAttack(Entity arg) {
        return true;
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.dataTracker.get(CHARGED);
    }

    @Environment(value=EnvType.CLIENT)
    public float getClientFuseTime(float f) {
        return MathHelper.lerp(f, this.lastFuseTime, this.currentFuseTime) / (float)(this.fuseTime - 2);
    }

    public int getFuseSpeed() {
        return this.dataTracker.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int i) {
        this.dataTracker.set(FUSE_SPEED, i);
    }

    @Override
    public void onStruckByLightning(LightningEntity arg) {
        super.onStruckByLightning(arg);
        this.dataTracker.set(CHARGED, true);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity arg, Hand arg22) {
        ItemStack lv = arg.getStackInHand(arg22);
        if (lv.getItem() == Items.FLINT_AND_STEEL) {
            this.world.playSound(arg, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            if (!this.world.isClient) {
                this.setIgnited();
                lv.damage(1, arg, arg2 -> arg2.sendToolBreakStatus(arg22));
            }
            return ActionResult.method_29236(this.world.isClient);
        }
        return super.interactMob(arg, arg22);
    }

    private void explode() {
        if (!this.world.isClient) {
            Explosion.DestructionType lv = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
            float f = this.shouldRenderOverlay() ? 2.0f : 1.0f;
            this.dead = true;
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, lv);
            this.remove();
            this.spawnEffectsCloud();
        }
    }

    private void spawnEffectsCloud() {
        Collection<StatusEffectInstance> collection = this.getStatusEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity lv = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
            lv.setRadius(2.5f);
            lv.setRadiusOnUse(-0.5f);
            lv.setWaitTime(10);
            lv.setDuration(lv.getDuration() / 2);
            lv.setRadiusGrowth(-lv.getRadius() / (float)lv.getDuration());
            for (StatusEffectInstance lv2 : collection) {
                lv.addEffect(new StatusEffectInstance(lv2));
            }
            this.world.spawnEntity(lv);
        }
    }

    public boolean getIgnited() {
        return this.dataTracker.get(IGNITED);
    }

    public void setIgnited() {
        this.dataTracker.set(IGNITED, true);
    }

    public boolean shouldDropHead() {
        return this.shouldRenderOverlay() && this.headsDropped < 1;
    }

    public void onHeadDropped() {
        ++this.headsDropped;
    }
}

