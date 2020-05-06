/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;

public class EnderDragonPart
extends Entity {
    public final EnderDragonEntity owner;
    public final String name;
    private final EntityDimensions partDimensions;

    public EnderDragonPart(EnderDragonEntity arg, String string, float f, float g) {
        super(arg.getType(), arg.world);
        this.partDimensions = EntityDimensions.changing(f, g);
        this.calculateDimensions();
        this.owner = arg;
        this.name = string;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        return this.owner.damagePart(this, arg, f);
    }

    @Override
    public boolean isPartOf(Entity arg) {
        return this == arg || this.owner == arg;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        return this.partDimensions;
    }
}

