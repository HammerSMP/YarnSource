/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public abstract class IllagerEntity
extends RaiderEntity {
    protected IllagerEntity(EntityType<? extends IllagerEntity> arg, World arg2) {
        super((EntityType<? extends RaiderEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }

    @Environment(value=EnvType.CLIENT)
    public State getState() {
        return State.CROSSED;
    }

    public class LongDoorInteractGoal
    extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
        public LongDoorInteractGoal(RaiderEntity raider) {
            super(raider, false);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && IllagerEntity.this.hasActiveRaid();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;

    }
}

