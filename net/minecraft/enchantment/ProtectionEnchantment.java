/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment
extends Enchantment {
    public final Type protectionType;

    public ProtectionEnchantment(Enchantment.Rarity weight, Type arg2, EquipmentSlot ... args) {
        super(weight, arg2 == Type.FALL ? EnchantmentTarget.ARMOR_FEET : EnchantmentTarget.ARMOR, args);
        this.protectionType = arg2;
    }

    @Override
    public int getMinPower(int level) {
        return this.protectionType.getBasePower() + (level - 1) * this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isOutOfWorld()) {
            return 0;
        }
        if (this.protectionType == Type.ALL) {
            return level;
        }
        if (this.protectionType == Type.FIRE && source.isFire()) {
            return level * 2;
        }
        if (this.protectionType == Type.FALL && source == DamageSource.FALL) {
            return level * 3;
        }
        if (this.protectionType == Type.EXPLOSION && source.isExplosive()) {
            return level * 2;
        }
        if (this.protectionType == Type.PROJECTILE && source.isProjectile()) {
            return level * 2;
        }
        return 0;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        if (other instanceof ProtectionEnchantment) {
            ProtectionEnchantment lv = (ProtectionEnchantment)other;
            if (this.protectionType == lv.protectionType) {
                return false;
            }
            return this.protectionType == Type.FALL || lv.protectionType == Type.FALL;
        }
        return super.canAccept(other);
    }

    public static int transformFireDuration(LivingEntity entity, int duration) {
        int j = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, entity);
        if (j > 0) {
            duration -= MathHelper.floor((float)duration * ((float)j * 0.15f));
        }
        return duration;
    }

    public static double transformExplosionKnockback(LivingEntity entity, double velocity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, entity);
        if (i > 0) {
            velocity -= (double)MathHelper.floor(velocity * (double)((float)i * 0.15f));
        }
        return velocity;
    }

    public static enum Type {
        ALL("all", 1, 11),
        FIRE("fire", 10, 8),
        FALL("fall", 5, 6),
        EXPLOSION("explosion", 5, 8),
        PROJECTILE("projectile", 3, 6);

        private final String name;
        private final int basePower;
        private final int powerPerLevel;

        private Type(String name, int basePower, int powerPerLevel) {
            this.name = name;
            this.basePower = basePower;
            this.powerPerLevel = powerPerLevel;
        }

        public int getBasePower() {
            return this.basePower;
        }

        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }
    }
}

