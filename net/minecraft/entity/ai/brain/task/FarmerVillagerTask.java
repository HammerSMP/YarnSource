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
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;

public class FarmerVillagerTask
extends Task<VillagerEntity> {
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
    private final List<BlockPos> targetPositions = Lists.newArrayList();

    public FarmerVillagerTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.SECONDARY_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (!arg.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if (arg2.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        BlockPos.Mutable lv = arg2.getBlockPos().mutableCopy();
        this.targetPositions.clear();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    lv.set(arg2.getX() + (double)i, arg2.getY() + (double)j, arg2.getZ() + (double)k);
                    if (!this.isSuitableTarget(lv, arg)) continue;
                    this.targetPositions.add(new BlockPos(lv));
                }
            }
        }
        this.currentTarget = this.chooseRandomTarget(arg);
        return this.currentTarget != null;
    }

    @Nullable
    private BlockPos chooseRandomTarget(ServerWorld arg) {
        return this.targetPositions.isEmpty() ? null : this.targetPositions.get(arg.getRandom().nextInt(this.targetPositions.size()));
    }

    private boolean isSuitableTarget(BlockPos arg, ServerWorld arg2) {
        BlockState lv = arg2.getBlockState(arg);
        Block lv2 = lv.getBlock();
        Block lv3 = arg2.getBlockState(arg.down()).getBlock();
        return lv2 instanceof CropBlock && ((CropBlock)lv2).isMature(lv) || lv.isAir() && lv3 instanceof FarmlandBlock;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            arg2.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5f, 1));
        }
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        arg2.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        arg2.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        if (this.currentTarget != null && !this.currentTarget.isWithinDistance(arg2.getPos(), 1.0)) {
            return;
        }
        if (this.currentTarget != null && l > this.nextResponseTime) {
            BlockState lv = arg.getBlockState(this.currentTarget);
            Block lv2 = lv.getBlock();
            Block lv3 = arg.getBlockState(this.currentTarget.down()).getBlock();
            if (lv2 instanceof CropBlock && ((CropBlock)lv2).isMature(lv)) {
                arg.breakBlock(this.currentTarget, true, arg2);
            }
            if (lv.isAir() && lv3 instanceof FarmlandBlock && arg2.hasSeedToPlant()) {
                SimpleInventory lv4 = arg2.getInventory();
                for (int i = 0; i < lv4.size(); ++i) {
                    ItemStack lv5 = lv4.getStack(i);
                    boolean bl = false;
                    if (!lv5.isEmpty()) {
                        if (lv5.getItem() == Items.WHEAT_SEEDS) {
                            arg.setBlockState(this.currentTarget, Blocks.WHEAT.getDefaultState(), 3);
                            bl = true;
                        } else if (lv5.getItem() == Items.POTATO) {
                            arg.setBlockState(this.currentTarget, Blocks.POTATOES.getDefaultState(), 3);
                            bl = true;
                        } else if (lv5.getItem() == Items.CARROT) {
                            arg.setBlockState(this.currentTarget, Blocks.CARROTS.getDefaultState(), 3);
                            bl = true;
                        } else if (lv5.getItem() == Items.BEETROOT_SEEDS) {
                            arg.setBlockState(this.currentTarget, Blocks.BEETROOTS.getDefaultState(), 3);
                            bl = true;
                        }
                    }
                    if (!bl) continue;
                    arg.playSound(null, (double)this.currentTarget.getX(), (double)this.currentTarget.getY(), this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    lv5.decrement(1);
                    if (!lv5.isEmpty()) break;
                    lv4.setStack(i, ItemStack.EMPTY);
                    break;
                }
            }
            if (lv2 instanceof CropBlock && !((CropBlock)lv2).isMature(lv)) {
                this.targetPositions.remove(this.currentTarget);
                this.currentTarget = this.chooseRandomTarget(arg);
                if (this.currentTarget != null) {
                    this.nextResponseTime = l + 20L;
                    arg2.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5f, 1));
                    arg2.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                }
            }
        }
        ++this.ticksRan;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.ticksRan < 200;
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
}

