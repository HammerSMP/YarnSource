/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FireballEntity
extends AbstractFireballEntity {
    public int explosionPower = 1;

    public FireballEntity(EntityType<? extends FireballEntity> arg, World arg2) {
        super((EntityType<? extends AbstractFireballEntity>)arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public FireballEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.FIREBALL, x, y, z, velocityX, velocityY, velocityZ, world);
    }

    public FireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            boolean bl;
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), this.explosionPower, bl, (bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
            this.remove();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.world.isClient) {
            return;
        }
        Entity lv = entityHitResult.getEntity();
        Entity lv2 = this.getOwner();
        lv.damage(DamageSource.fireball(this, lv2), 6.0f);
        if (lv2 instanceof LivingEntity) {
            this.dealDamage((LivingEntity)lv2, lv);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("ExplosionPower", this.explosionPower);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("ExplosionPower", 99)) {
            this.explosionPower = tag.getInt("ExplosionPower");
        }
    }
}

