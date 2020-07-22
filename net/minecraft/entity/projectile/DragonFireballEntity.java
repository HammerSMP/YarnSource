/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class DragonFireballEntity
extends ExplosiveProjectileEntity {
    public DragonFireballEntity(EntityType<? extends DragonFireballEntity> arg, World arg2) {
        super((EntityType<? extends ExplosiveProjectileEntity>)arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public DragonFireballEntity(World world, double x, double y, double z, double directionX, double directionY, double directionZ) {
        super(EntityType.DRAGON_FIREBALL, x, y, z, directionX, directionY, directionZ, world);
    }

    public DragonFireballEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(EntityType.DRAGON_FIREBALL, owner, directionX, directionY, directionZ, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        Entity lv = this.getOwner();
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity().isPartOf(lv)) {
            return;
        }
        if (!this.world.isClient) {
            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0));
            AreaEffectCloudEntity lv2 = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
            if (lv instanceof LivingEntity) {
                lv2.setOwner((LivingEntity)lv);
            }
            lv2.setParticleType(ParticleTypes.DRAGON_BREATH);
            lv2.setRadius(3.0f);
            lv2.setDuration(600);
            lv2.setRadiusGrowth((7.0f - lv2.getRadius()) / (float)lv2.getDuration());
            lv2.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
                for (LivingEntity lv3 : list) {
                    double d = this.squaredDistanceTo(lv3);
                    if (!(d < 16.0)) continue;
                    lv2.updatePosition(lv3.getX(), lv3.getY(), lv3.getZ());
                    break;
                }
            }
            this.world.syncWorldEvent(2006, this.getBlockPos(), this.isSilent() ? -1 : 1);
            this.world.spawnEntity(lv2);
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }
}

