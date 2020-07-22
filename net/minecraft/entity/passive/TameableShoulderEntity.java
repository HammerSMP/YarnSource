/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableShoulderEntity
extends TameableEntity {
    private int ticks;

    protected TameableShoulderEntity(EntityType<? extends TameableShoulderEntity> arg, World arg2) {
        super((EntityType<? extends TameableEntity>)arg, arg2);
    }

    public boolean mountOnto(ServerPlayerEntity player) {
        CompoundTag lv = new CompoundTag();
        lv.putString("id", this.getSavedEntityId());
        this.toTag(lv);
        if (player.addShoulderEntity(lv)) {
            this.remove();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        ++this.ticks;
        super.tick();
    }

    public boolean isReadyToSitOnPlayer() {
        return this.ticks > 100;
    }
}

