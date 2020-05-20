/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BonusChestFeature
extends Feature<DefaultFeatureConfig> {
    public BonusChestFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        ChunkPos lv = new ChunkPos(arg4);
        List list = IntStream.rangeClosed(lv.getStartX(), lv.getEndX()).boxed().collect(Collectors.toList());
        Collections.shuffle(list, random);
        List list2 = IntStream.rangeClosed(lv.getStartZ(), lv.getEndZ()).boxed().collect(Collectors.toList());
        Collections.shuffle(list2, random);
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (Integer integer : list) {
            for (Integer integer2 : list2) {
                lv2.set(integer, 0, integer2);
                BlockPos lv3 = arg.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv2);
                if (!arg.isAir(lv3) && !arg.getBlockState(lv3).getCollisionShape(arg, lv3).isEmpty()) continue;
                arg.setBlockState(lv3, Blocks.CHEST.getDefaultState(), 2);
                LootableContainerBlockEntity.setLootTable(arg, random, lv3, LootTables.SPAWN_BONUS_CHEST);
                BlockState lv4 = Blocks.TORCH.getDefaultState();
                for (Direction lv5 : Direction.Type.HORIZONTAL) {
                    BlockPos lv6 = lv3.offset(lv5);
                    if (!lv4.canPlaceAt(arg, lv6)) continue;
                    arg.setBlockState(lv6, lv4, 2);
                }
                return true;
            }
        }
        return false;
    }
}

