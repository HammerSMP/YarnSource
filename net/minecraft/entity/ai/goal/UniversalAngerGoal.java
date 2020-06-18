/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;

public class UniversalAngerGoal<T extends MobEntity>
extends Goal {
    private final T mob;
    private final boolean triggerOthers;
    private int field_25606;

    public UniversalAngerGoal(T arg, boolean bl) {
        this.mob = arg;
        this.triggerOthers = bl;
    }

    @Override
    public boolean canStart() {
        return ((MobEntity)this.mob).world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.method_29932();
    }

    private boolean method_29932() {
        return ((LivingEntity)this.mob).getAttacker() != null && ((LivingEntity)this.mob).getAttacker().getType() == EntityType.PLAYER && ((LivingEntity)this.mob).getLastAttackedTime() > this.field_25606;
    }

    @Override
    public void start() {
        this.field_25606 = ((LivingEntity)this.mob).getLastAttackedTime();
        ((Angerable)this.mob).universallyAnger();
        if (this.triggerOthers) {
            this.getOthersInRange().stream().filter(arg -> arg != this.mob).map(arg -> (Angerable)((Object)arg)).forEach(Angerable::universallyAnger);
        }
        super.start();
    }

    private List<MobEntity> getOthersInRange() {
        double d = ((LivingEntity)this.mob).getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box lv = Box.method_29968(((Entity)this.mob).getPos()).expand(d, 10.0, d);
        return ((MobEntity)this.mob).world.getEntitiesIncludingUngeneratedChunks(this.mob.getClass(), lv);
    }
}

