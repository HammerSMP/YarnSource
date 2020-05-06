/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public final class ProjectileUtil {
    public static HitResult getCollision(Entity arg, Predicate<Entity> predicate, RayTraceContext.ShapeType arg2) {
        EntityHitResult lv6;
        Vec3d lv4;
        Vec3d lv = arg.getVelocity();
        World lv2 = arg.world;
        Vec3d lv3 = arg.getPos();
        HitResult lv5 = lv2.rayTrace(new RayTraceContext(lv3, lv4 = lv3.add(lv), arg2, RayTraceContext.FluidHandling.NONE, arg));
        if (((HitResult)lv5).getType() != HitResult.Type.MISS) {
            lv4 = lv5.getPos();
        }
        if ((lv6 = ProjectileUtil.getEntityCollision(lv2, arg, lv3, lv4, arg.getBoundingBox().stretch(arg.getVelocity()).expand(1.0), predicate)) != null) {
            lv5 = lv6;
        }
        return lv5;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static EntityHitResult rayTrace(Entity arg, Vec3d arg2, Vec3d arg3, Box arg4, Predicate<Entity> predicate, double d) {
        World lv = arg.world;
        double e = d;
        Entity lv2 = null;
        Vec3d lv3 = null;
        for (Entity lv4 : lv.getEntities(arg, arg4, predicate)) {
            Vec3d lv6;
            double f;
            Box lv5 = lv4.getBoundingBox().expand(lv4.getTargetingMargin());
            Optional<Vec3d> optional = lv5.rayTrace(arg2, arg3);
            if (lv5.contains(arg2)) {
                if (!(e >= 0.0)) continue;
                lv2 = lv4;
                lv3 = optional.orElse(arg2);
                e = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((f = arg2.squaredDistanceTo(lv6 = optional.get())) < e) && e != 0.0) continue;
            if (lv4.getRootVehicle() == arg.getRootVehicle()) {
                if (e != 0.0) continue;
                lv2 = lv4;
                lv3 = lv6;
                continue;
            }
            lv2 = lv4;
            lv3 = lv6;
            e = f;
        }
        if (lv2 == null) {
            return null;
        }
        return new EntityHitResult(lv2, lv3);
    }

    @Nullable
    public static EntityHitResult getEntityCollision(World arg, Entity arg2, Vec3d arg3, Vec3d arg4, Box arg5, Predicate<Entity> predicate) {
        double d = Double.MAX_VALUE;
        Entity lv = null;
        for (Entity lv2 : arg.getEntities(arg2, arg5, predicate)) {
            double e;
            Box lv3 = lv2.getBoundingBox().expand(0.3f);
            Optional<Vec3d> optional = lv3.rayTrace(arg3, arg4);
            if (!optional.isPresent() || !((e = arg3.squaredDistanceTo(optional.get())) < d)) continue;
            lv = lv2;
            d = e;
        }
        if (lv == null) {
            return null;
        }
        return new EntityHitResult(lv);
    }

    public static final void method_7484(Entity arg, float f) {
        Vec3d lv = arg.getVelocity();
        if (lv.lengthSquared() == 0.0) {
            return;
        }
        float g = MathHelper.sqrt(Entity.squaredHorizontalLength(lv));
        arg.yaw = (float)(MathHelper.atan2(lv.z, lv.x) * 57.2957763671875) + 90.0f;
        arg.pitch = (float)(MathHelper.atan2(g, lv.y) * 57.2957763671875) - 90.0f;
        while (arg.pitch - arg.prevPitch < -180.0f) {
            arg.prevPitch -= 360.0f;
        }
        while (arg.pitch - arg.prevPitch >= 180.0f) {
            arg.prevPitch += 360.0f;
        }
        while (arg.yaw - arg.prevYaw < -180.0f) {
            arg.prevYaw -= 360.0f;
        }
        while (arg.yaw - arg.prevYaw >= 180.0f) {
            arg.prevYaw += 360.0f;
        }
        arg.pitch = MathHelper.lerp(f, arg.prevPitch, arg.pitch);
        arg.yaw = MathHelper.lerp(f, arg.prevYaw, arg.yaw);
    }

    public static Hand getHandPossiblyHolding(LivingEntity arg, Item arg2) {
        return arg.getMainHandStack().getItem() == arg2 ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static PersistentProjectileEntity createArrowProjectile(LivingEntity arg, ItemStack arg2, float f) {
        ArrowItem lv = (ArrowItem)(arg2.getItem() instanceof ArrowItem ? arg2.getItem() : Items.ARROW);
        PersistentProjectileEntity lv2 = lv.createArrow(arg.world, arg2, arg);
        lv2.applyEnchantmentEffects(arg, f);
        if (arg2.getItem() == Items.TIPPED_ARROW && lv2 instanceof ArrowEntity) {
            ((ArrowEntity)lv2).initFromStack(arg2);
        }
        return lv2;
    }
}

