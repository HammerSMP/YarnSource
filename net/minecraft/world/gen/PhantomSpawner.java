/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_5304;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;

public class PhantomSpawner
implements class_5304 {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld arg, boolean bl, boolean bl2) {
        if (!bl) {
            return 0;
        }
        if (!arg.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
            return 0;
        }
        Random random = arg.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += (60 + random.nextInt(60)) * 20;
        if (arg.getAmbientDarkness() < 5 && arg.getDimension().hasSkyLight()) {
            return 0;
        }
        int i = 0;
        for (PlayerEntity playerEntity : arg.getPlayers()) {
            FluidState lv7;
            BlockState lv6;
            BlockPos lv5;
            LocalDifficulty lv3;
            if (playerEntity.isSpectator()) continue;
            BlockPos lv2 = playerEntity.getBlockPos();
            if (arg.getDimension().hasSkyLight() && (lv2.getY() < arg.getSeaLevel() || !arg.isSkyVisible(lv2)) || !(lv3 = arg.getLocalDifficulty(lv2)).isHarderThan(random.nextFloat() * 3.0f)) continue;
            ServerStatHandler lv4 = ((ServerPlayerEntity)playerEntity).getStatHandler();
            int j = MathHelper.clamp(lv4.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            int k = 24000;
            if (random.nextInt(j) < 72000 || !SpawnHelper.isClearForSpawn(arg, lv5 = lv2.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21)), lv6 = arg.getBlockState(lv5), lv7 = arg.getFluidState(lv5), EntityType.PHANTOM)) continue;
            EntityData lv8 = null;
            int l = 1 + random.nextInt(lv3.getGlobalDifficulty().getId() + 1);
            for (int m = 0; m < l; ++m) {
                PhantomEntity lv9 = EntityType.PHANTOM.create(arg);
                lv9.refreshPositionAndAngles(lv5, 0.0f, 0.0f);
                lv8 = lv9.initialize(arg, lv3, SpawnReason.NATURAL, lv8, null);
                arg.spawnEntity(lv9);
            }
            i += l;
        }
        return i;
    }
}

