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

    public static boolean tryAttack(LivingEntity arg, LivingEntity arg2) {
        float h;
        float f = (float)arg.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (!arg.isBaby() && (int)f > 0) {
            float g = f / 2.0f + (float)arg.world.random.nextInt((int)f);
        } else {
            h = f;
        }
        boolean bl = arg2.damage(DamageSource.mob(arg), h);
        if (bl) {
            arg.dealDamage(arg, arg2);
            if (!arg.isBaby()) {
                Hoglin.knockback(arg, arg2);
            }
        }
        return bl;
    }

    public static void knockback(LivingEntity arg, LivingEntity arg2) {
        double e;
        double d = arg.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        double f = d - (e = arg2.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        if (f <= 0.0) {
            return;
        }
        double g = arg2.getX() - arg.getX();
        double h = arg2.getZ() - arg.getZ();
        float i = arg.world.random.nextInt(21) - 10;
        double j = f * (double)(arg.world.random.nextFloat() * 0.5f + 0.2f);
        Vec3d lv = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
        double k = f * (double)arg.world.random.nextFloat() * 0.5;
        arg2.addVelocity(lv.x, k, lv.z);
        arg2.velocityModified = true;
    }
}

