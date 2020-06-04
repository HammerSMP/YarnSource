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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Spawner;

public class PillagerSpawner
implements Spawner {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld arg, boolean bl, boolean bl2) {
        if (!bl) {
            return 0;
        }
        if (!arg.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
            return 0;
        }
        Random random = arg.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += 12000 + random.nextInt(1200);
        long l = arg.getTimeOfDay() / 24000L;
        if (l < 5L || !arg.isDay()) {
            return 0;
        }
        if (random.nextInt(5) != 0) {
            return 0;
        }
        int i = arg.getPlayers().size();
        if (i < 1) {
            return 0;
        }
        PlayerEntity lv = arg.getPlayers().get(random.nextInt(i));
        if (lv.isSpectator()) {
            return 0;
        }
        if (arg.isNearOccupiedPointOfInterest(lv.getBlockPos(), 2)) {
            return 0;
        }
        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.Mutable lv2 = lv.getBlockPos().mutableCopy().move(j, 0, k);
        if (!arg.isRegionLoaded(lv2.getX() - 10, lv2.getY() - 10, lv2.getZ() - 10, lv2.getX() + 10, lv2.getY() + 10, lv2.getZ() + 10)) {
            return 0;
        }
        Biome lv3 = arg.getBiome(lv2);
        Biome.Category lv4 = lv3.getCategory();
        if (lv4 == Biome.Category.MUSHROOM) {
            return 0;
        }
        int m = 0;
        int n = (int)Math.ceil(arg.getLocalDifficulty(lv2).getLocalDifficulty()) + 1;
        for (int o = 0; o < n; ++o) {
            ++m;
            lv2.setY(arg.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv2).getY());
            if (o == 0) {
                if (!this.spawnOneEntity(arg, lv2, random, true)) {
                    break;
                }
            } else {
                this.spawnOneEntity(arg, lv2, random, false);
            }
            lv2.setX(lv2.getX() + random.nextInt(5) - random.nextInt(5));
            lv2.setZ(lv2.getZ() + random.nextInt(5) - random.nextInt(5));
        }
        return m;
    }

    private boolean spawnOneEntity(World arg, BlockPos arg2, Random random, boolean bl) {
        BlockState lv = arg.getBlockState(arg2);
        if (!SpawnHelper.isClearForSpawn(arg, arg2, lv, lv.getFluidState(), EntityType.PILLAGER)) {
            return false;
        }
        if (!PatrolEntity.canSpawn(EntityType.PILLAGER, arg, SpawnReason.PATROL, arg2, random)) {
            return false;
        }
        PatrolEntity lv2 = EntityType.PILLAGER.create(arg);
        if (lv2 != null) {
            if (bl) {
                lv2.setPatrolLeader(true);
                lv2.setRandomPatrolTarget();
            }
            lv2.updatePosition(arg2.getX(), arg2.getY(), arg2.getZ());
            lv2.initialize(arg, arg.getLocalDifficulty(arg2), SpawnReason.PATROL, null, null);
            arg.spawnEntity(lv2);
            return true;
        }
        return false;
    }
}

