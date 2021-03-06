/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class LookTargetUtil {
    public static void lookAtAndWalkTowardsEachOther(LivingEntity first, LivingEntity second, float speed) {
        LookTargetUtil.lookAtEachOther(first, second);
        LookTargetUtil.walkTowardsEachOther(first, second, speed);
    }

    public static boolean canSee(Brain<?> brain, LivingEntity target) {
        return brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).filter(list -> list.contains(target)).isPresent();
    }

    public static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
        return LookTargetUtil.canSee(brain, memoryModuleType, (LivingEntity arg2) -> arg2.getType() == entityType);
    }

    private static boolean canSee(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryType, Predicate<LivingEntity> filter) {
        return brain.getOptionalMemory(memoryType).filter(filter).filter(LivingEntity::isAlive).filter(arg2 -> LookTargetUtil.canSee(brain, arg2)).isPresent();
    }

    private static void lookAtEachOther(LivingEntity first, LivingEntity second) {
        LookTargetUtil.lookAt(first, second);
        LookTargetUtil.lookAt(second, first);
    }

    public static void lookAt(LivingEntity entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
    }

    private static void walkTowardsEachOther(LivingEntity first, LivingEntity second, float speed) {
        int i = 2;
        LookTargetUtil.walkTowards(first, second, speed, 2);
        LookTargetUtil.walkTowards(second, first, speed, 2);
    }

    public static void walkTowards(LivingEntity entity, Entity target, float speed, int completionRange) {
        WalkTarget lv = new WalkTarget(new EntityLookTarget(target, false), speed, completionRange);
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, lv);
    }

    public static void walkTowards(LivingEntity entity, BlockPos target, float speed, int completionRange) {
        WalkTarget lv = new WalkTarget(new BlockPosLookTarget(target), speed, completionRange);
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(target));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, lv);
    }

    public static void give(LivingEntity entity, ItemStack stack, Vec3d targetLocation) {
        double d = entity.getEyeY() - (double)0.3f;
        ItemEntity lv = new ItemEntity(entity.world, entity.getX(), d, entity.getZ(), stack);
        float f = 0.3f;
        Vec3d lv2 = targetLocation.subtract(entity.getPos());
        lv2 = lv2.normalize().multiply(0.3f);
        lv.setVelocity(lv2);
        lv.setToDefaultPickupDelay();
        entity.world.spawnEntity(lv);
    }

    public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(ServerWorld world, ChunkSectionPos center, int radius) {
        int j = world.getOccupiedPointOfInterestDistance(center);
        return ChunkSectionPos.stream(center, radius).filter(arg2 -> world.getOccupiedPointOfInterestDistance((ChunkSectionPos)arg2) < j).min(Comparator.comparingInt(world::getOccupiedPointOfInterestDistance)).orElse(center);
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

    public static boolean isNewTargetTooFar(LivingEntity source, LivingEntity target, double extraDistance) {
        Optional<LivingEntity> optional = source.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        if (!optional.isPresent()) {
            return false;
        }
        double e = source.squaredDistanceTo(optional.get().getPos());
        double f = source.squaredDistanceTo(target.getPos());
        return f > e + extraDistance * extraDistance;
    }

    public static boolean isVisibleInMemory(LivingEntity source, LivingEntity target) {
        Brain<List<LivingEntity>> lv = source.getBrain();
        if (!lv.hasMemoryModule(MemoryModuleType.VISIBLE_MOBS)) {
            return false;
        }
        return lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(target);
    }

    public static LivingEntity getCloserEntity(LivingEntity source, Optional<LivingEntity> first, LivingEntity second) {
        if (!first.isPresent()) {
            return second;
        }
        return LookTargetUtil.getCloserEntity(source, first.get(), second);
    }

    public static LivingEntity getCloserEntity(LivingEntity source, LivingEntity first, LivingEntity second) {
        Vec3d lv = first.getPos();
        Vec3d lv2 = second.getPos();
        return source.squaredDistanceTo(lv) < source.squaredDistanceTo(lv2) ? first : second;
    }

    public static Optional<LivingEntity> getEntity(LivingEntity entity, MemoryModuleType<UUID> uuidMemoryModule) {
        Optional<UUID> optional = entity.getBrain().getOptionalMemory(uuidMemoryModule);
        return optional.map(uUID -> (LivingEntity)((ServerWorld)arg.world).getEntity((UUID)uUID));
    }

    public static Stream<VillagerEntity> streamSeenVillagers(VillagerEntity villager, Predicate<VillagerEntity> filter) {
        return villager.getBrain().getOptionalMemory(MemoryModuleType.MOBS).map(list -> list.stream().filter(arg2 -> arg2 instanceof VillagerEntity && arg2 != villager).map(arg -> (VillagerEntity)arg).filter(LivingEntity::isAlive).filter(filter)).orElseGet(Stream::empty);
    }
}

