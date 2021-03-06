/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WanderingTraderManager
implements Spawner {
    private final Random random = new Random();
    private final ServerWorldProperties properties;
    private int spawnTimer;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderManager(ServerWorldProperties properties) {
        this.properties = properties;
        this.spawnTimer = 1200;
        this.spawnDelay = properties.getWanderingTraderSpawnDelay();
        this.spawnChance = properties.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            properties.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            properties.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            return 0;
        }
        if (--this.spawnTimer > 0) {
            return 0;
        }
        this.spawnTimer = 1200;
        this.spawnDelay -= 1200;
        this.properties.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return 0;
        }
        this.spawnDelay = 24000;
        if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return 0;
        }
        int i = this.spawnChance;
        this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
        this.properties.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > i) {
            return 0;
        }
        if (this.method_18018(world)) {
            this.spawnChance = 25;
            return 1;
        }
        return 0;
    }

    private boolean method_18018(ServerWorld arg2) {
        ServerPlayerEntity lv = arg2.getRandomAlivePlayer();
        if (lv == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos lv2 = lv.getBlockPos();
        int i = 48;
        PointOfInterestStorage lv3 = arg2.getPointOfInterestStorage();
        Optional<BlockPos> optional = lv3.getPosition(PointOfInterestType.MEETING.getCompletionCondition(), arg -> true, lv2, 48, PointOfInterestStorage.OccupationStatus.ANY);
        BlockPos lv4 = optional.orElse(lv2);
        BlockPos lv5 = this.getNearbySpawnPos(arg2, lv4, 48);
        if (lv5 != null && this.doesNotSuffocateAt(arg2, lv5)) {
            if (arg2.getBiome(lv5) == Biomes.THE_VOID) {
                return false;
            }
            WanderingTraderEntity lv6 = EntityType.WANDERING_TRADER.spawn(arg2, null, null, null, lv5, SpawnReason.EVENT, false, false);
            if (lv6 != null) {
                for (int j = 0; j < 2; ++j) {
                    this.spawnLlama(arg2, lv6, 4);
                }
                this.properties.setWanderingTraderId(lv6.getUuid());
                lv6.setDespawnDelay(48000);
                lv6.setWanderTarget(lv4);
                lv6.setPositionTarget(lv4, 16);
                return true;
            }
        }
        return false;
    }

    private void spawnLlama(ServerWorld arg, WanderingTraderEntity arg2, int i) {
        BlockPos lv = this.getNearbySpawnPos(arg, arg2.getBlockPos(), i);
        if (lv == null) {
            return;
        }
        TraderLlamaEntity lv2 = EntityType.TRADER_LLAMA.spawn(arg, null, null, null, lv, SpawnReason.EVENT, false, false);
        if (lv2 == null) {
            return;
        }
        lv2.attachLeash(arg2, true);
    }

    @Nullable
    private BlockPos getNearbySpawnPos(WorldView arg, BlockPos arg2, int i) {
        BlockPos lv = null;
        for (int j = 0; j < 10; ++j) {
            int l;
            int m;
            int k = arg2.getX() + this.random.nextInt(i * 2) - i;
            BlockPos lv2 = new BlockPos(k, m = arg.getTopY(Heightmap.Type.WORLD_SURFACE, k, l = arg2.getZ() + this.random.nextInt(i * 2) - i), l);
            if (!SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, arg, lv2, EntityType.WANDERING_TRADER)) continue;
            lv = lv2;
            break;
        }
        return lv;
    }

    private boolean doesNotSuffocateAt(BlockView arg, BlockPos arg2) {
        for (BlockPos lv : BlockPos.iterate(arg2, arg2.add(1, 2, 1))) {
            if (arg.getBlockState(lv).getCollisionShape(arg, lv).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

