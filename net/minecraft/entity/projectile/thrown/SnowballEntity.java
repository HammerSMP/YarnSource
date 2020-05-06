/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SnowballEntity
extends ThrownItemEntity {
    public SnowballEntity(EntityType<? extends SnowballEntity> arg, World arg2) {
        super((EntityType<? extends ThrownItemEntity>)arg, arg2);
    }

    public SnowballEntity(World arg, LivingEntity arg2) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.SNOWBALL, arg2, arg);
    }

    public SnowballEntity(World arg, double d, double e, double f) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.SNOWBALL, d, e, f, arg);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Environment(value=EnvType.CLIENT)
    private ParticleEffect getParticleParameters() {
        ItemStack lv = this.getItem();
        return lv.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, lv);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 3) {
            ParticleEffect lv = this.getParticleParameters();
            for (int i = 0; i < 8; ++i) {
                this.world.addParticle(lv, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        Entity lv = arg.getEntity();
        int i = lv instanceof BlazeEntity ? 3 : 0;
        lv.damage(DamageSource.thrownProjectile(this, this.getOwner()), i);
    }

    @Override
    protected void onCollision(HitResult arg) {
        super.onCollision(arg);
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }
}

