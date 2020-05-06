/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Potion {
    private final String baseName;
    private final ImmutableList<StatusEffectInstance> effects;

    public static Potion byId(String string) {
        return Registry.POTION.get(Identifier.tryParse(string));
    }

    public Potion(StatusEffectInstance ... args) {
        this((String)null, args);
    }

    public Potion(@Nullable String string, StatusEffectInstance ... args) {
        this.baseName = string;
        this.effects = ImmutableList.copyOf((Object[])args);
    }

    public String finishTranslationKey(String string) {
        return string + (this.baseName == null ? Registry.POTION.getId(this).getPath() : this.baseName);
    }

    public List<StatusEffectInstance> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffect() {
        if (!this.effects.isEmpty()) {
            for (StatusEffectInstance lv : this.effects) {
                if (!lv.getEffectType().isInstant()) continue;
                return true;
            }
        }
        return false;
    }
}

