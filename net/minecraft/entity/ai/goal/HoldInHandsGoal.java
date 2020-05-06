/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;

public class HoldInHandsGoal<T extends MobEntity>
extends Goal {
    private final T actor;
    private final ItemStack item;
    private final Predicate<? super T> condition;
    private final SoundEvent sound;

    public HoldInHandsGoal(T arg, ItemStack arg2, @Nullable SoundEvent arg3, Predicate<? super T> predicate) {
        this.actor = arg;
        this.item = arg2;
        this.sound = arg3;
        this.condition = predicate;
    }

    @Override
    public boolean canStart() {
        return this.condition.test(this.actor);
    }

    @Override
    public boolean shouldContinue() {
        return ((LivingEntity)this.actor).isUsingItem();
    }

    @Override
    public void start() {
        ((MobEntity)this.actor).equipStack(EquipmentSlot.MAINHAND, this.item.copy());
        ((LivingEntity)this.actor).setCurrentHand(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
        ((MobEntity)this.actor).equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        if (this.sound != null) {
            ((Entity)this.actor).playSound(this.sound, 1.0f, ((LivingEntity)this.actor).getRandom().nextFloat() * 0.2f + 0.9f);
        }
    }
}

