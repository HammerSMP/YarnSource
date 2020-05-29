/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class FarmerWorkTask
extends VillagerWorkTask {
    private static final List<Item> COMPOSTABLES = ImmutableList.of((Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS);

    @Override
    protected void performAdditionalWork(ServerWorld arg, VillagerEntity arg2) {
        Optional<GlobalPos> optional = arg2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return;
        }
        GlobalPos lv = optional.get();
        BlockState lv2 = arg.getBlockState(lv.getPos());
        if (lv2.isOf(Blocks.COMPOSTER)) {
            this.craftAndDropBread(arg2);
            this.compostSeeds(arg, arg2, lv, lv2);
        }
    }

    private void compostSeeds(ServerWorld arg, VillagerEntity arg2, GlobalPos arg3, BlockState arg4) {
        if (arg4.get(ComposterBlock.LEVEL) == 8) {
            arg4 = ComposterBlock.emptyFullComposter(arg4, arg, arg3.getPos());
        }
        int i = 20;
        int j = 10;
        int[] is = new int[COMPOSTABLES.size()];
        SimpleInventory lv = arg2.getInventory();
        int k = lv.size();
        for (int l = k - 1; l >= 0 && i > 0; --l) {
            int o;
            ItemStack lv2 = lv.getStack(l);
            int m = COMPOSTABLES.indexOf(lv2.getItem());
            if (m == -1) continue;
            int n = lv2.getCount();
            is[m] = o = is[m] + n;
            int p = Math.min(Math.min(o - 10, i), n);
            if (p <= 0) continue;
            i -= p;
            for (int q = 0; q < p; ++q) {
                if ((arg4 = ComposterBlock.compost(arg4, arg, lv2, arg3.getPos())).get(ComposterBlock.LEVEL) != 7) continue;
                return;
            }
        }
    }

    private void craftAndDropBread(VillagerEntity arg) {
        SimpleInventory lv = arg.getInventory();
        if (lv.count(Items.BREAD) > 36) {
            return;
        }
        int i = lv.count(Items.WHEAT);
        int j = 3;
        int k = 3;
        int l = Math.min(3, i / 3);
        if (l == 0) {
            return;
        }
        int m = l * 3;
        lv.removeItem(Items.WHEAT, m);
        ItemStack lv2 = lv.addStack(new ItemStack(Items.BREAD, l));
        if (!lv2.isEmpty()) {
            arg.dropStack(lv2, 0.5f);
        }
    }
}

