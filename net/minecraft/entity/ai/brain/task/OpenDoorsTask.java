/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class OpenDoorsTask
extends Task<LivingEntity> {
    public OpenDoorsTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.PATH, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.INTERACTABLE_DOORS, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.OPENED_DOORS, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> lv = entity.getBrain();
        Path lv2 = lv.getOptionalMemory(MemoryModuleType.PATH).get();
        List<GlobalPos> list = lv.getOptionalMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
        List<BlockPos> list2 = lv2.getNodes().stream().map(arg -> new BlockPos(arg.x, arg.y, arg.z)).collect(Collectors.toList());
        Set<BlockPos> set = this.getDoorsOnPath(world, list, list2);
        int i = lv2.getCurrentNodeIndex() - 1;
        this.findAndCloseOpenedDoors(world, list2, set, i, entity, lv);
    }

    private Set<BlockPos> getDoorsOnPath(ServerWorld world, List<GlobalPos> doors, List<BlockPos> path) {
        return doors.stream().filter(arg2 -> arg2.getDimension() == world.getRegistryKey()).map(GlobalPos::getPos).filter(path::contains).collect(Collectors.toSet());
    }

    private void findAndCloseOpenedDoors(ServerWorld world, List<BlockPos> path, Set<BlockPos> doors, int lastNodeIndex, LivingEntity entity, Brain<?> brain) {
        doors.forEach(arg3 -> {
            int j = path.indexOf(arg3);
            BlockState lv = world.getBlockState((BlockPos)arg3);
            Block lv2 = lv.getBlock();
            if (BlockTags.WOODEN_DOORS.contains(lv2) && lv2 instanceof DoorBlock) {
                boolean bl = j >= lastNodeIndex;
                ((DoorBlock)lv2).setOpen(world, (BlockPos)arg3, bl);
                GlobalPos lv3 = GlobalPos.create(world.getRegistryKey(), arg3);
                if (!brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).isPresent() && bl) {
                    brain.remember(MemoryModuleType.OPENED_DOORS, Sets.newHashSet((Object[])new GlobalPos[]{lv3}));
                } else {
                    brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
                        if (bl) {
                            set.add(lv3);
                        } else {
                            set.remove(lv3);
                        }
                    });
                }
            }
        });
        OpenDoorsTask.closeOpenedDoors(world, path, lastNodeIndex, entity, brain);
    }

    public static void closeOpenedDoors(ServerWorld world, List<BlockPos> path, int currentPathIndex, LivingEntity entity, Brain<?> brain) {
        brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                GlobalPos lv = (GlobalPos)iterator.next();
                BlockPos lv2 = lv.getPos();
                int j = path.indexOf(lv2);
                if (world.getRegistryKey() != lv.getDimension()) {
                    iterator.remove();
                    continue;
                }
                BlockState lv3 = world.getBlockState(lv2);
                Block lv4 = lv3.getBlock();
                if (!BlockTags.WOODEN_DOORS.contains(lv4) || !(lv4 instanceof DoorBlock) || j >= currentPathIndex || !lv2.isWithinDistance(entity.getPos(), 4.0)) continue;
                ((DoorBlock)lv4).setOpen(world, lv2, false);
                iterator.remove();
            }
        });
    }
}

