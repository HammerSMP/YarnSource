/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;

public class CelebrateRaidWinTask
extends Task<VillagerEntity> {
    @Nullable
    private Raid raid;

    public CelebrateRaidWinTask(int minRunTime, int maxRunTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        BlockPos lv = arg2.getBlockPos();
        this.raid = arg.getRaidAt(lv);
        return this.raid != null && this.raid.hasWon() && SeekSkyTask.isSkyVisible(arg, arg2, lv);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.raid != null && !this.raid.hasStopped();
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        this.raid = null;
        arg2.getBrain().refreshActivities(arg.getTimeOfDay(), arg.getTime());
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        Random random = arg2.getRandom();
        if (random.nextInt(100) == 0) {
            arg2.playCelebrateSound();
        }
        if (random.nextInt(200) == 0 && SeekSkyTask.isSkyVisible(arg, arg2, arg2.getBlockPos())) {
            DyeColor lv = Util.getRandom(DyeColor.values(), random);
            int i = random.nextInt(3);
            ItemStack lv2 = this.createFirework(lv, i);
            FireworkRocketEntity lv3 = new FireworkRocketEntity(arg2.world, arg2, arg2.getX(), arg2.getEyeY(), arg2.getZ(), lv2);
            arg2.world.spawnEntity(lv3);
        }
    }

    private ItemStack createFirework(DyeColor color, int flight) {
        ItemStack lv = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack lv2 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag lv3 = lv2.getOrCreateSubTag("Explosion");
        ArrayList list = Lists.newArrayList();
        list.add(color.getFireworkColor());
        lv3.putIntArray("Colors", list);
        lv3.putByte("Type", (byte)FireworkItem.Type.BURST.getId());
        CompoundTag lv4 = lv.getOrCreateSubTag("Fireworks");
        ListTag lv5 = new ListTag();
        CompoundTag lv6 = lv2.getSubTag("Explosion");
        if (lv6 != null) {
            lv5.add(lv6);
        }
        lv4.putByte("Flight", (byte)flight);
        if (!lv5.isEmpty()) {
            lv4.put("Explosions", lv5);
        }
        return lv;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }
}

