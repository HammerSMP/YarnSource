/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class CatSpawner
implements Spawner {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld arg, boolean bl, boolean bl2) {
        Random random;
        if (!bl2 || !arg.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return 0;
        }
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn = 1200;
        ServerPlayerEntity lv = arg.getRandomAlivePlayer();
        if (lv == null) {
            return 0;
        }
        int i = (8 + random.nextInt(24)) * ((random = arg.random).nextBoolean() ? -1 : 1);
        int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos lv2 = lv.getBlockPos().add(i, 0, j);
        if (!arg.isRegionLoaded(lv2.getX() - 10, lv2.getY() - 10, lv2.getZ() - 10, lv2.getX() + 10, lv2.getY() + 10, lv2.getZ() + 10)) {
            return 0;
        }
        if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, arg, lv2, EntityType.CAT)) {
            if (arg.isNearOccupiedPointOfInterest(lv2, 2)) {
                return this.spawnInHouse(arg, lv2);
            }
            if (arg.getStructureAccessor().method_28388(lv2, true, StructureFeature.SWAMP_HUT).hasChildren()) {
                return this.spawnInSwampHut(arg, lv2);
            }
        }
        return 0;
    }

    private int spawnInHouse(ServerWorld arg, BlockPos arg2) {
        List<CatEntity> list;
        int i = 48;
        if (arg.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), arg2, 48, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED) > 4L && (list = arg.getNonSpectatingEntities(CatEntity.class, new Box(arg2).expand(48.0, 8.0, 48.0))).size() < 5) {
            return this.spawn(arg2, arg);
        }
        return 0;
    }

    private int spawnInSwampHut(World arg, BlockPos arg2) {
        int i = 16;
        List<CatEntity> list = arg.getNonSpectatingEntities(CatEntity.class, new Box(arg2).expand(16.0, 8.0, 16.0));
        if (list.size() < 1) {
            return this.spawn(arg2, arg);
        }
        return 0;
    }

    private int spawn(BlockPos arg, World arg2) {
        CatEntity lv = EntityType.CAT.create(arg2);
        if (lv == null) {
            return 0;
        }
        lv.initialize(arg2, arg2.getLocalDifficulty(arg), SpawnReason.NATURAL, null, null);
        lv.refreshPositionAndAngles(arg, 0.0f, 0.0f);
        arg2.spawnEntity(lv);
        return 1;
    }
}

