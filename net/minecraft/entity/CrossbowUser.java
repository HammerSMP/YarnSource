/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public interface CrossbowUser
extends RangedAttackMob {
    public void setCharging(boolean var1);

    public void shoot(LivingEntity var1, ItemStack var2, ProjectileEntity var3, float var4);

    @Nullable
    public LivingEntity getTarget();

    public void postShoot();

    default public void shoot(LivingEntity entity, float speed) {
        Hand lv = ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW);
        ItemStack lv2 = entity.getStackInHand(lv);
        if (entity.isHolding(Items.CROSSBOW)) {
            CrossbowItem.shootAll(entity.world, entity, lv, lv2, speed, 14 - entity.world.getDifficulty().getId() * 4);
        }
        this.postShoot();
    }

    default public void shoot(LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
        ProjectileEntity lv = projectile;
        double d = target.getX() - entity.getX();
        double e = target.getZ() - entity.getZ();
        double h = MathHelper.sqrt(d * d + e * e);
        double i = target.getBodyY(0.3333333333333333) - lv.getY() + h * (double)0.2f;
        Vector3f lv2 = this.getProjectileLaunchVelocity(entity, new Vec3d(d, i, e), multishotSpray);
        projectile.setVelocity(lv2.getX(), lv2.getY(), lv2.getZ(), speed, 14 - entity.world.getDifficulty().getId() * 4);
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (entity.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    default public Vector3f getProjectileLaunchVelocity(LivingEntity entity, Vec3d positionDelta, float multishotSpray) {
        Vec3d lv = positionDelta.normalize();
        Vec3d lv2 = lv.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (lv2.lengthSquared() <= 1.0E-7) {
            lv2 = lv.crossProduct(entity.getOppositeRotationVector(1.0f));
        }
        Quaternion lv3 = new Quaternion(new Vector3f(lv2), 90.0f, true);
        Vector3f lv4 = new Vector3f(lv);
        lv4.rotate(lv3);
        Quaternion lv5 = new Quaternion(lv4, multishotSpray, true);
        Vector3f lv6 = new Vector3f(lv);
        lv6.rotate(lv5);
        return lv6;
    }
}

