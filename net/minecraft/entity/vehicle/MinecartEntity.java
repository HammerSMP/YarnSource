/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity
extends AbstractMinecartEntity {
    public MinecartEntity(EntityType<?> arg, World arg2) {
        super(arg, arg2);
    }

    public MinecartEntity(World arg, double d, double e, double f) {
        super(EntityType.MINECART, arg, d, e, f);
    }

    @Override
    public boolean interact(PlayerEntity arg, Hand arg2) {
        if (arg.shouldCancelInteraction()) {
            return false;
        }
        if (this.hasPassengers()) {
            return false;
        }
        if (!this.world.isClient) {
            arg.startRiding(this);
        }
        return true;
    }

    @Override
    public void onActivatorRail(int i, int j, int k, boolean bl) {
        if (bl) {
            if (this.hasPassengers()) {
                this.removeAllPassengers();
            }
            if (this.getDamageWobbleTicks() == 0) {
                this.setDamageWobbleSide(-this.getDamageWobbleSide());
                this.setDamageWobbleTicks(10);
                this.setDamageWobbleStrength(50.0f);
                this.scheduleVelocityUpdate();
            }
        }
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.RIDEABLE;
    }
}

