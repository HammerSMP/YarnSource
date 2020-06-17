/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;

public class FoodComponent {
    private final int hunger;
    private final float saturationModifier;
    private final boolean meat;
    private final boolean alwaysEdible;
    private final boolean snack;
    private final List<Pair<StatusEffectInstance, Float>> statusEffects;

    private FoodComponent(int i, float f, boolean bl, boolean bl2, boolean bl3, List<Pair<StatusEffectInstance, Float>> list) {
        this.hunger = i;
        this.saturationModifier = f;
        this.meat = bl;
        this.alwaysEdible = bl2;
        this.snack = bl3;
        this.statusEffects = list;
    }

    public int getHunger() {
        return this.hunger;
    }

    public float getSaturationModifier() {
        return this.saturationModifier;
    }

    public boolean isMeat() {
        return this.meat;
    }

    public boolean isAlwaysEdible() {
        return this.alwaysEdible;
    }

    public boolean isSnack() {
        return this.snack;
    }

    public List<Pair<StatusEffectInstance, Float>> getStatusEffects() {
        return this.statusEffects;
    }

    public static class Builder {
        private int hunger;
        private float saturationModifier;
        private boolean meat;
        private boolean alwaysEdible;
        private boolean snack;
        private final List<Pair<StatusEffectInstance, Float>> statusEffects = Lists.newArrayList();

        public Builder hunger(int i) {
            this.hunger = i;
            return this;
        }

        public Builder saturationModifier(float f) {
            this.saturationModifier = f;
            return this;
        }

        public Builder meat() {
            this.meat = true;
            return this;
        }

        public Builder alwaysEdible() {
            this.alwaysEdible = true;
            return this;
        }

        public Builder snack() {
            this.snack = true;
            return this;
        }

        public Builder statusEffect(StatusEffectInstance arg, float f) {
            this.statusEffects.add((Pair<StatusEffectInstance, Float>)Pair.of((Object)arg, (Object)Float.valueOf(f)));
            return this;
        }

        public FoodComponent build() {
            return new FoodComponent(this.hunger, this.saturationModifier, this.meat, this.alwaysEdible, this.snack, this.statusEffects);
        }
    }
}

