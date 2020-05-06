/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class SpectralArrowEntity
extends PersistentProjectileEntity {
    private int duration = 200;

    public SpectralArrowEntity(EntityType<? extends SpectralArrowEntity> arg, World arg2) {
        super((EntityType<? extends PersistentProjectileEntity>)arg, arg2);
    }

    public SpectralArrowEntity(World arg, LivingEntity arg2) {
        super(EntityType.SPECTRAL_ARROW, arg2, arg);
    }

    public SpectralArrowEntity(World arg, double d, double e, double f) {
        super(EntityType.SPECTRAL_ARROW, d, e, f, arg);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient && !this.inGround) {
            this.world.addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }

    @Override
    protected void onHit(LivingEntity arg) {
        super.onHit(arg);
        StatusEffectInstance lv = new StatusEffectInstance(StatusEffects.GLOWING, this.duration, 0);
        arg.addStatusEffect(lv);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("Duration")) {
            this.duration = arg.getInt("Duration");
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Duration", this.duration);
    }
}

