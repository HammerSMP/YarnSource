/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity
extends Entity {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    private Potion potion = Potions.EMPTY;
    private final List<StatusEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean customColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusGrowth;
    private LivingEntity owner;
    private UUID ownerUuid;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> arg, World arg2) {
        super(arg, arg2);
        this.noClip = true;
        this.setRadius(3.0f);
    }

    public AreaEffectCloudEntity(World world, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, world);
        this.updatePosition(x, y, z);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(COLOR, 0);
        this.getDataTracker().startTracking(RADIUS, Float.valueOf(0.5f));
        this.getDataTracker().startTracking(WAITING, false);
        this.getDataTracker().startTracking(PARTICLE_ID, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float radius) {
        if (!this.world.isClient) {
            this.getDataTracker().set(RADIUS, Float.valueOf(radius));
        }
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.updatePosition(d, e, f);
    }

    public float getRadius() {
        return this.getDataTracker().get(RADIUS).floatValue();
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
        if (!this.customColor) {
            this.updateColor();
        }
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataTracker().set(COLOR, 0);
        } else {
            this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        if (!this.customColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getDataTracker().get(COLOR);
    }

    public void setColor(int rgb) {
        this.customColor = true;
        this.getDataTracker().set(COLOR, rgb);
    }

    public ParticleEffect getParticleType() {
        return this.getDataTracker().get(PARTICLE_ID);
    }

    public void setParticleType(ParticleEffect particle) {
        this.getDataTracker().set(PARTICLE_ID, particle);
    }

    protected void setWaiting(boolean waiting) {
        this.getDataTracker().set(WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getDataTracker().get(WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void tick() {
        block23: {
            boolean bl2;
            float f;
            boolean bl;
            block21: {
                ParticleEffect lv;
                block22: {
                    super.tick();
                    bl = this.isWaiting();
                    f = this.getRadius();
                    if (!this.world.isClient) break block21;
                    lv = this.getParticleType();
                    if (!bl) break block22;
                    if (!this.random.nextBoolean()) break block23;
                    for (int i = 0; i < 2; ++i) {
                        float g = this.random.nextFloat() * ((float)Math.PI * 2);
                        float h = MathHelper.sqrt(this.random.nextFloat()) * 0.2f;
                        float j = MathHelper.cos(g) * h;
                        float k = MathHelper.sin(g) * h;
                        if (lv.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int l = this.random.nextBoolean() ? 0xFFFFFF : this.getColor();
                            int m = l >> 16 & 0xFF;
                            int n = l >> 8 & 0xFF;
                            int o = l & 0xFF;
                            this.world.addImportantParticle(lv, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, (float)m / 255.0f, (float)n / 255.0f, (float)o / 255.0f);
                            continue;
                        }
                        this.world.addImportantParticle(lv, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
                    }
                    break block23;
                }
                float p = (float)Math.PI * f * f;
                int q = 0;
                while ((float)q < p) {
                    float r = this.random.nextFloat() * ((float)Math.PI * 2);
                    float s = MathHelper.sqrt(this.random.nextFloat()) * f;
                    float t = MathHelper.cos(r) * s;
                    float u = MathHelper.sin(r) * s;
                    if (lv.getType() == ParticleTypes.ENTITY_EFFECT) {
                        int v = this.getColor();
                        int w = v >> 16 & 0xFF;
                        int x = v >> 8 & 0xFF;
                        int y = v & 0xFF;
                        this.world.addImportantParticle(lv, this.getX() + (double)t, this.getY(), this.getZ() + (double)u, (float)w / 255.0f, (float)x / 255.0f, (float)y / 255.0f);
                    } else {
                        this.world.addImportantParticle(lv, this.getX() + (double)t, this.getY(), this.getZ() + (double)u, (0.5 - this.random.nextDouble()) * 0.15, 0.01f, (0.5 - this.random.nextDouble()) * 0.15);
                    }
                    ++q;
                }
                break block23;
            }
            if (this.age >= this.waitTime + this.duration) {
                this.remove();
                return;
            }
            boolean bl3 = bl2 = this.age < this.waitTime;
            if (bl != bl2) {
                this.setWaiting(bl2);
            }
            if (bl2) {
                return;
            }
            if (this.radiusGrowth != 0.0f) {
                if ((f += this.radiusGrowth) < 0.5f) {
                    this.remove();
                    return;
                }
                this.setRadius(f);
            }
            if (this.age % 5 == 0) {
                Iterator<Map.Entry<Entity, Integer>> iterator = this.affectedEntities.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, Integer> entry = iterator.next();
                    if (this.age < entry.getValue()) continue;
                    iterator.remove();
                }
                ArrayList list = Lists.newArrayList();
                for (StatusEffectInstance lv2 : this.potion.getEffects()) {
                    list.add(new StatusEffectInstance(lv2.getEffectType(), lv2.getDuration() / 4, lv2.getAmplifier(), lv2.isAmbient(), lv2.shouldShowParticles()));
                }
                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.affectedEntities.clear();
                } else {
                    List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
                    if (!list2.isEmpty()) {
                        for (LivingEntity lv3 : list2) {
                            double e;
                            double d;
                            double z;
                            if (this.affectedEntities.containsKey(lv3) || !lv3.isAffectedBySplashPotions() || !((z = (d = lv3.getX() - this.getX()) * d + (e = lv3.getZ() - this.getZ()) * e) <= (double)(f * f))) continue;
                            this.affectedEntities.put(lv3, this.age + this.reapplicationDelay);
                            for (StatusEffectInstance lv4 : list) {
                                if (lv4.getEffectType().isInstant()) {
                                    lv4.getEffectType().applyInstantEffect(this, this.getOwner(), lv3, lv4.getAmplifier(), 0.5);
                                    continue;
                                }
                                lv3.addStatusEffect(new StatusEffectInstance(lv4));
                            }
                            if (this.radiusOnUse != 0.0f) {
                                if ((f += this.radiusOnUse) < 0.5f) {
                                    this.remove();
                                    return;
                                }
                                this.setRadius(f);
                            }
                            if (this.durationOnUse == 0) continue;
                            this.duration += this.durationOnUse;
                            if (this.duration > 0) continue;
                            this.remove();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void setRadiusOnUse(float radius) {
        this.radiusOnUse = radius;
    }

    public void setRadiusGrowth(float growth) {
        this.radiusGrowth = growth;
    }

    public void setWaitTime(int ticks) {
        this.waitTime = ticks;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity lv;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (lv = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)lv;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        this.age = tag.getInt("Age");
        this.duration = tag.getInt("Duration");
        this.waitTime = tag.getInt("WaitTime");
        this.reapplicationDelay = tag.getInt("ReapplicationDelay");
        this.durationOnUse = tag.getInt("DurationOnUse");
        this.radiusOnUse = tag.getFloat("RadiusOnUse");
        this.radiusGrowth = tag.getFloat("RadiusPerTick");
        this.setRadius(tag.getFloat("Radius"));
        if (tag.containsUuid("Owner")) {
            this.ownerUuid = tag.getUuid("Owner");
        }
        if (tag.contains("Particle", 8)) {
            try {
                this.setParticleType(ParticleArgumentType.readParameters(new StringReader(tag.getString("Particle"))));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                LOGGER.warn("Couldn't load custom particle {}", (Object)tag.getString("Particle"), (Object)commandSyntaxException);
            }
        }
        if (tag.contains("Color", 99)) {
            this.setColor(tag.getInt("Color"));
        }
        if (tag.contains("Potion", 8)) {
            this.setPotion(PotionUtil.getPotion(tag));
        }
        if (tag.contains("Effects", 9)) {
            ListTag lv = tag.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < lv.size(); ++i) {
                StatusEffectInstance lv2 = StatusEffectInstance.fromTag(lv.getCompound(i));
                if (lv2 == null) continue;
                this.addEffect(lv2);
            }
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putInt("Duration", this.duration);
        tag.putInt("WaitTime", this.waitTime);
        tag.putInt("ReapplicationDelay", this.reapplicationDelay);
        tag.putInt("DurationOnUse", this.durationOnUse);
        tag.putFloat("RadiusOnUse", this.radiusOnUse);
        tag.putFloat("RadiusPerTick", this.radiusGrowth);
        tag.putFloat("Radius", this.getRadius());
        tag.putString("Particle", this.getParticleType().asString());
        if (this.ownerUuid != null) {
            tag.putUuid("Owner", this.ownerUuid);
        }
        if (this.customColor) {
            tag.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY && this.potion != null) {
            tag.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            ListTag lv = new ListTag();
            for (StatusEffectInstance lv2 : this.effects) {
                lv.add(lv2.toTag(new CompoundTag()));
            }
            tag.put("Effects", lv);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (RADIUS.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0f, 0.5f);
    }
}

