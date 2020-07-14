/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntitySelector {
    private final int limit;
    private final boolean includesNonPlayers;
    private final boolean localWorldOnly;
    private final Predicate<Entity> basePredicate;
    private final NumberRange.FloatRange distance;
    private final Function<Vec3d, Vec3d> positionOffset;
    @Nullable
    private final Box box;
    private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
    private final boolean senderOnly;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID uuid;
    @Nullable
    private final EntityType<?> type;
    private final boolean usesAt;

    public EntitySelector(int count, boolean includesNonPlayers, boolean localWorldOnly, Predicate<Entity> basePredicate, NumberRange.FloatRange distance, Function<Vec3d, Vec3d> positionOffset, @Nullable Box box, BiConsumer<Vec3d, List<? extends Entity>> sorter, boolean senderOnly, @Nullable String playerName, @Nullable UUID uuid, @Nullable EntityType<?> type, boolean usesAt) {
        this.limit = count;
        this.includesNonPlayers = includesNonPlayers;
        this.localWorldOnly = localWorldOnly;
        this.basePredicate = basePredicate;
        this.distance = distance;
        this.positionOffset = positionOffset;
        this.box = box;
        this.sorter = sorter;
        this.senderOnly = senderOnly;
        this.playerName = playerName;
        this.uuid = uuid;
        this.type = type;
        this.usesAt = usesAt;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean includesNonPlayers() {
        return this.includesNonPlayers;
    }

    public boolean isSenderOnly() {
        return this.senderOnly;
    }

    public boolean isLocalWorldOnly() {
        return this.localWorldOnly;
    }

    private void checkSourcePermission(ServerCommandSource arg) throws CommandSyntaxException {
        if (this.usesAt && !arg.hasPermissionLevel(2)) {
            throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
        }
    }

    public Entity getEntity(ServerCommandSource arg) throws CommandSyntaxException {
        this.checkSourcePermission(arg);
        List<? extends Entity> list = this.getEntities(arg);
        if (list.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        if (list.size() > 1) {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<? extends Entity> getEntities(ServerCommandSource arg) throws CommandSyntaxException {
        this.checkSourcePermission(arg);
        if (!this.includesNonPlayers) {
            return this.getPlayers(arg);
        }
        if (this.playerName != null) {
            ServerPlayerEntity lv = arg.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (lv == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{lv});
        }
        if (this.uuid != null) {
            for (ServerWorld lv2 : arg.getMinecraftServer().getWorlds()) {
                Entity lv3 = lv2.getEntity(this.uuid);
                if (lv3 == null) continue;
                return Lists.newArrayList((Object[])new Entity[]{lv3});
            }
            return Collections.emptyList();
        }
        Vec3d lv4 = this.positionOffset.apply(arg.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(lv4);
        if (this.senderOnly) {
            if (arg.getEntity() != null && predicate.test(arg.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[]{arg.getEntity()});
            }
            return Collections.emptyList();
        }
        ArrayList list = Lists.newArrayList();
        if (this.isLocalWorldOnly()) {
            this.appendEntitiesFromWorld(list, arg.getWorld(), lv4, predicate);
        } else {
            for (ServerWorld lv5 : arg.getMinecraftServer().getWorlds()) {
                this.appendEntitiesFromWorld(list, lv5, lv4, predicate);
            }
        }
        return this.getEntities(lv4, list);
    }

    private void appendEntitiesFromWorld(List<Entity> list, ServerWorld arg, Vec3d arg2, Predicate<Entity> predicate) {
        if (this.box != null) {
            list.addAll(arg.getEntities(this.type, this.box.offset(arg2), predicate));
        } else {
            list.addAll(arg.getEntities(this.type, predicate));
        }
    }

    public ServerPlayerEntity getPlayer(ServerCommandSource arg) throws CommandSyntaxException {
        this.checkSourcePermission(arg);
        List<ServerPlayerEntity> list = this.getPlayers(arg);
        if (list.size() != 1) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<ServerPlayerEntity> getPlayers(ServerCommandSource arg) throws CommandSyntaxException {
        ArrayList list2;
        this.checkSourcePermission(arg);
        if (this.playerName != null) {
            ServerPlayerEntity lv = arg.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (lv == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{lv});
        }
        if (this.uuid != null) {
            ServerPlayerEntity lv2 = arg.getMinecraftServer().getPlayerManager().getPlayer(this.uuid);
            if (lv2 == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{lv2});
        }
        Vec3d lv3 = this.positionOffset.apply(arg.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(lv3);
        if (this.senderOnly) {
            ServerPlayerEntity lv4;
            if (arg.getEntity() instanceof ServerPlayerEntity && predicate.test(lv4 = (ServerPlayerEntity)arg.getEntity())) {
                return Lists.newArrayList((Object[])new ServerPlayerEntity[]{lv4});
            }
            return Collections.emptyList();
        }
        if (this.isLocalWorldOnly()) {
            List<ServerPlayerEntity> list = arg.getWorld().getPlayers(predicate::test);
        } else {
            list2 = Lists.newArrayList();
            for (ServerPlayerEntity lv5 : arg.getMinecraftServer().getPlayerManager().getPlayerList()) {
                if (!predicate.test(lv5)) continue;
                list2.add(lv5);
            }
        }
        return this.getEntities(lv3, list2);
    }

    private Predicate<Entity> getPositionPredicate(Vec3d arg) {
        Predicate<Entity> predicate = this.basePredicate;
        if (this.box != null) {
            Box lv = this.box.offset(arg);
            predicate = predicate.and(arg2 -> lv.intersects(arg2.getBoundingBox()));
        }
        if (!this.distance.isDummy()) {
            predicate = predicate.and(arg2 -> this.distance.testSqrt(arg2.squaredDistanceTo(arg)));
        }
        return predicate;
    }

    private <T extends Entity> List<T> getEntities(Vec3d arg, List<T> list) {
        if (list.size() > 1) {
            this.sorter.accept(arg, list);
        }
        return list.subList(0, Math.min(this.limit, list.size()));
    }

    public static MutableText getNames(List<? extends Entity> list) {
        return Texts.join(list, Entity::getDisplayName);
    }
}

