/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

public interface Hoglin {
    @Environment(value=EnvType.CLIENT)
    public int getMovementCooldownTicks();

    public static boolean tryAttack(LivingEntity attacker, LivingEntity target) {
        float h;
        float f = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (!attacker.isBaby() && (int)f > 0) {
            float g = f / 2.0f + (float)attacker.world.random.nextInt((int)f);
        } else {
            h = f;
        }
        boolean bl = target.damage(DamageSource.mob(attacker), h);
        if (bl) {
            attacker.dealDamage(attacker, target);
            if (!attacker.isBaby()) {
                Hoglin.knockback(attacker, target);
            }
        }
        return bl;
    }

    public static void knockback(LivingEntity attacker, LivingEntity target) {
        double e;
        double d = attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        double f = d - (e = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        if (f <= 0.0) {
            return;
        }
        double g = target.getX() - attacker.getX();
        double h = target.getZ() - attacker.getZ();
        float i = attacker.world.random.nextInt(21) - 10;
        double j = f * (double)(attacker.world.random.nextFloat() * 0.5f + 0.2f);
        Vec3d lv = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
        double k = f * (double)attacker.world.random.nextFloat() * 0.5;
        target.addVelocity(lv.x, k, lv.z);
        target.velocityModified = true;
    }
}

