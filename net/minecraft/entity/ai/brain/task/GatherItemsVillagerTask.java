/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;

public class GatherItemsVillagerTask
extends Task<VillagerEntity> {
    private Set<Item> items = ImmutableSet.of();

    public GatherItemsVillagerTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        return LookTargetUtil.canSee(arg2.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.shouldRun(arg, arg2);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        VillagerEntity lv = (VillagerEntity)arg2.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, 0.5f);
        this.items = GatherItemsVillagerTask.getGatherableItems(arg2, lv);
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        VillagerEntity lv = (VillagerEntity)arg2.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (arg2.squaredDistanceTo(lv) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(arg2, lv, 0.5f);
        arg2.talkWithVillager(lv, l);
        if (arg2.wantsToStartBreeding() && (arg2.getVillagerData().getProfession() == VillagerProfession.FARMER || lv.canBreed())) {
            GatherItemsVillagerTask.giveHalfOfStack(arg2, VillagerEntity.ITEM_FOOD_VALUES.keySet(), lv);
        }
        if (lv.getVillagerData().getProfession() == VillagerProfession.FARMER && arg2.getInventory().count(Items.WHEAT) > Items.WHEAT.getMaxCount() / 2) {
            GatherItemsVillagerTask.giveHalfOfStack(arg2, (Set<Item>)ImmutableSet.of((Object)Items.WHEAT), lv);
        }
        if (!this.items.isEmpty() && arg2.getInventory().containsAny(this.items)) {
            GatherItemsVillagerTask.giveHalfOfStack(arg2, this.items, lv);
        }
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        arg2.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(VillagerEntity arg2, VillagerEntity arg22) {
        ImmutableSet<Item> immutableSet = arg22.getVillagerData().getProfession().getGatherableItems();
        ImmutableSet<Item> immutableSet2 = arg2.getVillagerData().getProfession().getGatherableItems();
        return immutableSet.stream().filter(arg -> !immutableSet2.contains(arg)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(VillagerEntity arg, Set<Item> set, LivingEntity arg2) {
        SimpleInventory lv = arg.getInventory();
        ItemStack lv2 = ItemStack.EMPTY;
        for (int i = 0; i < lv.size(); ++i) {
            int k;
            Item lv4;
            ItemStack lv3 = lv.getStack(i);
            if (lv3.isEmpty() || !set.contains(lv4 = lv3.getItem())) continue;
            if (lv3.getCount() > lv3.getMaxCount() / 2) {
                int j = lv3.getCount() / 2;
            } else {
                if (lv3.getCount() <= 24) continue;
                k = lv3.getCount() - 24;
            }
            lv3.decrement(k);
            lv2 = new ItemStack(lv4, k);
            break;
        }
        if (!lv2.isEmpty()) {
            LookTargetUtil.give(arg, lv2, arg2.getPos());
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

