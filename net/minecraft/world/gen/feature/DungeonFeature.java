/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature
extends Feature<DefaultFeatureConfig> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityType<?>[] MOB_SPAWNER_ENTITIES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

    public DungeonFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        int i = 3;
        int j = random.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int m = -1;
        int n = 4;
        int o = random.nextInt(2) + 2;
        int p = -o - 1;
        int q = o + 1;
        int r = 0;
        for (int s = k; s <= l; ++s) {
            for (int t = -1; t <= 4; ++t) {
                for (int u = p; u <= q; ++u) {
                    BlockPos lv = arg4.add(s, t, u);
                    Material lv2 = arg.getBlockState(lv).getMaterial();
                    boolean bl = lv2.isSolid();
                    if (t == -1 && !bl) {
                        return false;
                    }
                    if (t == 4 && !bl) {
                        return false;
                    }
                    if (s != k && s != l && u != p && u != q || t != 0 || !arg.isAir(lv) || !arg.isAir(lv.up())) continue;
                    ++r;
                }
            }
        }
        if (r < 1 || r > 5) {
            return false;
        }
        for (int v = k; v <= l; ++v) {
            for (int w = 3; w >= -1; --w) {
                for (int x = p; x <= q; ++x) {
                    BlockPos lv3 = arg4.add(v, w, x);
                    if (v == k || w == -1 || x == p || v == l || w == 4 || x == q) {
                        if (lv3.getY() >= 0 && !arg.getBlockState(lv3.down()).getMaterial().isSolid()) {
                            arg.setBlockState(lv3, AIR, 2);
                            continue;
                        }
                        if (!arg.getBlockState(lv3).getMaterial().isSolid() || arg.getBlockState(lv3).isOf(Blocks.CHEST)) continue;
                        if (w == -1 && random.nextInt(4) != 0) {
                            arg.setBlockState(lv3, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                            continue;
                        }
                        arg.setBlockState(lv3, Blocks.COBBLESTONE.getDefaultState(), 2);
                        continue;
                    }
                    if (arg.getBlockState(lv3).isOf(Blocks.CHEST)) continue;
                    arg.setBlockState(lv3, AIR, 2);
                }
            }
        }
        block6: for (int y = 0; y < 2; ++y) {
            for (int z = 0; z < 3; ++z) {
                int ac;
                int ab;
                int aa = arg4.getX() + random.nextInt(j * 2 + 1) - j;
                BlockPos lv4 = new BlockPos(aa, ab = arg4.getY(), ac = arg4.getZ() + random.nextInt(o * 2 + 1) - o);
                if (!arg.isAir(lv4)) continue;
                int ad = 0;
                for (Direction lv5 : Direction.Type.HORIZONTAL) {
                    if (!arg.getBlockState(lv4.offset(lv5)).getMaterial().isSolid()) continue;
                    ++ad;
                }
                if (ad != 1) continue;
                arg.setBlockState(lv4, StructurePiece.method_14916(arg, lv4, Blocks.CHEST.getDefaultState()), 2);
                LootableContainerBlockEntity.setLootTable(arg, random, lv4, LootTables.SIMPLE_DUNGEON_CHEST);
                continue block6;
            }
        }
        arg.setBlockState(arg4, Blocks.SPAWNER.getDefaultState(), 2);
        BlockEntity lv6 = arg.getBlockEntity(arg4);
        if (lv6 instanceof MobSpawnerBlockEntity) {
            ((MobSpawnerBlockEntity)lv6).getLogic().setEntityId(this.getMobSpawnerEntity(random));
        } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", (Object)arg4.getX(), (Object)arg4.getY(), (Object)arg4.getZ());
        }
        return true;
    }

    private EntityType<?> getMobSpawnerEntity(Random random) {
        return Util.getRandom(MOB_SPAWNER_ENTITIES, random);
    }
}

