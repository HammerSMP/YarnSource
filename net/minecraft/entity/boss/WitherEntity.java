/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=SkinOverlayOwner.class)})
public class WitherEntity
extends HostileEntity
implements SkinOverlayOwner,
RangedAttackMob {
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_1 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_2 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_3 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final List<TrackedData<Integer>> TRACKED_ENTITY_IDS = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final float[] sideHeadPitches = new float[2];
    private final float[] sideHeadYaws = new float[2];
    private final float[] prevSideHeadPitches = new float[2];
    private final float[] prevSideHeadYaws = new float[2];
    private final int[] field_7091 = new int[2];
    private final int[] field_7092 = new int[2];
    private int field_7082;
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE = arg -> arg.getGroup() != EntityGroup.UNDEAD && arg.isMobOrPlayer();
    private static final TargetPredicate HEAD_TARGET_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0).setPredicate(CAN_ATTACK_PREDICATE);

    public WitherEntity(EntityType<? extends WitherEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.setHealth(this.getMaxHealth());
        this.getNavigation().setCanSwim(true);
        this.experiencePoints = 50;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new DescendAtHalfHealthGoal());
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<MobEntity>(this, MobEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_1, 0);
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_2, 0);
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_3, 0);
        this.dataTracker.startTracking(INVUL_TIMER, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Invul", this.getInvulnerableTimer());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setInvulTimer(arg.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text arg) {
        super.setCustomName(arg);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    public void tickMovement() {
        Entity lv2;
        Vec3d lv = this.getVelocity().multiply(1.0, 0.6, 1.0);
        if (!this.world.isClient && this.getTrackedEntityId(0) > 0 && (lv2 = this.world.getEntityById(this.getTrackedEntityId(0))) != null) {
            double d = lv.y;
            if (this.getY() < lv2.getY() || !this.shouldRenderOverlay() && this.getY() < lv2.getY() + 5.0) {
                d = Math.max(0.0, d);
                d += 0.3 - d * (double)0.6f;
            }
            lv = new Vec3d(lv.x, d, lv.z);
            Vec3d lv3 = new Vec3d(lv2.getX() - this.getX(), 0.0, lv2.getZ() - this.getZ());
            if (WitherEntity.squaredHorizontalLength(lv3) > 9.0) {
                Vec3d lv4 = lv3.normalize();
                lv = lv.add(lv4.x * 0.3 - lv.x * 0.6, 0.0, lv4.z * 0.3 - lv.z * 0.6);
            }
        }
        this.setVelocity(lv);
        if (WitherEntity.squaredHorizontalLength(lv) > 0.05) {
            this.yaw = (float)MathHelper.atan2(lv.z, lv.x) * 57.295776f - 90.0f;
        }
        super.tickMovement();
        for (int i = 0; i < 2; ++i) {
            this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
            this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
        }
        for (int j = 0; j < 2; ++j) {
            int k = this.getTrackedEntityId(j + 1);
            Entity lv5 = null;
            if (k > 0) {
                lv5 = this.world.getEntityById(k);
            }
            if (lv5 != null) {
                double e = this.getHeadX(j + 1);
                double f = this.getHeadY(j + 1);
                double g = this.getHeadZ(j + 1);
                double h = lv5.getX() - e;
                double l = lv5.getEyeY() - f;
                double m = lv5.getZ() - g;
                double n = MathHelper.sqrt(h * h + m * m);
                float o = (float)(MathHelper.atan2(m, h) * 57.2957763671875) - 90.0f;
                float p = (float)(-(MathHelper.atan2(l, n) * 57.2957763671875));
                this.sideHeadPitches[j] = this.getNextAngle(this.sideHeadPitches[j], p, 40.0f);
                this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], o, 10.0f);
                continue;
            }
            this.sideHeadYaws[j] = this.getNextAngle(this.sideHeadYaws[j], this.bodyYaw, 10.0f);
        }
        boolean bl = this.shouldRenderOverlay();
        for (int q = 0; q < 3; ++q) {
            double r = this.getHeadX(q);
            double s = this.getHeadY(q);
            double t = this.getHeadZ(q);
            this.world.addParticle(ParticleTypes.SMOKE, r + this.random.nextGaussian() * (double)0.3f, s + this.random.nextGaussian() * (double)0.3f, t + this.random.nextGaussian() * (double)0.3f, 0.0, 0.0, 0.0);
            if (!bl || this.world.random.nextInt(4) != 0) continue;
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, r + this.random.nextGaussian() * (double)0.3f, s + this.random.nextGaussian() * (double)0.3f, t + this.random.nextGaussian() * (double)0.3f, 0.7f, 0.7f, 0.5);
        }
        if (this.getInvulnerableTimer() > 0) {
            for (int u = 0; u < 3; ++u) {
                this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3f), this.getZ() + this.random.nextGaussian(), 0.7f, 0.7f, 0.9f);
            }
        }
    }

    @Override
    protected void mobTick() {
        if (this.getInvulnerableTimer() > 0) {
            int i = this.getInvulnerableTimer() - 1;
            if (i <= 0) {
                Explosion.DestructionType lv = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
                this.world.createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0f, false, lv);
                if (!this.isSilent()) {
                    this.world.syncGlobalEvent(1023, this.getBlockPos(), 0);
                }
            }
            this.setInvulTimer(i);
            if (this.age % 10 == 0) {
                this.heal(10.0f);
            }
            return;
        }
        super.mobTick();
        block0: for (int j = 1; j < 3; ++j) {
            int k;
            if (this.age < this.field_7091[j - 1]) continue;
            this.field_7091[j - 1] = this.age + 10 + this.random.nextInt(10);
            if (this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) {
                int n = j - 1;
                int n2 = this.field_7092[n];
                this.field_7092[n] = n2 + 1;
                if (n2 > 15) {
                    float f = 10.0f;
                    float g = 5.0f;
                    double d = MathHelper.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                    double e = MathHelper.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                    double h = MathHelper.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                    this.method_6877(j + 1, d, e, h, true);
                    this.field_7092[j - 1] = 0;
                }
            }
            if ((k = this.getTrackedEntityId(j)) > 0) {
                Entity lv2 = this.world.getEntityById(k);
                if (lv2 == null || !lv2.isAlive() || this.squaredDistanceTo(lv2) > 900.0 || !this.canSee(lv2)) {
                    this.setTrackedEntityId(j, 0);
                    continue;
                }
                if (lv2 instanceof PlayerEntity && ((PlayerEntity)lv2).abilities.invulnerable) {
                    this.setTrackedEntityId(j, 0);
                    continue;
                }
                this.method_6878(j + 1, (LivingEntity)lv2);
                this.field_7091[j - 1] = this.age + 40 + this.random.nextInt(20);
                this.field_7092[j - 1] = 0;
                continue;
            }
            List<LivingEntity> list = this.world.getTargets(LivingEntity.class, HEAD_TARGET_PREDICATE, this, this.getBoundingBox().expand(20.0, 8.0, 20.0));
            for (int l = 0; l < 10 && !list.isEmpty(); ++l) {
                LivingEntity lv3 = list.get(this.random.nextInt(list.size()));
                if (lv3 != this && lv3.isAlive() && this.canSee(lv3)) {
                    if (lv3 instanceof PlayerEntity) {
                        if (((PlayerEntity)lv3).abilities.invulnerable) continue block0;
                        this.setTrackedEntityId(j, lv3.getEntityId());
                        continue block0;
                    }
                    this.setTrackedEntityId(j, lv3.getEntityId());
                    continue block0;
                }
                list.remove(lv3);
            }
        }
        if (this.getTarget() != null) {
            this.setTrackedEntityId(0, this.getTarget().getEntityId());
        } else {
            this.setTrackedEntityId(0, 0);
        }
        if (this.field_7082 > 0) {
            --this.field_7082;
            if (this.field_7082 == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                int m = MathHelper.floor(this.getY());
                int n = MathHelper.floor(this.getX());
                int o = MathHelper.floor(this.getZ());
                boolean bl = false;
                for (int p = -1; p <= 1; ++p) {
                    for (int q = -1; q <= 1; ++q) {
                        for (int r = 0; r <= 3; ++r) {
                            int s = n + p;
                            int t = m + r;
                            int u = o + q;
                            BlockPos lv4 = new BlockPos(s, t, u);
                            BlockState lv5 = this.world.getBlockState(lv4);
                            if (!WitherEntity.canDestroy(lv5)) continue;
                            bl = this.world.breakBlock(lv4, true, this) || bl;
                        }
                    }
                }
                if (bl) {
                    this.world.syncWorldEvent(null, 1022, this.getBlockPos(), 0);
                }
            }
        }
        if (this.age % 20 == 0) {
            this.heal(1.0f);
        }
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public static boolean canDestroy(BlockState arg) {
        return !arg.isAir() && !BlockTags.WITHER_IMMUNE.contains(arg.getBlock());
    }

    public void method_6885() {
        this.setInvulTimer(220);
        this.setHealth(this.getMaxHealth() / 3.0f);
    }

    @Override
    public void slowMovement(BlockState arg, Vec3d arg2) {
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity arg) {
        super.onStartedTrackingBy(arg);
        this.bossBar.addPlayer(arg);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity arg) {
        super.onStoppedTrackingBy(arg);
        this.bossBar.removePlayer(arg);
    }

    private double getHeadX(int i) {
        if (i <= 0) {
            return this.getX();
        }
        float f = (this.bodyYaw + (float)(180 * (i - 1))) * ((float)Math.PI / 180);
        float g = MathHelper.cos(f);
        return this.getX() + (double)g * 1.3;
    }

    private double getHeadY(int i) {
        if (i <= 0) {
            return this.getY() + 3.0;
        }
        return this.getY() + 2.2;
    }

    private double getHeadZ(int i) {
        if (i <= 0) {
            return this.getZ();
        }
        float f = (this.bodyYaw + (float)(180 * (i - 1))) * ((float)Math.PI / 180);
        float g = MathHelper.sin(f);
        return this.getZ() + (double)g * 1.3;
    }

    private float getNextAngle(float f, float g, float h) {
        float i = MathHelper.wrapDegrees(g - f);
        if (i > h) {
            i = h;
        }
        if (i < -h) {
            i = -h;
        }
        return f + i;
    }

    private void method_6878(int i, LivingEntity arg) {
        this.method_6877(i, arg.getX(), arg.getY() + (double)arg.getStandingEyeHeight() * 0.5, arg.getZ(), i == 0 && this.random.nextFloat() < 0.001f);
    }

    private void method_6877(int i, double d, double e, double f, boolean bl) {
        if (!this.isSilent()) {
            this.world.syncWorldEvent(null, 1024, this.getBlockPos(), 0);
        }
        double g = this.getHeadX(i);
        double h = this.getHeadY(i);
        double j = this.getHeadZ(i);
        double k = d - g;
        double l = e - h;
        double m = f - j;
        WitherSkullEntity lv = new WitherSkullEntity(this.world, this, k, l, m);
        lv.setOwner(this);
        if (bl) {
            lv.setCharged(true);
        }
        lv.setPos(g, h, j);
        this.world.spawnEntity(lv);
    }

    @Override
    public void attack(LivingEntity arg, float f) {
        this.method_6878(0, arg);
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        Entity lv;
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (arg == DamageSource.DROWN || arg.getAttacker() instanceof WitherEntity) {
            return false;
        }
        if (this.getInvulnerableTimer() > 0 && arg != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (this.shouldRenderOverlay() && (lv = arg.getSource()) instanceof PersistentProjectileEntity) {
            return false;
        }
        Entity lv2 = arg.getAttacker();
        if (lv2 != null && !(lv2 instanceof PlayerEntity) && lv2 instanceof LivingEntity && ((LivingEntity)lv2).getGroup() == this.getGroup()) {
            return false;
        }
        if (this.field_7082 <= 0) {
            this.field_7082 = 20;
        }
        int i = 0;
        while (i < this.field_7092.length) {
            int n = i++;
            this.field_7092[n] = this.field_7092[n] + 3;
        }
        return super.damage(arg, f);
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        super.dropEquipment(arg, i, bl);
        ItemEntity lv = this.dropItem(Items.NETHER_STAR);
        if (lv != null) {
            lv.setCovetedItem();
        }
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.remove();
            return;
        }
        this.despawnCounter = 0;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance arg) {
        return false;
    }

    public static DefaultAttributeContainer.Builder createWitherAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0).add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadYaw(int i) {
        return this.sideHeadYaws[i];
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadPitch(int i) {
        return this.sideHeadPitches[i];
    }

    public int getInvulnerableTimer() {
        return this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int i) {
        this.dataTracker.set(INVUL_TIMER, i);
    }

    public int getTrackedEntityId(int i) {
        return this.dataTracker.get(TRACKED_ENTITY_IDS.get(i));
    }

    public void setTrackedEntityId(int i, int j) {
        this.dataTracker.set(TRACKED_ENTITY_IDS.get(i), j);
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.getHealth() <= this.getMaxHealth() / 2.0f;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected boolean canStartRiding(Entity arg) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance arg) {
        if (arg.getEffectType() == StatusEffects.WITHER) {
            return false;
        }
        return super.canHaveStatusEffect(arg);
    }

    class DescendAtHalfHealthGoal
    extends Goal {
        public DescendAtHalfHealthGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return WitherEntity.this.getInvulnerableTimer() > 0;
        }
    }
}

