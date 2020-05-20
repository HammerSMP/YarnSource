/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class LookTargetUtil {
    public static void lookAtAndWalkTowardsEachOther(LivingEntity arg, LivingEntity arg2, float f) {
        LookTargetUtil.lookAtEachOther(arg, arg2);
        LookTargetUtil.walkTowardsEachOther(arg, arg2, f);
    }

    public static boolean canSee(Brain<?> arg, LivingEntity arg2) {
        return arg.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).filter(list -> list.contains(arg2)).isPresent();
    }

    public static boolean canSee(Brain<?> arg, MemoryModuleType<? extends LivingEntity> arg22, EntityType<?> arg3) {
        return LookTargetUtil.method_24564(arg, arg22, arg2 -> arg2.getType() == arg3);
    }

    private static boolean method_24564(Brain<?> arg, MemoryModuleType<? extends LivingEntity> arg22, Predicate<LivingEntity> predicate) {
        return arg.getOptionalMemory(arg22).filter(predicate).filter(LivingEntity::isAlive).filter(arg2 -> LookTargetUtil.canSee(arg, arg2)).isPresent();
    }

    private static void lookAtEachOther(LivingEntity arg, LivingEntity arg2) {
        LookTargetUtil.lookAt(arg, arg2);
        LookTargetUtil.lookAt(arg2, arg);
    }

    public static void lookAt(LivingEntity arg, LivingEntity arg2) {
        arg.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg2, true));
    }

    private static void walkTowardsEachOther(LivingEntity arg, LivingEntity arg2, float f) {
        int i = 2;
        LookTargetUtil.walkTowards(arg, arg2, f, 2);
        LookTargetUtil.walkTowards(arg2, arg, f, 2);
    }

    public static void walkTowards(LivingEntity arg, Entity arg2, float f, int i) {
        WalkTarget lv = new WalkTarget(new EntityLookTarget(arg2, false), f, i);
        arg.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg2, true));
        arg.getBrain().remember(MemoryModuleType.WALK_TARGET, lv);
    }

    public static void walkTowards(LivingEntity arg, BlockPos arg2, float f, int i) {
        WalkTarget lv = new WalkTarget(new BlockPosLookTarget(arg2), f, i);
        arg.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(arg2));
        arg.getBrain().remember(MemoryModuleType.WALK_TARGET, lv);
    }

    public static void give(LivingEntity arg, ItemStack arg2, Vec3d arg3) {
        double d = arg.getEyeY() - (double)0.3f;
        ItemEntity lv = new ItemEntity(arg.world, arg.getX(), d, arg.getZ(), arg2);
        float f = 0.3f;
        Vec3d lv2 = arg3.subtract(arg.getPos());
        lv2 = lv2.normalize().multiply(0.3f);
        lv.setVelocity(lv2);
        lv.setToDefaultPickupDelay();
        arg.world.spawnEntity(lv);
    }

    public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(ServerWorld arg, ChunkSectionPos arg22, int i) {
        int j = arg.getOccupiedPointOfInterestDistance(arg22);
        return ChunkSectionPos.stream(arg22, i).filter(arg2 -> arg.getOccupiedPointOfInterestDistance((ChunkSectionPos)arg2) < j).min(Comparator.comparingInt(arg::getOccupiedPointOfInterestDistance)).orElse(arg22);
    }

    public static boolean method_25940(MobEntity arg, LivingEntity arg2, int i) {
        Item lv = arg.getMainHandStack().getItem();
        if (lv instanceof RangedWeaponItem && arg.canUseRangedWeapon((RangedWeaponItem)lv)) {
            int j = ((RangedWeaponItem)lv).getRange() - i;
            return arg.isInRange(arg2, j);
        }
        return LookTargetUtil.method_25941(arg, arg2);
    }

    public static boolean method_25941(LivingEntity arg, LivingEntity arg2) {
        double e;
        double d = arg.squaredDistanceTo(arg2.getX(), arg2.getY(), arg2.getZ());
        return d <= (e = (double)(arg.getWidth() * 2.0f * (arg.getWidth() * 2.0f) + arg2.getWidth()));
    }

    public static boolean isNewTargetTooFar(LivingEntity arg, LivingEntity arg2, double d) {
        Optional<LivingEntity> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        if (!optional.isPresent()) {
            return false;
        }
        double e = arg.squaredDistanceTo(optional.get().getPos());
        double f = arg.squaredDistanceTo(arg2.getPos());
        return f > e + d * d;
    }

    public static boolean isVisibleInMemory(LivingEntity arg, LivingEntity arg2) {
        Brain<List<LivingEntity>> lv = arg.getBrain();
        if (!lv.hasMemoryModule(MemoryModuleType.VISIBLE_MOBS)) {
            return false;
        }
        return lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(arg2);
    }

    public static LivingEntity getCloserEntity(LivingEntity arg, Optional<LivingEntity> optional, LivingEntity arg2) {
        if (!optional.isPresent()) {
            return arg2;
        }
        return LookTargetUtil.getCloserEntity(arg, optional.get(), arg2);
    }

    public static LivingEntity getCloserEntity(LivingEntity arg, LivingEntity arg2, LivingEntity arg3) {
        Vec3d lv = arg2.getPos();
        Vec3d lv2 = arg3.getPos();
        return arg.squaredDistanceTo(lv) < arg.squaredDistanceTo(lv2) ? arg2 : arg3;
    }

    public static Optional<LivingEntity> getEntity(LivingEntity arg, MemoryModuleType<DynamicSerializableUuid> arg2) {
        Optional<DynamicSerializableUuid> optional = arg.getBrain().getOptionalMemory(arg2);
        return optional.map(DynamicSerializableUuid::getUuid).map(uUID -> (LivingEntity)((ServerWorld)arg.world).getEntity((UUID)uUID));
    }
}

