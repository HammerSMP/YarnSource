/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Spawner;

public class PillagerSpawner
implements Spawner {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        }
        if (!world.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
            return 0;
        }
        Random random = world.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += 12000 + random.nextInt(1200);
        long l = world.getTimeOfDay() / 24000L;
        if (l < 5L || !world.isDay()) {
            return 0;
        }
        if (random.nextInt(5) != 0) {
            return 0;
        }
        int i = world.getPlayers().size();
        if (i < 1) {
            return 0;
        }
        PlayerEntity lv = world.getPlayers().get(random.nextInt(i));
        if (lv.isSpectator()) {
            return 0;
        }
        if (world.isNearOccupiedPointOfInterest(lv.getBlockPos(), 2)) {
            return 0;
        }
        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.Mutable lv2 = lv.getBlockPos().mutableCopy().move(j, 0, k);
        if (!world.isRegionLoaded(lv2.getX() - 10, lv2.getY() - 10, lv2.getZ() - 10, lv2.getX() + 10, lv2.getY() + 10, lv2.getZ() + 10)) {
            return 0;
        }
        Biome lv3 = world.getBiome(lv2);
        Biome.Category lv4 = lv3.getCategory();
        if (lv4 == Biome.Category.MUSHROOM) {
            return 0;
        }
        int m = 0;
        int n = (int)Math.ceil(world.getLocalDifficulty(lv2).getLocalDifficulty()) + 1;
        for (int o = 0; o < n; ++o) {
            ++m;
            lv2.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv2).getY());
            if (o == 0) {
                if (!this.spawnPillager(world, lv2, random, true)) {
                    break;
                }
            } else {
                this.spawnPillager(world, lv2, random, false);
            }
            lv2.setX(lv2.getX() + random.nextInt(5) - random.nextInt(5));
            lv2.setZ(lv2.getZ() + random.nextInt(5) - random.nextInt(5));
        }
        return m;
    }

    private boolean spawnPillager(ServerWorld world, BlockPos pos, Random random, boolean captain) {
        BlockState lv = world.getBlockState(pos);
        if (!SpawnHelper.isClearForSpawn(world, pos, lv, lv.getFluidState(), EntityType.PILLAGER)) {
            return false;
        }
        if (!PatrolEntity.canSpawn(EntityType.PILLAGER, world, SpawnReason.PATROL, pos, random)) {
            return false;
        }
        PatrolEntity lv2 = EntityType.PILLAGER.create(world);
        if (lv2 != null) {
            if (captain) {
                lv2.setPatrolLeader(true);
                lv2.setRandomPatrolTarget();
            }
            lv2.updatePosition(pos.getX(), pos.getY(), pos.getZ());
            lv2.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, null, null);
            world.spawnEntity(lv2);
            return true;
        }
        return false;
    }
}

