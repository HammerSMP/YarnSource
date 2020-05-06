/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;

public interface InteractionObserver {
    public void onInteractionWith(EntityInteraction var1, Entity var2);
}

