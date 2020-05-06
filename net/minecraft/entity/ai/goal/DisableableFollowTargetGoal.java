/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;

public class DisableableFollowTargetGoal<T extends LivingEntity>
extends FollowTargetGoal<T> {
    private boolean enabled = true;

    public DisableableFollowTargetGoal(RaiderEntity arg, Class<T> arg2, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
        super(arg, arg2, i, bl, bl2, predicate);
    }

    public void setEnabled(boolean bl) {
        this.enabled = bl;
    }

    @Override
    public boolean canStart() {
        return this.enabled && super.canStart();
    }
}

