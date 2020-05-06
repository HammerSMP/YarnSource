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

    public ProtectionEnchantment(Enchantment.Rarity arg, Type arg2, EquipmentSlot ... args) {
        super(arg, arg2 == Type.FALL ? EnchantmentTarget.ARMOR_FEET : EnchantmentTarget.ARMOR, args);
        this.protectionType = arg2;
    }

    @Override
    public int getMinimumPower(int i) {
        return this.protectionType.getBasePower() + (i - 1) * this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + this.protectionType.getPowerPerLevel();
    }

    @Override
    public int getMaximumLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int i, DamageSource arg) {
        if (arg.isOutOfWorld()) {
            return 0;
        }
        if (this.protectionType == Type.ALL) {
            return i;
        }
        if (this.protectionType == Type.FIRE && arg.isFire()) {
            return i * 2;
        }
        if (this.protectionType == Type.FALL && arg == DamageSource.FALL) {
            return i * 3;
        }
        if (this.protectionType == Type.EXPLOSION && arg.isExplosive()) {
            return i * 2;
        }
        if (this.protectionType == Type.PROJECTILE && arg.isProjectile()) {
            return i * 2;
        }
        return 0;
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        if (arg instanceof ProtectionEnchantment) {
            ProtectionEnchantment lv = (ProtectionEnchantment)arg;
            if (this.protectionType == lv.protectionType) {
                return false;
            }
            return this.protectionType == Type.FALL || lv.protectionType == Type.FALL;
        }
        return super.canAccept(arg);
    }

    public static int transformFireDuration(LivingEntity arg, int i) {
        int j = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, arg);
        if (j > 0) {
            i -= MathHelper.floor((float)i * ((float)j * 0.15f));
        }
        return i;
    }

    public static double transformExplosionKnockback(LivingEntity arg, double d) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, arg);
        if (i > 0) {
            d -= (double)MathHelper.floor(d * (double)((float)i * 0.15f));
        }
        return d;
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

        private Type(String string2, int j, int k) {
            this.name = string2;
            this.basePower = j;
            this.powerPerLevel = k;
        }

        public int getBasePower() {
            return this.basePower;
        }

        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }
    }
}

