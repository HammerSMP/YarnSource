/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

public class AnimalMateGoal
extends Goal {
    private static final TargetPredicate VALID_MATE_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeInvulnerable().includeTeammates().includeHidden();
    protected final AnimalEntity animal;
    private final Class<? extends AnimalEntity> entityClass;
    protected final World world;
    protected AnimalEntity mate;
    private int timer;
    private final double chance;

    public AnimalMateGoal(AnimalEntity arg, double d) {
        this(arg, d, arg.getClass());
    }

    public AnimalMateGoal(AnimalEntity arg, double d, Class<? extends AnimalEntity> arg2) {
        this.animal = arg;
        this.world = arg.world;
        this.entityClass = arg2;
        this.chance = d;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.mate = this.findMate();
        return this.mate != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
    }

    @Override
    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().lookAt(this.mate, 10.0f, this.animal.getLookPitchSpeed());
        this.animal.getNavigation().startMovingTo(this.mate, this.chance);
        ++this.timer;
        if (this.timer >= 60 && this.animal.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private AnimalEntity findMate() {
        List<? extends AnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.animal, this.animal.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        AnimalEntity lv = null;
        for (AnimalEntity animalEntity : list) {
            if (!this.animal.canBreedWith(animalEntity) || !(this.animal.squaredDistanceTo(animalEntity) < d)) continue;
            lv = animalEntity;
            d = this.animal.squaredDistanceTo(animalEntity);
        }
        return lv;
    }

    protected void breed() {
        this.animal.breed(this.world, this.mate);
    }
}

