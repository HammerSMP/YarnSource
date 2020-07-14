/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface EntityView {
    public List<Entity> getEntities(@Nullable Entity var1, Box var2, @Nullable Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(Class<? extends T> var1, Box var2, @Nullable Predicate<? super T> var3);

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        return this.getEntities(entityClass, box, predicate);
    }

    public List<? extends PlayerEntity> getPlayers();

    default public List<Entity> getEntities(@Nullable Entity except, Box box) {
        return this.getEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public boolean intersectsEntities(@Nullable Entity entity, VoxelShape shape) {
        if (shape.isEmpty()) {
            return true;
        }
        for (Entity lv : this.getEntities(entity, shape.getBoundingBox())) {
            if (lv.removed || !lv.inanimate || entity != null && lv.isConnectedThroughVehicle(entity) || !VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(lv.getBoundingBox()), BooleanBiFunction.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getNonSpectatingEntities(Class<? extends T> entityClass, Box box) {
        return this.getEntities(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box) {
        return this.getEntitiesIncludingUngeneratedChunks(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        if (box.getAverageSideLength() < 1.0E-7) {
            return Stream.empty();
        }
        Box lv = box.expand(1.0E-7);
        return this.getEntities(entity, lv, predicate.and(e -> entity == null || !entity.isConnectedThroughVehicle((Entity)e))).stream().flatMap(arg3 -> {
            Box lv;
            if (entity != null && (lv = entity.getHardCollisionBox((Entity)arg3)) != null && lv.intersects(lv)) {
                return Stream.of(arg3.getCollisionBox(), lv);
            }
            return Stream.of(arg3.getCollisionBox());
        }).filter(Objects::nonNull).map(VoxelShapes::cuboid);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, @Nullable Predicate<Entity> targetPredicate) {
        double h = -1.0;
        PlayerEntity lv = null;
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (targetPredicate != null && !targetPredicate.test(playerEntity)) continue;
            double i = playerEntity.squaredDistanceTo(x, y, z);
            if (!(maxDistance < 0.0) && !(i < maxDistance * maxDistance) || h != -1.0 && !(i < h)) continue;
            h = i;
            lv = playerEntity;
        }
        return lv;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
        return this.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), maxDistance, false);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
        Predicate<Entity> predicate = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(x, y, z, maxDistance, predicate);
    }

    default public boolean isPlayerInRange(double x, double y, double z, double range) {
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_LIVING_ENTITY.test(playerEntity)) continue;
            double h = playerEntity.squaredDistanceTo(x, y, z);
            if (!(range < 0.0) && !(h < range * range)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.getX(), entity.getY(), entity.getZ());
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, null, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
        return this.getClosestEntity(this.getEntities(entityClass, box, null), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
        return this.getClosestEntity(this.getEntitiesIncludingUngeneratedChunks(entityClass, box, null), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> entityList, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z) {
        double g = -1.0;
        LivingEntity lv = null;
        for (LivingEntity lv2 : entityList) {
            if (!targetPredicate.test(entity, lv2)) continue;
            double h = lv2.squaredDistanceTo(x, y, z);
            if (g != -1.0 && !(h < g)) continue;
            g = h;
            lv = lv2;
        }
        return (T)lv;
    }

    default public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
        ArrayList list = Lists.newArrayList();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) || !targetPredicate.test(entity, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<? extends T> entityClass, TargetPredicate targetPredicate, LivingEntity targetingEntity, Box box) {
        List<T> list = this.getEntities(entityClass, box, null);
        ArrayList list2 = Lists.newArrayList();
        for (LivingEntity lv : list) {
            if (!targetPredicate.test(targetingEntity, lv)) continue;
            list2.add(lv);
        }
        return list2;
    }

    @Nullable
    default public PlayerEntity getPlayerByUuid(UUID uuid) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity lv = this.getPlayers().get(i);
            if (!uuid.equals(lv.getUuid())) continue;
            return lv;
        }
        return null;
    }
}

