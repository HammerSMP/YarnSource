/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity
extends MobEntity
implements Monster {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    public final double[][] segmentCircularBuffer = new double[64][3];
    public int latestSegment = -1;
    private final EnderDragonPart[] parts;
    public final EnderDragonPart partHead;
    private final EnderDragonPart partNeck;
    private final EnderDragonPart partBody;
    private final EnderDragonPart partTail1;
    private final EnderDragonPart partTail2;
    private final EnderDragonPart partTail3;
    private final EnderDragonPart partWingRight;
    private final EnderDragonPart partWingLeft;
    public float prevWingPosition;
    public float wingPosition;
    public boolean slowedDownByBlock;
    public int ticksSinceDeath;
    public float field_20865;
    @Nullable
    public EndCrystalEntity connectedCrystal;
    @Nullable
    private final EnderDragonFight fight;
    private final PhaseManager phaseManager;
    private int ticksUntilNextGrowl = 100;
    private int field_7029;
    private final PathNode[] pathNodes = new PathNode[24];
    private final int[] pathNodeConnections = new int[24];
    private final PathMinHeap pathHeap = new PathMinHeap();

    public EnderDragonEntity(EntityType<? extends EnderDragonEntity> arg, World arg2) {
        super((EntityType<? extends MobEntity>)EntityType.ENDER_DRAGON, arg2);
        this.partHead = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.partNeck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.partBody = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.partTail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partWingRight = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.partWingLeft = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.parts = new EnderDragonPart[]{this.partHead, this.partNeck, this.partBody, this.partTail1, this.partTail2, this.partTail3, this.partWingRight, this.partWingLeft};
        this.setHealth(this.getMaximumHealth());
        this.noClip = true;
        this.ignoreCameraFrustum = true;
        this.fight = arg2 instanceof ServerWorld ? ((ServerWorld)arg2).method_29198() : null;
        this.phaseManager = new PhaseManager(this);
    }

    public static DefaultAttributeContainer.Builder createEnderDragonAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.HOVER.getTypeId());
    }

    public double[] getSegmentProperties(int i, float f) {
        if (this.getHealth() <= 0.0f) {
            f = 0.0f;
        }
        f = 1.0f - f;
        int j = this.latestSegment - i & 0x3F;
        int k = this.latestSegment - i - 1 & 0x3F;
        double[] ds = new double[3];
        double d = this.segmentCircularBuffer[j][0];
        double e = MathHelper.wrapDegrees(this.segmentCircularBuffer[k][0] - d);
        ds[0] = d + e * (double)f;
        d = this.segmentCircularBuffer[j][1];
        e = this.segmentCircularBuffer[k][1] - d;
        ds[1] = d + e * (double)f;
        ds[2] = MathHelper.lerp((double)f, this.segmentCircularBuffer[j][2], this.segmentCircularBuffer[k][2]);
        return ds;
    }

    @Override
    public void tickMovement() {
        if (this.world.isClient) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                float f = MathHelper.cos(this.wingPosition * ((float)Math.PI * 2));
                float g = MathHelper.cos(this.prevWingPosition * ((float)Math.PI * 2));
                if (g <= -0.3f && f >= -0.3f) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
                }
                if (!this.phaseManager.getCurrent().isSittingOrHovering() && --this.ticksUntilNextGrowl < 0) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                    this.ticksUntilNextGrowl = 200 + this.random.nextInt(200);
                }
            }
        }
        this.prevWingPosition = this.wingPosition;
        if (this.getHealth() <= 0.0f) {
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            float i = (this.random.nextFloat() - 0.5f) * 4.0f;
            float j = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)h, this.getY() + 2.0 + (double)i, this.getZ() + (double)j, 0.0, 0.0, 0.0);
            return;
        }
        this.tickWithEndCrystals();
        Vec3d lv = this.getVelocity();
        float k = 0.2f / (MathHelper.sqrt(EnderDragonEntity.squaredHorizontalLength(lv)) * 10.0f + 1.0f);
        this.wingPosition = this.phaseManager.getCurrent().isSittingOrHovering() ? (this.wingPosition += 0.1f) : (this.slowedDownByBlock ? (this.wingPosition += k * 0.5f) : (this.wingPosition += (k *= (float)Math.pow(2.0, lv.y))));
        this.yaw = MathHelper.wrapDegrees(this.yaw);
        if (this.isAiDisabled()) {
            this.wingPosition = 0.5f;
            return;
        }
        if (this.latestSegment < 0) {
            for (int l = 0; l < this.segmentCircularBuffer.length; ++l) {
                this.segmentCircularBuffer[l][0] = this.yaw;
                this.segmentCircularBuffer[l][1] = this.getY();
            }
        }
        if (++this.latestSegment == this.segmentCircularBuffer.length) {
            this.latestSegment = 0;
        }
        this.segmentCircularBuffer[this.latestSegment][0] = this.yaw;
        this.segmentCircularBuffer[this.latestSegment][1] = this.getY();
        if (this.world.isClient) {
            if (this.bodyTrackingIncrements > 0) {
                double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
                double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
                double m = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
                double n = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
                this.yaw = (float)((double)this.yaw + n / (double)this.bodyTrackingIncrements);
                this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
                --this.bodyTrackingIncrements;
                this.updatePosition(d, e, m);
                this.setRotation(this.yaw, this.pitch);
            }
            this.phaseManager.getCurrent().clientTick();
        } else {
            Vec3d lv3;
            Phase lv2 = this.phaseManager.getCurrent();
            lv2.serverTick();
            if (this.phaseManager.getCurrent() != lv2) {
                lv2 = this.phaseManager.getCurrent();
                lv2.serverTick();
            }
            if ((lv3 = lv2.getTarget()) != null) {
                double o = lv3.x - this.getX();
                double p = lv3.y - this.getY();
                double q = lv3.z - this.getZ();
                double r = o * o + p * p + q * q;
                float s = lv2.getMaxYAcceleration();
                double t = MathHelper.sqrt(o * o + q * q);
                if (t > 0.0) {
                    p = MathHelper.clamp(p / t, (double)(-s), (double)s);
                }
                this.setVelocity(this.getVelocity().add(0.0, p * 0.01, 0.0));
                this.yaw = MathHelper.wrapDegrees(this.yaw);
                double u = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(o, q) * 57.2957763671875 - (double)this.yaw), -50.0, 50.0);
                Vec3d lv4 = lv3.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                Vec3d lv5 = new Vec3d(MathHelper.sin(this.yaw * ((float)Math.PI / 180)), this.getVelocity().y, -MathHelper.cos(this.yaw * ((float)Math.PI / 180))).normalize();
                float v = Math.max(((float)lv5.dotProduct(lv4) + 0.5f) / 1.5f, 0.0f);
                this.field_20865 *= 0.8f;
                this.field_20865 = (float)((double)this.field_20865 + u * (double)lv2.method_6847());
                this.yaw += this.field_20865 * 0.1f;
                float w = (float)(2.0 / (r + 1.0));
                float x = 0.06f;
                this.updateVelocity(0.06f * (v * w + (1.0f - w)), new Vec3d(0.0, 0.0, -1.0));
                if (this.slowedDownByBlock) {
                    this.move(MovementType.SELF, this.getVelocity().multiply(0.8f));
                } else {
                    this.move(MovementType.SELF, this.getVelocity());
                }
                Vec3d lv6 = this.getVelocity().normalize();
                double y = 0.8 + 0.15 * (lv6.dotProduct(lv5) + 1.0) / 2.0;
                this.setVelocity(this.getVelocity().multiply(y, 0.91f, y));
            }
        }
        this.bodyYaw = this.yaw;
        Vec3d[] lvs = new Vec3d[this.parts.length];
        for (int z = 0; z < this.parts.length; ++z) {
            lvs[z] = new Vec3d(this.parts[z].getX(), this.parts[z].getY(), this.parts[z].getZ());
        }
        float aa = (float)(this.getSegmentProperties(5, 1.0f)[1] - this.getSegmentProperties(10, 1.0f)[1]) * 10.0f * ((float)Math.PI / 180);
        float ab = MathHelper.cos(aa);
        float ac = MathHelper.sin(aa);
        float ad = this.yaw * ((float)Math.PI / 180);
        float ae = MathHelper.sin(ad);
        float af = MathHelper.cos(ad);
        this.movePart(this.partBody, ae * 0.5f, 0.0, -af * 0.5f);
        this.movePart(this.partWingRight, af * 4.5f, 2.0, ae * 4.5f);
        this.movePart(this.partWingLeft, af * -4.5f, 2.0, ae * -4.5f);
        if (!this.world.isClient && this.hurtTime == 0) {
            this.launchLivingEntities(this.world.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.launchLivingEntities(this.world.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.damageLivingEntities(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.damageLivingEntities(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
        float ag = MathHelper.sin(this.yaw * ((float)Math.PI / 180) - this.field_20865 * 0.01f);
        float ah = MathHelper.cos(this.yaw * ((float)Math.PI / 180) - this.field_20865 * 0.01f);
        float ai = this.method_6820();
        this.movePart(this.partHead, ag * 6.5f * ab, ai + ac * 6.5f, -ah * 6.5f * ab);
        this.movePart(this.partNeck, ag * 5.5f * ab, ai + ac * 5.5f, -ah * 5.5f * ab);
        double[] ds = this.getSegmentProperties(5, 1.0f);
        for (int aj = 0; aj < 3; ++aj) {
            EnderDragonPart lv7 = null;
            if (aj == 0) {
                lv7 = this.partTail1;
            }
            if (aj == 1) {
                lv7 = this.partTail2;
            }
            if (aj == 2) {
                lv7 = this.partTail3;
            }
            double[] es = this.getSegmentProperties(12 + aj * 2, 1.0f);
            float ak = this.yaw * ((float)Math.PI / 180) + this.wrapYawChange(es[0] - ds[0]) * ((float)Math.PI / 180);
            float al = MathHelper.sin(ak);
            float am = MathHelper.cos(ak);
            float an = 1.5f;
            float ao = (float)(aj + 1) * 2.0f;
            this.movePart(lv7, -(ae * 1.5f + al * ao) * ab, es[1] - ds[1] - (double)((ao + 1.5f) * ac) + 1.5, (af * 1.5f + am * ao) * ab);
        }
        if (!this.world.isClient) {
            this.slowedDownByBlock = this.destroyBlocks(this.partHead.getBoundingBox()) | this.destroyBlocks(this.partNeck.getBoundingBox()) | this.destroyBlocks(this.partBody.getBoundingBox());
            if (this.fight != null) {
                this.fight.updateFight(this);
            }
        }
        for (int ap = 0; ap < this.parts.length; ++ap) {
            this.parts[ap].prevX = lvs[ap].x;
            this.parts[ap].prevY = lvs[ap].y;
            this.parts[ap].prevZ = lvs[ap].z;
            this.parts[ap].lastRenderX = lvs[ap].x;
            this.parts[ap].lastRenderY = lvs[ap].y;
            this.parts[ap].lastRenderZ = lvs[ap].z;
        }
    }

    private void movePart(EnderDragonPart arg, double d, double e, double f) {
        arg.updatePosition(this.getX() + d, this.getY() + e, this.getZ() + f);
    }

    private float method_6820() {
        if (this.phaseManager.getCurrent().isSittingOrHovering()) {
            return -1.0f;
        }
        double[] ds = this.getSegmentProperties(5, 1.0f);
        double[] es = this.getSegmentProperties(0, 1.0f);
        return (float)(ds[1] - es[1]);
    }

    private void tickWithEndCrystals() {
        if (this.connectedCrystal != null) {
            if (this.connectedCrystal.removed) {
                this.connectedCrystal = null;
            } else if (this.age % 10 == 0 && this.getHealth() < this.getMaximumHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, this.getBoundingBox().expand(32.0));
            EndCrystalEntity lv = null;
            double d = Double.MAX_VALUE;
            for (EndCrystalEntity lv2 : list) {
                double e = lv2.squaredDistanceTo(this);
                if (!(e < d)) continue;
                d = e;
                lv = lv2;
            }
            this.connectedCrystal = lv;
        }
    }

    private void launchLivingEntities(List<Entity> list) {
        double d = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
        double e = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;
        for (Entity lv : list) {
            if (!(lv instanceof LivingEntity)) continue;
            double f = lv.getX() - d;
            double g = lv.getZ() - e;
            double h = f * f + g * g;
            lv.addVelocity(f / h * 4.0, 0.2f, g / h * 4.0);
            if (this.phaseManager.getCurrent().isSittingOrHovering() || ((LivingEntity)lv).getLastAttackedTime() >= lv.age - 2) continue;
            lv.damage(DamageSource.mob(this), 5.0f);
            this.dealDamage(this, lv);
        }
    }

    private void damageLivingEntities(List<Entity> list) {
        for (Entity lv : list) {
            if (!(lv instanceof LivingEntity)) continue;
            lv.damage(DamageSource.mob(this), 10.0f);
            this.dealDamage(this, lv);
        }
    }

    private float wrapYawChange(double d) {
        return (float)MathHelper.wrapDegrees(d);
    }

    private boolean destroyBlocks(Box arg) {
        int i = MathHelper.floor(arg.minX);
        int j = MathHelper.floor(arg.minY);
        int k = MathHelper.floor(arg.minZ);
        int l = MathHelper.floor(arg.maxX);
        int m = MathHelper.floor(arg.maxY);
        int n = MathHelper.floor(arg.maxZ);
        boolean bl = false;
        boolean bl2 = false;
        for (int o = i; o <= l; ++o) {
            for (int p = j; p <= m; ++p) {
                for (int q = k; q <= n; ++q) {
                    BlockPos lv = new BlockPos(o, p, q);
                    BlockState lv2 = this.world.getBlockState(lv);
                    Block lv3 = lv2.getBlock();
                    if (lv2.isAir() || lv2.getMaterial() == Material.FIRE) continue;
                    if (!this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) || BlockTags.DRAGON_IMMUNE.contains(lv3)) {
                        bl = true;
                        continue;
                    }
                    bl2 = this.world.removeBlock(lv, false) || bl2;
                }
            }
        }
        if (bl2) {
            BlockPos lv4 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
            this.world.syncWorldEvent(2008, lv4, 0);
        }
        return bl;
    }

    public boolean damagePart(EnderDragonPart arg, DamageSource arg2, float f) {
        if (this.phaseManager.getCurrent().getType() == PhaseType.DYING) {
            return false;
        }
        f = this.phaseManager.getCurrent().modifyDamageTaken(arg2, f);
        if (arg != this.partHead) {
            f = f / 4.0f + Math.min(f, 1.0f);
        }
        if (f < 0.01f) {
            return false;
        }
        if (arg2.getAttacker() instanceof PlayerEntity || arg2.isExplosive()) {
            float g = this.getHealth();
            this.parentDamage(arg2, f);
            if (this.getHealth() <= 0.0f && !this.phaseManager.getCurrent().isSittingOrHovering()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(PhaseType.DYING);
            }
            if (this.phaseManager.getCurrent().isSittingOrHovering()) {
                this.field_7029 = (int)((float)this.field_7029 + (g - this.getHealth()));
                if ((float)this.field_7029 > 0.25f * this.getMaximumHealth()) {
                    this.field_7029 = 0;
                    this.phaseManager.setPhase(PhaseType.TAKEOFF);
                }
            }
        }
        return true;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (arg instanceof EntityDamageSource && ((EntityDamageSource)arg).isThorns()) {
            this.damagePart(this.partBody, arg, f);
        }
        return false;
    }

    protected boolean parentDamage(DamageSource arg, float f) {
        return super.damage(arg, f);
    }

    @Override
    public void kill() {
        this.remove();
        if (this.fight != null) {
            this.fight.updateFight(this);
            this.fight.dragonKilled(this);
        }
    }

    @Override
    protected void updatePostDeath() {
        if (this.fight != null) {
            this.fight.updateFight(this);
        }
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath >= 180 && this.ticksSinceDeath <= 200) {
            float f = (this.random.nextFloat() - 0.5f) * 8.0f;
            float g = (this.random.nextFloat() - 0.5f) * 4.0f;
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
        }
        boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        int i = 500;
        if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
            i = 12000;
        }
        if (!this.world.isClient) {
            if (this.ticksSinceDeath > 150 && this.ticksSinceDeath % 5 == 0 && bl) {
                this.awardExperience(MathHelper.floor((float)i * 0.08f));
            }
            if (this.ticksSinceDeath == 1 && !this.isSilent()) {
                this.world.syncGlobalEvent(1028, this.getBlockPos(), 0);
            }
        }
        this.move(MovementType.SELF, new Vec3d(0.0, 0.1f, 0.0));
        this.yaw += 20.0f;
        this.bodyYaw = this.yaw;
        if (this.ticksSinceDeath == 200 && !this.world.isClient) {
            if (bl) {
                this.awardExperience(MathHelper.floor((float)i * 0.2f));
            }
            if (this.fight != null) {
                this.fight.dragonKilled(this);
            }
            this.remove();
        }
    }

    private void awardExperience(int i) {
        while (i > 0) {
            int j = ExperienceOrbEntity.roundToOrbSize(i);
            i -= j;
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
        }
    }

    public int getNearestPathNodeIndex() {
        if (this.pathNodes[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int q;
                int p;
                int j = 5;
                int k = i;
                if (i < 12) {
                    int l = MathHelper.floor(60.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                    int m = MathHelper.floor(60.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                } else if (i < 20) {
                    int n = MathHelper.floor(40.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)(k -= 12))));
                    int o = MathHelper.floor(40.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)k)));
                    j += 10;
                } else {
                    p = MathHelper.floor(20.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)(k -= 20))));
                    q = MathHelper.floor(20.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)k)));
                }
                int r = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(p, 0, q)).getY() + j);
                this.pathNodes[i] = new PathNode(p, r, q);
            }
            this.pathNodeConnections[0] = 6146;
            this.pathNodeConnections[1] = 8197;
            this.pathNodeConnections[2] = 8202;
            this.pathNodeConnections[3] = 16404;
            this.pathNodeConnections[4] = 32808;
            this.pathNodeConnections[5] = 32848;
            this.pathNodeConnections[6] = 65696;
            this.pathNodeConnections[7] = 131392;
            this.pathNodeConnections[8] = 131712;
            this.pathNodeConnections[9] = 263424;
            this.pathNodeConnections[10] = 526848;
            this.pathNodeConnections[11] = 525313;
            this.pathNodeConnections[12] = 1581057;
            this.pathNodeConnections[13] = 3166214;
            this.pathNodeConnections[14] = 2138120;
            this.pathNodeConnections[15] = 6373424;
            this.pathNodeConnections[16] = 4358208;
            this.pathNodeConnections[17] = 12910976;
            this.pathNodeConnections[18] = 9044480;
            this.pathNodeConnections[19] = 9706496;
            this.pathNodeConnections[20] = 15216640;
            this.pathNodeConnections[21] = 0xD0E000;
            this.pathNodeConnections[22] = 11763712;
            this.pathNodeConnections[23] = 0x7E0000;
        }
        return this.getNearestPathNodeIndex(this.getX(), this.getY(), this.getZ());
    }

    public int getNearestPathNodeIndex(double d, double e, double f) {
        float g = 10000.0f;
        int i = 0;
        PathNode lv = new PathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        int j = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            j = 12;
        }
        for (int k = j; k < 24; ++k) {
            float h;
            if (this.pathNodes[k] == null || !((h = this.pathNodes[k].getSquaredDistance(lv)) < g)) continue;
            g = h;
            i = k;
        }
        return i;
    }

    @Nullable
    public Path findPath(int i, int j, @Nullable PathNode arg) {
        for (int k = 0; k < 24; ++k) {
            PathNode lv = this.pathNodes[k];
            lv.visited = false;
            lv.heapWeight = 0.0f;
            lv.penalizedPathLength = 0.0f;
            lv.distanceToNearestTarget = 0.0f;
            lv.previous = null;
            lv.heapIndex = -1;
        }
        PathNode lv2 = this.pathNodes[i];
        PathNode lv3 = this.pathNodes[j];
        lv2.penalizedPathLength = 0.0f;
        lv2.heapWeight = lv2.distanceToNearestTarget = lv2.getDistance(lv3);
        this.pathHeap.clear();
        this.pathHeap.push(lv2);
        PathNode lv4 = lv2;
        int l = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            l = 12;
        }
        while (!this.pathHeap.isEmpty()) {
            PathNode lv5 = this.pathHeap.pop();
            if (lv5.equals(lv3)) {
                if (arg != null) {
                    arg.previous = lv3;
                    lv3 = arg;
                }
                return this.getPathOfAllPredecessors(lv2, lv3);
            }
            if (lv5.getDistance(lv3) < lv4.getDistance(lv3)) {
                lv4 = lv5;
            }
            lv5.visited = true;
            int m = 0;
            for (int n = 0; n < 24; ++n) {
                if (this.pathNodes[n] != lv5) continue;
                m = n;
                break;
            }
            for (int o = l; o < 24; ++o) {
                if ((this.pathNodeConnections[m] & 1 << o) <= 0) continue;
                PathNode lv6 = this.pathNodes[o];
                if (lv6.visited) continue;
                float f = lv5.penalizedPathLength + lv5.getDistance(lv6);
                if (lv6.isInHeap() && !(f < lv6.penalizedPathLength)) continue;
                lv6.previous = lv5;
                lv6.penalizedPathLength = f;
                lv6.distanceToNearestTarget = lv6.getDistance(lv3);
                if (lv6.isInHeap()) {
                    this.pathHeap.setNodeWeight(lv6, lv6.penalizedPathLength + lv6.distanceToNearestTarget);
                    continue;
                }
                lv6.heapWeight = lv6.penalizedPathLength + lv6.distanceToNearestTarget;
                this.pathHeap.push(lv6);
            }
        }
        if (lv4 == lv2) {
            return null;
        }
        LOGGER.debug("Failed to find path from {} to {}", (Object)i, (Object)j);
        if (arg != null) {
            arg.previous = lv4;
            lv4 = arg;
        }
        return this.getPathOfAllPredecessors(lv2, lv4);
    }

    private Path getPathOfAllPredecessors(PathNode arg, PathNode arg2) {
        ArrayList list = Lists.newArrayList();
        PathNode lv = arg2;
        list.add(0, lv);
        while (lv.previous != null) {
            lv = lv.previous;
            list.add(0, lv);
        }
        return new Path(list, new BlockPos(arg2.x, arg2.y, arg2.z), true);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("DragonPhase")) {
            this.phaseManager.setPhase(PhaseType.getFromId(arg.getInt("DragonPhase")));
        }
    }

    @Override
    public void checkDespawn() {
    }

    public EnderDragonPart[] getBodyParts() {
        return this.parts;
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6823(int i, double[] ds, double[] es) {
        double h;
        Phase lv = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> lv2 = lv.getType();
        if (lv2 == PhaseType.LANDING || lv2 == PhaseType.TAKEOFF) {
            BlockPos lv3 = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float f = Math.max(MathHelper.sqrt(lv3.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            double d = (float)i / f;
        } else if (lv.isSittingOrHovering()) {
            double e = i;
        } else if (i == 6) {
            double g = 0.0;
        } else {
            h = es[1] - ds[1];
        }
        return (float)h;
    }

    public Vec3d method_6834(float f) {
        Vec3d lv6;
        Phase lv = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> lv2 = lv.getType();
        if (lv2 == PhaseType.LANDING || lv2 == PhaseType.TAKEOFF) {
            BlockPos lv3 = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float g = Math.max(MathHelper.sqrt(lv3.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            float h = 6.0f / g;
            float i = this.pitch;
            float j = 1.5f;
            this.pitch = -h * 1.5f * 5.0f;
            Vec3d lv4 = this.getRotationVec(f);
            this.pitch = i;
        } else if (lv.isSittingOrHovering()) {
            float k = this.pitch;
            float l = 1.5f;
            this.pitch = -45.0f;
            Vec3d lv5 = this.getRotationVec(f);
            this.pitch = k;
        } else {
            lv6 = this.getRotationVec(f);
        }
        return lv6;
    }

    public void crystalDestroyed(EndCrystalEntity arg, BlockPos arg2, DamageSource arg3) {
        PlayerEntity lv2;
        if (arg3.getAttacker() instanceof PlayerEntity) {
            PlayerEntity lv = (PlayerEntity)arg3.getAttacker();
        } else {
            lv2 = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, arg2.getX(), arg2.getY(), arg2.getZ());
        }
        if (arg == this.connectedCrystal) {
            this.damagePart(this.partHead, DamageSource.explosion(lv2), 10.0f);
        }
        this.phaseManager.getCurrent().crystalDestroyed(arg, arg2, arg3, lv2);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (PHASE_TYPE.equals(arg) && this.world.isClient) {
            this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
        }
        super.onTrackedDataSet(arg);
    }

    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public EnderDragonFight getFight() {
        return this.fight;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance arg) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity arg) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }
}

