/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class StatusEffect {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();
    private final StatusEffectType type;
    private final int color;
    @Nullable
    private String translationKey;

    @Nullable
    public static StatusEffect byRawId(int i) {
        return (StatusEffect)Registry.STATUS_EFFECT.get(i);
    }

    public static int getRawId(StatusEffect arg) {
        return Registry.STATUS_EFFECT.getRawId(arg);
    }

    protected StatusEffect(StatusEffectType arg, int i) {
        this.type = arg;
        this.color = i;
    }

    public void applyUpdateEffect(LivingEntity arg, int i) {
        if (this == StatusEffects.REGENERATION) {
            if (arg.getHealth() < arg.getMaximumHealth()) {
                arg.heal(1.0f);
            }
        } else if (this == StatusEffects.POISON) {
            if (arg.getHealth() > 1.0f) {
                arg.damage(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == StatusEffects.WITHER) {
            arg.damage(DamageSource.WITHER, 1.0f);
        } else if (this == StatusEffects.HUNGER && arg instanceof PlayerEntity) {
            ((PlayerEntity)arg).addExhaustion(0.005f * (float)(i + 1));
        } else if (this == StatusEffects.SATURATION && arg instanceof PlayerEntity) {
            if (!arg.world.isClient) {
                ((PlayerEntity)arg).getHungerManager().add(i + 1, 1.0f);
            }
        } else if (this == StatusEffects.INSTANT_HEALTH && !arg.isUndead() || this == StatusEffects.INSTANT_DAMAGE && arg.isUndead()) {
            arg.heal(Math.max(4 << i, 0));
        } else if (this == StatusEffects.INSTANT_DAMAGE && !arg.isUndead() || this == StatusEffects.INSTANT_HEALTH && arg.isUndead()) {
            arg.damage(DamageSource.MAGIC, 6 << i);
        }
    }

    public void applyInstantEffect(@Nullable Entity arg, @Nullable Entity arg2, LivingEntity arg3, int i, double d) {
        if (this == StatusEffects.INSTANT_HEALTH && !arg3.isUndead() || this == StatusEffects.INSTANT_DAMAGE && arg3.isUndead()) {
            int j = (int)(d * (double)(4 << i) + 0.5);
            arg3.heal(j);
        } else if (this == StatusEffects.INSTANT_DAMAGE && !arg3.isUndead() || this == StatusEffects.INSTANT_HEALTH && arg3.isUndead()) {
            int k = (int)(d * (double)(6 << i) + 0.5);
            if (arg == null) {
                arg3.damage(DamageSource.MAGIC, k);
            } else {
                arg3.damage(DamageSource.magic(arg, arg2), k);
            }
        } else {
            this.applyUpdateEffect(arg3, i);
        }
    }

    public boolean canApplyUpdateEffect(int i, int j) {
        if (this == StatusEffects.REGENERATION) {
            int k = 50 >> j;
            if (k > 0) {
                return i % k == 0;
            }
            return true;
        }
        if (this == StatusEffects.POISON) {
            int l = 25 >> j;
            if (l > 0) {
                return i % l == 0;
            }
            return true;
        }
        if (this == StatusEffects.WITHER) {
            int m = 40 >> j;
            if (m > 0) {
                return i % m == 0;
            }
            return true;
        }
        return this == StatusEffects.HUNGER;
    }

    public boolean isInstant() {
        return false;
    }

    protected String loadTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("effect", Registry.STATUS_EFFECT.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.loadTranslationKey();
    }

    public Text getName() {
        return new TranslatableText(this.getTranslationKey());
    }

    @Environment(value=EnvType.CLIENT)
    public StatusEffectType getType() {
        return this.type;
    }

    public int getColor() {
        return this.color;
    }

    public StatusEffect addAttributeModifier(EntityAttribute arg, String string, double d, EntityAttributeModifier.Operation arg2) {
        EntityAttributeModifier lv = new EntityAttributeModifier(UUID.fromString(string), this::getTranslationKey, d, arg2);
        this.attributeModifiers.put(arg, lv);
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void onRemoved(LivingEntity arg, AttributeContainer arg2, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance lv = arg2.getCustomInstance(entry.getKey());
            if (lv == null) continue;
            lv.removeModifier(entry.getValue());
        }
    }

    public void onApplied(LivingEntity arg, AttributeContainer arg2, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance lv = arg2.getCustomInstance(entry.getKey());
            if (lv == null) continue;
            EntityAttributeModifier lv2 = entry.getValue();
            lv.removeModifier(lv2);
            lv.addPersistentModifier(new EntityAttributeModifier(lv2.getId(), this.getTranslationKey() + " " + i, this.adjustModifierAmount(i, lv2), lv2.getOperation()));
        }
    }

    public double adjustModifierAmount(int i, EntityAttributeModifier arg) {
        return arg.getValue() * (double)(i + 1);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBeneficial() {
        return this.type == StatusEffectType.BENEFICIAL;
    }
}

