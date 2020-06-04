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

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> class_, Box arg, @Nullable Predicate<? super T> predicate) {
        return this.getEntities(class_, arg, predicate);
    }

    public List<? extends PlayerEntity> getPlayers();

    default public List<Entity> getEntities(@Nullable Entity arg, Box arg2) {
        return this.getEntities(arg, arg2, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public boolean intersectsEntities(@Nullable Entity arg, VoxelShape arg2) {
        if (arg2.isEmpty()) {
            return true;
        }
        for (Entity lv : this.getEntities(arg, arg2.getBoundingBox())) {
            if (lv.removed || !lv.inanimate || arg != null && lv.isConnectedThroughVehicle(arg) || !VoxelShapes.matchesAnywhere(arg2, VoxelShapes.cuboid(lv.getBoundingBox()), BooleanBiFunction.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getNonSpectatingEntities(Class<? extends T> class_, Box arg) {
        return this.getEntities(class_, arg, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> class_, Box arg) {
        return this.getEntitiesIncludingUngeneratedChunks(class_, arg, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity arg, Box arg22, Predicate<Entity> predicate) {
        if (arg22.getAverageSideLength() < 1.0E-7) {
            return Stream.empty();
        }
        Box lv = arg22.expand(1.0E-7);
        return this.getEntities(arg, lv).stream().filter(predicate).filter(arg2 -> arg == null || !arg.isConnectedThroughVehicle((Entity)arg2)).flatMap(arg2 -> Stream.of(arg2.getCollisionBox(), arg == null ? null : arg.getHardCollisionBox((Entity)arg2))).filter(Objects::nonNull).filter(lv::intersects).map(VoxelShapes::cuboid);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double d, double e, double f, double g, @Nullable Predicate<Entity> predicate) {
        double h = -1.0;
        PlayerEntity lv = null;
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (predicate != null && !predicate.test(playerEntity)) continue;
            double i = playerEntity.squaredDistanceTo(d, e, f);
            if (!(g < 0.0) && !(i < g * g) || h != -1.0 && !(i < h)) continue;
            h = i;
            lv = playerEntity;
        }
        return lv;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(Entity arg, double d) {
        return this.getClosestPlayer(arg.getX(), arg.getY(), arg.getZ(), d, false);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double d, double e, double f, double g, boolean bl) {
        Predicate<Entity> predicate = bl ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(d, e, f, g, predicate);
    }

    default public boolean isPlayerInRange(double d, double e, double f, double g) {
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_LIVING_ENTITY.test(playerEntity)) continue;
            double h = playerEntity.squaredDistanceTo(d, e, f);
            if (!(g < 0.0) && !(h < g * g)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate arg, LivingEntity arg2) {
        return this.getClosestEntity(this.getPlayers(), arg, arg2, arg2.getX(), arg2.getY(), arg2.getZ());
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate arg, LivingEntity arg2, double d, double e, double f) {
        return this.getClosestEntity(this.getPlayers(), arg, arg2, d, e, f);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate arg, double d, double e, double f) {
        return this.getClosestEntity(this.getPlayers(), arg, null, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(Class<? extends T> class_, TargetPredicate arg, @Nullable LivingEntity arg2, double d, double e, double f, Box arg3) {
        return this.getClosestEntity(this.getEntities(class_, arg3, null), arg, arg2, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(Class<? extends T> class_, TargetPredicate arg, @Nullable LivingEntity arg2, double d, double e, double f, Box arg3) {
        return this.getClosestEntity(this.getEntitiesIncludingUngeneratedChunks(class_, arg3, null), arg, arg2, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> list, TargetPredicate arg, @Nullable LivingEntity arg2, double d, double e, double f) {
        double g = -1.0;
        LivingEntity lv = null;
        for (LivingEntity lv2 : list) {
            if (!arg.test(arg2, lv2)) continue;
            double h = lv2.squaredDistanceTo(d, e, f);
            if (g != -1.0 && !(h < g)) continue;
            g = h;
            lv = lv2;
        }
        return (T)lv;
    }

    default public List<PlayerEntity> getPlayers(TargetPredicate arg, LivingEntity arg2, Box arg3) {
        ArrayList list = Lists.newArrayList();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!arg3.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) || !arg.test(arg2, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<? extends T> class_, TargetPredicate arg, LivingEntity arg2, Box arg3) {
        List<T> list = this.getEntities(class_, arg3, null);
        ArrayList list2 = Lists.newArrayList();
        for (LivingEntity lv : list) {
            if (!arg.test(arg2, lv)) continue;
            list2.add(lv);
        }
        return list2;
    }

    @Nullable
    default public PlayerEntity getPlayerByUuid(UUID uUID) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity lv = this.getPlayers().get(i);
            if (!uUID.equals(lv.getUuid())) continue;
            return lv;
        }
        return null;
    }
}

