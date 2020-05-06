/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BoneMealTask
extends Task<VillagerEntity> {
    private long startTime;
    private long lastEndEntityAge;
    private int duration;
    private Optional<BlockPos> pos = Optional.empty();

    public BoneMealTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (arg2.age % 10 != 0 || this.lastEndEntityAge != 0L && this.lastEndEntityAge + 160L > (long)arg2.age) {
            return false;
        }
        if (arg2.getInventory().count(Items.BONE_MEAL) <= 0) {
            return false;
        }
        this.pos = this.findBoneMealPos(arg, arg2);
        return this.pos.isPresent();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.duration < 80 && this.pos.isPresent();
    }

    private Optional<BlockPos> findBoneMealPos(ServerWorld arg, VillagerEntity arg2) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        Optional<BlockPos> optional = Optional.empty();
        int i = 0;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    lv.set(arg2.getBlockPos(), j, k, l);
                    if (!this.canBoneMeal(lv, arg) || arg.random.nextInt(++i) != 0) continue;
                    optional = Optional.of(lv.toImmutable());
                }
            }
        }
        return optional;
    }

    private boolean canBoneMeal(BlockPos arg, ServerWorld arg2) {
        BlockState lv = arg2.getBlockState(arg);
        Block lv2 = lv.getBlock();
        return lv2 instanceof CropBlock && !((CropBlock)lv2).isMature(lv);
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        this.addLookWalkTargets(arg2);
        arg2.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.startTime = l;
        this.duration = 0;
    }

    private void addLookWalkTargets(VillagerEntity arg) {
        this.pos.ifPresent(arg2 -> {
            BlockPosLookTarget lv = new BlockPosLookTarget((BlockPos)arg2);
            arg.getBrain().remember(MemoryModuleType.LOOK_TARGET, lv);
            arg.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lv, 0.5f, 1));
        });
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        arg2.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.lastEndEntityAge = arg2.age;
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        BlockPos lv = this.pos.get();
        if (l < this.startTime || !lv.isWithinDistance(arg2.getPos(), 1.0)) {
            return;
        }
        ItemStack lv2 = ItemStack.EMPTY;
        BasicInventory lv3 = arg2.getInventory();
        int i = lv3.size();
        for (int j = 0; j < i; ++j) {
            ItemStack lv4 = lv3.getStack(j);
            if (lv4.getItem() != Items.BONE_MEAL) continue;
            lv2 = lv4;
            break;
        }
        if (!lv2.isEmpty() && BoneMealItem.useOnFertilizable(lv2, arg, lv)) {
            arg.syncWorldEvent(2005, lv, 0);
            this.pos = this.findBoneMealPos(arg, arg2);
            this.addLookWalkTargets(arg2);
            this.startTime = l + 40L;
        }
        ++this.duration;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

