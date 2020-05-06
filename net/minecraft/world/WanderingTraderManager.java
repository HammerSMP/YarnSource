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
import net.minecraft.class_5268;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WanderingTraderManager {
    private final Random random = new Random();
    private final ServerWorld world;
    private final class_5268 field_24387;
    private int spawnTimer;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderManager(ServerWorld arg, class_5268 arg2) {
        this.world = arg;
        this.field_24387 = arg2;
        this.spawnTimer = 1200;
        this.spawnDelay = arg2.getWanderingTraderSpawnDelay();
        this.spawnChance = arg2.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            arg2.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            arg2.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    public void tick() {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            return;
        }
        if (--this.spawnTimer > 0) {
            return;
        }
        this.spawnTimer = 1200;
        this.spawnDelay -= 1200;
        this.field_24387.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return;
        }
        this.spawnDelay = 24000;
        if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return;
        }
        int i = this.spawnChance;
        this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
        this.field_24387.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > i) {
            return;
        }
        if (this.method_18018()) {
            this.spawnChance = 25;
        }
    }

    private boolean method_18018() {
        ServerPlayerEntity lv = this.world.getRandomAlivePlayer();
        if (lv == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos lv2 = lv.getBlockPos();
        int i = 48;
        PointOfInterestStorage lv3 = this.world.getPointOfInterestStorage();
        Optional<BlockPos> optional = lv3.getPosition(PointOfInterestType.MEETING.getCompletionCondition(), arg -> true, lv2, 48, PointOfInterestStorage.OccupationStatus.ANY);
        BlockPos lv4 = optional.orElse(lv2);
        BlockPos lv5 = this.getNearbySpawnPos(lv4, 48);
        if (lv5 != null && this.wontSuffocateAt(lv5)) {
            if (this.world.getBiome(lv5) == Biomes.THE_VOID) {
                return false;
            }
            WanderingTraderEntity lv6 = EntityType.WANDERING_TRADER.spawn(this.world, null, null, null, lv5, SpawnType.EVENT, false, false);
            if (lv6 != null) {
                for (int j = 0; j < 2; ++j) {
                    this.spawnLlama(lv6, 4);
                }
                this.field_24387.setWanderingTraderId(lv6.getUuid());
                lv6.setDespawnDelay(48000);
                lv6.setWanderTarget(lv4);
                lv6.setPositionTarget(lv4, 16);
                return true;
            }
        }
        return false;
    }

    private void spawnLlama(WanderingTraderEntity arg, int i) {
        BlockPos lv = this.getNearbySpawnPos(arg.getBlockPos(), i);
        if (lv == null) {
            return;
        }
        TraderLlamaEntity lv2 = EntityType.TRADER_LLAMA.spawn(this.world, null, null, null, lv, SpawnType.EVENT, false, false);
        if (lv2 == null) {
            return;
        }
        lv2.attachLeash(arg, true);
    }

    @Nullable
    private BlockPos getNearbySpawnPos(BlockPos arg, int i) {
        BlockPos lv = null;
        for (int j = 0; j < 10; ++j) {
            int l;
            int m;
            int k = arg.getX() + this.random.nextInt(i * 2) - i;
            BlockPos lv2 = new BlockPos(k, m = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l = arg.getZ() + this.random.nextInt(i * 2) - i), l);
            if (!SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, lv2, EntityType.WANDERING_TRADER)) continue;
            lv = lv2;
            break;
        }
        return lv;
    }

    private boolean wontSuffocateAt(BlockPos arg) {
        for (BlockPos lv : BlockPos.iterate(arg, arg.add(1, 2, 1))) {
            if (this.world.getBlockState(lv).getCollisionShape(this.world, lv).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

