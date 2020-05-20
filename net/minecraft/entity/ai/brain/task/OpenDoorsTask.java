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
    protected void run(ServerWorld arg2, LivingEntity arg22, long l) {
        Brain<?> lv = arg22.getBrain();
        Path lv2 = lv.getOptionalMemory(MemoryModuleType.PATH).get();
        List<GlobalPos> list = lv.getOptionalMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
        List<BlockPos> list2 = lv2.getNodes().stream().map(arg -> new BlockPos(arg.x, arg.y, arg.z)).collect(Collectors.toList());
        Set<BlockPos> set = this.getDoorsOnPath(arg2, list, list2);
        int i = lv2.getCurrentNodeIndex() - 1;
        this.findAndCloseOpenedDoors(arg2, list2, set, i, arg22, lv);
    }

    private Set<BlockPos> getDoorsOnPath(ServerWorld arg, List<GlobalPos> list, List<BlockPos> list2) {
        return list.stream().filter(arg2 -> arg2.getDimension() == arg.method_27983()).map(GlobalPos::getPos).filter(list2::contains).collect(Collectors.toSet());
    }

    private void findAndCloseOpenedDoors(ServerWorld arg, List<BlockPos> list, Set<BlockPos> set, int i, LivingEntity arg2, Brain<?> arg32) {
        set.forEach(arg3 -> {
            int j = list.indexOf(arg3);
            BlockState lv = arg.getBlockState((BlockPos)arg3);
            Block lv2 = lv.getBlock();
            if (BlockTags.WOODEN_DOORS.contains(lv2) && lv2 instanceof DoorBlock) {
                boolean bl = j >= i;
                ((DoorBlock)lv2).setOpen(arg, (BlockPos)arg3, bl);
                GlobalPos lv3 = GlobalPos.create(arg.method_27983(), arg3);
                if (!arg32.getOptionalMemory(MemoryModuleType.OPENED_DOORS).isPresent() && bl) {
                    arg32.remember(MemoryModuleType.OPENED_DOORS, Sets.newHashSet((Object[])new GlobalPos[]{lv3}));
                } else {
                    arg32.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
                        if (bl) {
                            set.add(lv3);
                        } else {
                            set.remove(lv3);
                        }
                    });
                }
            }
        });
        OpenDoorsTask.closeOpenedDoors(arg, list, i, arg2, arg32);
    }

    public static void closeOpenedDoors(ServerWorld arg, List<BlockPos> list, int i, LivingEntity arg2, Brain<?> arg3) {
        arg3.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                GlobalPos lv = (GlobalPos)iterator.next();
                BlockPos lv2 = lv.getPos();
                int j = list.indexOf(lv2);
                if (arg.method_27983() != lv.getDimension()) {
                    iterator.remove();
                    continue;
                }
                BlockState lv3 = arg.getBlockState(lv2);
                Block lv4 = lv3.getBlock();
                if (!BlockTags.WOODEN_DOORS.contains(lv4) || !(lv4 instanceof DoorBlock) || j >= i || !lv2.isWithinDistance(arg2.getPos(), 4.0)) continue;
                ((DoorBlock)lv4).setOpen(arg, lv2, false);
                iterator.remove();
            }
        });
    }
}

