/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class WaterCreatureEntity
extends MobEntityWithAi {
    protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> arg, World arg2) {
        super((EntityType<? extends MobEntityWithAi>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return arg.intersectsEntities(this);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        return 1 + this.world.random.nextInt(3);
    }

    protected void tickWaterBreathingAir(int i) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setAir(i - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.damage(DamageSource.DROWN, 2.0f);
            }
        } else {
            this.setAir(300);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        this.tickWaterBreathingAir(i);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return false;
    }
}

